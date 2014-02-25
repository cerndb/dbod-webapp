package DBOD;

use strict;
use warnings;
use Exporter;

use DBI;
use DBD::Oracle qw(:ora_types);
use POSIX qw(strftime);

use DBOD::Config qw( $config );
use DBOD::Database;
use DBOD::MySQL;
use DBOD::Oracle;
use DBOD::PostgreSQL;
use DBOD::Middleware;
use DBOD::All;
use DBOD::Command;

use POSIX ":sys_wait_h";

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $logger,
    $DSN, $DBTAG, $DATEFORMAT, $user, $password, %callback_table);

$VERSION     = 2.1;
@ISA         = qw(Exporter);
@EXPORT      = qw(jobDispatcher $logger);
@EXPORT_OK   = ( );
%EXPORT_TAGS = ( );

# Load general configuration

INIT {
    $logger = Log::Log4perl::get_logger('DBOD');
    $logger->info('Logger created');
} # BEGIN BLOCK

my %command_callback_table = (
    'UPGRADE' => { 'MYSQL' => \&DBOD::MySQL::upgrade_callback , 
                    'ORACLE' => \&DBOD::Oracle::upgrade_callback, 
                    'ORA' => \&DBOD::Oracle::upgrade_callback, 
                    'PG' => \&DBOD::PostgreSQL::upgrade_callback, }
);

my %state_checker_table = (
    'MYSQL' => \&DBOD::All::state_checker,
    'ORACLE' => \&DBOD::All::state_checker, 
    'ORA' => \&DBOD::All::state_checker, 
    'PG' => \&DBOD::All::state_checker, 
    'MIDDLEWARE' => \&DBOD::Middleware::state_checker, 
);

sub jobDispatcher {
    # This is neccesary because daemonizing closes all file descriptors
    my $logger = Log::Log4perl::get_logger('DBOD.jobDispatcher');
    my $dbh = DBOD::Database::getDBH();
    my @tasks;
    my @job_list;
    while (1){

        $logger->info('Checking Database connection');
        unless(defined($dbh->ping)) {
            $logger->error('The connecion to the DB was lost');
            $dbh = undef;
            $logger->info('Creating new DB connection');
            $dbh = DBOD::Database::getDBH();
        }
        
        $logger->info('Fetching job list');
        push(@job_list, DBOD::Database::getJobList($dbh));
        my $pendingjobs = $#job_list + 1;
        $logger->info("Pending jobs: $pendingjobs");
        if ($pendingjobs > 0){
            foreach my $job (@job_list){
                $logger->info(sprintf("Fetching job params" ));
                $job->{'PARAMS'} = DBOD::Database::getJobParams($job, $dbh);
                $logger->info(sprintf("Number of open tasks: %d", $#tasks + 1));
                if ($#tasks < 20){
                    my $worker_pid = fork();
                    if ($worker_pid){
                        $logger->info("Adding worker ($worker_pid) to pool");
                        my $task = {};
                        $job->{'STATE'} = 'DISPATCHED';
                        $job->{'task'} = $task; 
                        $task->{'pid'} = $worker_pid;
                        $task->{'job'} = $job;
                        push(@tasks, $task);
                        # Updates job status to RUNNING
                        DBOD::Database::updateJob($job, 'STATE', 'RUNNING', $dbh); # forking destroys $dbh ??
                    }
                    else{
                        &worker_body($job, $dbh);
                    }
                }
                else {
                    $logger->info("Waiting for $#tasks tasks  completion");
                    foreach my $task (@tasks) {
                        my $tmp = waitpid($task->{'pid'}, 0);
                        $logger->info("Done with worker : $tmp");
                    }
                    $logger->info('Removing finished workers from pool');
                    @tasks = grep(waitpid($_->{'pid'}, 0)>=0, @tasks);
                }
            }
        }
        else{
            # Cleaning stranded jobs 
            $logger->info('Checking for Timed Out jobs');
            my @timedoutjobs = DBOD::Database::getTimedOutJobs($dbh);
            foreach my $job (@timedoutjobs){
                my $state_checker = get_state_checker($job);
                if (! defined($state_checker)){
                    $logger->error('No state checker defined for this DB type');
                }
                # Fetching instance state
                my ($job_state, $instance_state) = $state_checker->($job, 1, 1);
                $logger->info( "Updating job STATE" );
                DBOD::Database::updateJob( $job, 'STATE', 'TIMED OUT', $dbh );
                $logger->info( "Updating job Completion Date" );
                DBOD::Database::updateJob($job, 'COMPLETION_DATE', 'sysdate', $dbh);
                $logger->info( "Updating job LOG" );
                DBOD::Database::updateJob($job, 'LOG', 'This job was cancelled for exceeding the maximum running time (6h)', $dbh);
                $logger->info( "Updating Instance State" );
                DBOD::Database::updateInstance( $job, 'STATE', $instance_state, $dbh );
                my $task = $job->{'task'};
                if (ref $task) {
                    my $pid = $task->{'pid'};
                    $logger->info( "Killing stranded process ($pid)"); 
                    if (kill($SIG{KILL}, $pid) == 1){
                        $logger->info( "Process ($pid) succesfully killed");
                    }
                    else{
                        $logger->error( "Process ($pid) could not be killed\n $!");
                    }
                }
            }
        }

        # Remove dispatched jobs from joblist
        $logger->info("Cleaning Dispatched jobs from job list ($pendingjobs)");
        @job_list = grep( ( $_->{'STATE'} =~ 'PENDING' ), @job_list);
        $logger->info(sprintf("Pending jobs after cleaning (%d)", $#job_list + 1));
        
        # Reaping
        my $ntasks = $#tasks +1;
        $logger->info("Waiting for $ntasks tasks");
        foreach my $task (@tasks) {
            my $tmp = waitpid($task->{'pid'}, WNOHANG);
            if ($tmp) {
                $logger->info("Done with worker : $tmp");
            }
        }
        
        $logger->info('Removing finished workers from pool');
        @tasks = grep(waitpid($_->{'pid'}, WNOHANG)>=0, @tasks);

        # Iteration timer
        sleep 5;
    }
}

sub worker_body {
    my ($job, $pdbh) = @_;
    my $logger = Log::Log4perl::get_logger('DBOD.worker');

    my $worker_dbh;
    if (defined($pdbh)){
        # Cloning parent process DB handler
        $logger->info('Cloning parent DB handler');
        $worker_dbh = $pdbh->clone();
        $logger->debug("Setting date format: $DBOD::Database::DATEFORMAT" );
        $worker_dbh->do("alter session set NLS_DATE_FORMAT='$DBOD::Database::DATEFORMAT'");
        $pdbh->{InactiveDestroy} = 1;
        undef $pdbh;
    }
    else{
        # Obtaining a new DB connector
        $worker_dbh = DBOD::Database::getDBH();
    }

    my $cmd_line = DBOD::Command::prepareCommand($job, $worker_dbh);
    $logger->info("Received cmd_line: $cmd_line");
    my $log;
    my $retcode;
    my $params = $job->{'PARAMS'};
    if ((defined $cmd_line) && ($cmd_line !~ /^ERROR/)){
        my $entity = DBOD::All::get_entity($job);
        my $cmd =  "/etc/init.d/syscontrol -i $entity $cmd_line";
        $logger->info( "Executing $cmd" );
        $log = `$cmd`;
        $retcode = DBOD::All::result_code($log);
        $logger->info("Finishing Job. Return code: $retcode");
        DBOD::Database::finishJob( $job, $retcode, $log, $worker_dbh );
    }
    else{
        $logger->error( "An error ocurred preparing command execution:\n$cmd_line" );
        $logger->info( "Finishing Job.");
        DBOD::Database::finishJob( $job, 1, $cmd_line, $worker_dbh );
    }
    $logger->info( "Exiting worker process" );
    $worker_dbh->disconnect();
    exit 0;
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
