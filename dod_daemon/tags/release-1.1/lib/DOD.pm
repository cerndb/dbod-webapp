package DOD;

use strict;
use warnings;
use Exporter;

use YAML::Syck;
use File::ShareDir;
use Log::Log4perl;

use DBI;
use DBD::Oracle qw(:ora_types);
use POSIX qw(strftime);

use DOD::Database;
use DOD::MySQL;
use DOD::All;

use POSIX ":sys_wait_h";

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, $config_dir, $logger,
    $DSN, $DBTAG, $DATEFORMAT, $user, $password, %callback_table);

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
    my $dbh = DOD::Database::getDBH();
    my @tasks;
    my @job_list;
    while (1){

         $logger->debug("Checking status of connection");
        unless(defined($dbh->ping)){
            $logger->error("The connecion to the DB was lost");
            $dbh = undef;
            $logger->debug("Creating new DB connection");
            $dbh = DOD::Database::getDBH();
        }
        
        $logger->debug( "Fetching job list" );
        push(@job_list, DOD::Database::getJobList($dbh));
        my $pendingjobs = $#job_list + 1;
        $logger->debug( "Pending jobs: $pendingjobs" );
        if ($pendingjobs > 0){
            foreach my $job (@job_list){
                $logger->debug( sprintf("Number of open tasks: %d", $#tasks + 1) );
                if ($#tasks < 20){
                    my $worker_pid = fork();
                    if ($worker_pid){
                        $logger->debug( "Adding worker ($worker_pid) to pool" );
                        my $task = {};
                        $job->{'STATE'} = 'DISPATCHED';
                        $job->{'task'} = $task; 
                        $task->{'pid'} = $worker_pid;
                        $task->{'job'} = $job;
                        push(@tasks, $task);
                        # Updates job status to RUNNING
                        DOD::Database::updateJob($job, 'STATE', 'RUNNING', $dbh); # forking destroys $dbh ??
                    }
                    else{
                        &worker_body($job);
                        # Exit moved outside of worker_body to allow safe cleaing of DB objects
                        exit 0;
                    }
                }
                else {
                    $logger->debug( "Waiting for $#tasks tasks  completion" );
                    foreach my $task (@tasks) {
                        my $tmp = waitpid($task->{'pid'}, 0);
                        $logger->debug( "Done with worker : $tmp" );
                    }
                    $logger->debug( "Removing finished workers from pool" );
                    @tasks = grep(waitpid($_->{'pid'}, 0)>=0, @tasks);
                }
            }
        }
        else{
            # Cleaning stranded jobs 
            $logger->debug( "No pending jobs" );
            $logger->debug( "Checking for timed out jobs" );
            my @timedoutjobs = DOD::Database::getTimedOutJobs($dbh);
            foreach my $job (@timedoutjobs){
                my $state_checker = get_state_checker($job);
                if (! defined($state_checker)){
                    $logger->error( "Not state checker defined for this DB type" );
                }
                my ($job_state, $instance_state) = $state_checker->($job, 1);
                DOD::Database::finishJob( $job, $job_state, "TIMED OUT", $dbh );
                DOD::Database::updateInstance( $job, 'STATE', $instance_state, $dbh );
                my $task = $job->{'task'};
                if (ref $task) {
                    my $pid = $task->{'pid'};
                    $logger->debug( "Killing stranded process ($pid)"); 
                    if (kill($SIG{KILL}, $pid) == 1){
                        $logger->debug( "Process ($pid) succesfully killed");
                    }
                    else{
                        $logger->error( "Process ($pid) could not be killed\n $!");
                    }
                }
            }
        }

        # Remove dispatched jobs from joblist
        $logger->debug( "Cleaning Dispatched jobs from job list. #JOBS = $pendingjobs");
        @job_list = grep( ( $_->{'STATE'} =~ 'PENDING' ), @job_list);
        $logger->debug( sprintf("Pending jobs after cleaning Dispatched jobs #JOBS = %d", $#job_list + 1) );
        
        # Reaping
        my $ntasks = $#tasks +1;
        $logger->debug( "Waiting for $ntasks tasks  completion" );
        foreach my $task (@tasks) {
            my $tmp = waitpid($task->{'pid'}, WNOHANG);
            if ($tmp) {
                $logger->debug( "Done with worker : $tmp" );
            }
        }
        
        $logger->debug( "Removing finished workers from pool" );
        @tasks = grep(waitpid($_->{'pid'}, WNOHANG)>=0, @tasks);

        # Iteration timer
        sleep 5;
    }
}

sub worker_body {
    my $job = shift;
    my $logger = Log::Log4perl::get_logger( "DOD.worker" );
    my $dbh = DOD::Database::getDBH();
    do {
        $logger->error( "Unable to connect to database !!! \n $!" );
        die( "Unable to connect to database !!!" );
    } unless (defined($dbh));
    my $cmd_line = DOD::Database::prepareCommand($job, $dbh);
    my $log;
    my $retcode;
    if (defined $cmd_line){
        my $buf = DOD::Database::getJobParams($job, $dbh);
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
        DOD::Database::finishJob( $job, $retcode, $log, $dbh );
    }
    else{
        $logger->error( "An error ocurred preparing command execution \n $!" );
        $logger->debug( "Finishing Job.");
        DOD::Database::finishJob( $job, 1, $!, $dbh );
    }
    $logger->debug( "Exiting worker process" );
    $dbh->disconnect();
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
