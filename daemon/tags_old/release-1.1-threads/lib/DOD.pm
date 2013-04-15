package DOD;

use strict;
use warnings;
use Exporter;

use YAML::Syck;
use File::ShareDir;
use Log::Log4perl;

use DBI;
use DBD::Oracle qw(:ora_types);

use DOD::Database;
use DOD::MySQL;
use DOD::All;

use threads;
use threads::shared;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, $config_dir, $logger,
    $DSN, $DBTAG, $DATEFORMAT, $user, $password, %callback_table, $db_lock );

$VERSION     = 0.03;
@ISA         = qw(Exporter);
@EXPORT      = qw(jobDispatcher $config);
@EXPORT_OK   = ( );
%EXPORT_TAGS = ( );

# Load general configuration

BEGIN{
$config_dir = File::ShareDir::dist_dir(__PACKAGE__);
$config = LoadFile( "$config_dir/dod.conf" );
Log::Log4perl::init( "$config_dir/$config->{'LOGGER_CONFIG'}" );
$logger = Log::Log4perl::get_logger( 'DOD' );
$logger->debug( "Logger created" );
$logger->debug( "Loaded configuration from $config_dir" );
foreach my $key ( keys(%{$config}) ) {
    my %h = %{$config};
    $logger->debug( "\t$key -> $h{$key}" );
    }

share($db_lock);

} # BEGIN BLOCK

my %command_callback_table = (
    'UPGRADE' => { 'MYSQL' => \&DOD::MySQL::upgrade_callback , 'ORACLE' => undef }
);

my %state_checker_table = (
    'MYSQL' => \&DOD::MySQL::state_checker,
    'ORACLE' => undef
);


sub jobDispatcher {
    # This is neccesary because daemonizing closes all file descriptors
    Log::Log4perl::init_and_watch( "$config_dir/$config->{'LOGGER_CONFIG'}", 60 );
    my $logger = Log::Log4perl::get_logger( "DOD.jobDispatcher" );
    $logger->debug("Creating new DB connection");
    my $dbh = DOD::Database::getDBH();
    
    my @job_list;
    my @workers;

    while (1){

        $logger->debug("Checking status of connection");
        unless(defined($dbh->ping)){
            $logger->error("The connecion to the DB was lost");
            $dbh = undef;
            $logger->debug("Creating new DB connection");
            $dbh = DOD::Database::getDBH();
        }

        # Executing jobs
        $logger->debug( "Fetching job list" );
        { # Fetch $db_lock
            lock($db_lock); 
            push(@job_list, DOD::Database::getJobList($dbh));
        } # Release $db_lock
        my $pendingjobs = $#job_list + 1;
        $logger->debug( "Pending jobs: $pendingjobs" );

        foreach my $job (@job_list){
            my $tid = threads->create(\&worker, $job, undef);
            $logger->debug( "Adding thread ($tid) to list" );
            push(@workers, $tid);
            $job->{'STATE'} = 'DISPATCHED';
            # Updates job status to RUNNING
            { # Fetch $db_lock
                lock($db_lock);
                DOD::Database::updateJob($job, 'STATE', 'RUNNING', $dbh); 
            } # Release $db_lock
        }

        # Remove dispatched jobs from joblist
        $logger->debug( "Cleaning Dispatched jobs from job list. #JOBS = $pendingjobs");
        @job_list = grep( ( $_->{'STATE'} =~ 'PENDING' ), @job_list);
        $logger->debug( sprintf("Pending jobs = %d", $#job_list + 1) );
        
        # Cleaning workers pool
        
        my @running_threads = threads->list();
        my @finished_threads;
        for my $worker (@workers){
            unless (grep( $_ == $worker->tid(), @running_threads) ){
                $worker->join();    
                push(@finished_threads, $worker);
            }
        }

        my %temp = ();
        @temp{@workers} = ();
        foreach (@finished_threads) {
                delete $temp{$_};
        }
        @workers = sort keys %temp;

        # Detecting old jobs in queue
        my @timedoutjobs;
        { # Fetch $db_lock;
            lock($db_lock);
            @timedoutjobs = DOD::Database::getTimedOutJobs($dbh);
        } # Release $db_lock
        foreach my $job (@timedoutjobs){
            my $state_checker = get_state_checker($job);
            if (! defined($state_checker)){
                $logger->error( "Not state checker defined for this DB type" );
            }
            my ($job_state, $instance_state) = $state_checker->($job, 1);
            { # Fetch $db_lock;
                lock($db_lock);
                DOD::Database::finishJob( $job, $job_state, "TIMED OUT", $dbh);
                DOD::Database::updateInstance( $job, 'STATE', $instance_state, $dbh);
            } # Release $db_lock
        }
        
        # Iteration timer
        sleep 5;
    }
}

sub worker {
    my ($job, $pdbh) = @_;
    my $logger = Log::Log4perl::get_logger( "DOD.worker" );
    
    my $worker_dbh;
    if (defined($pdbh)){
        # Cloning parent process DB handler
        $worker_dbh = $pdbh->clone();
        $pdbh->{InactiveDestroy} = 1;
        undef $pdbh;
    }
    else{
        # Obtaining a new DB connector
        $worker_dbh = DOD::Database::getDBH(); 
    }
  
    my $cmd_line;
    { #Acquire $db_lock
        lock($db_lock);
        $cmd_line = DOD::Database::prepareCommand($job, $worker_dbh);
    } # Release $db_lock
    my $log;
    my $retcode;
    if (defined $cmd_line){
        my $buf;
        { #Acquire $db_lock
            lock($db_lock);
            $buf = DOD::Database::getJobParams($job, $worker_dbh);
        } # Release $db_lock
        my $entity;
        foreach (@{$buf}){
            if ($_->{'NAME'} =~ /INSTANCE_NAME/){
                $entity = $_->{'VALUE'};
                }
            }
        my $cmd =  "/etc/init.d/syscontrol -i $entity $cmd_line";
        $logger->debug( "Executing $cmd" );
        $log = `$cmd`;
        $retcode = DOD::All::result_code($log);
        $logger->debug( "Finishing Job. Return code: $retcode");
        { #Acquire $db_lock
            lock($db_lock);
            DOD::Database::finishJob( $job, $retcode, $log, $worker_dbh );
        } # Release $db_lock
    }
    else{
        $logger->error( "An error ocurred preparing command execution \n $!" );
        $logger->debug( "Finishing Job.");
        { #Acquire $db_lock
            lock($db_lock);
            DOD::Database::finishJob( $job, 1, $!, $worker_dbh );
        } # Release $db_lock
    }
    $logger->debug( "Exiting worker process" );

    threads->exit(0);
}


sub get_callback{
    # Returns callback method for command/type pair, if defined

    my $job = shift;
    my $type = $job->{'TYPE'};
    my $command = $job->{'COMMAND_NAME'};
    my $res = undef;
    $res = $command_callback_table{$command}->{$type};
    if (defined $res){
        $logger->debug( "Returning $command callback: $res" );
    }
    else{
        $logger->debug( "Returning $command callback: <undef>" );
    }
    return $res;
}

sub get_state_checker{
    # Returns  method for command/type pair, if defined

    my $job = shift;
    my $type = $job->{'TYPE'};
    my $res = undef;
    $res = $state_checker_table{$type};
    if (defined $res){
        $logger->debug( "Returning $type state checker:  $res" );
    }
    else{
        $logger->debug( "Returning $type state checker: <undef>" );
    }
    return $res;
}

# End of Module
END{

}

1;
