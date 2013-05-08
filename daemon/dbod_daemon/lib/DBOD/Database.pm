package DBOD::Database;

use strict;
use warnings;
use Exporter;

use DBI;
use DBD::Oracle qw(:ora_types);
use POSIX qw(strftime);

use DBOD::Config qw( $config );
use DBOD::Templates;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $logger,
    $DSN, $DBTAG, $DATEFORMAT, $user, $password, $JOB_MAX_RUNNING,
    $JOB_MAX_PENDING);

$VERSION     = 0.03;
@ISA         = qw(Exporter);
@EXPORT      = qw(getJobList updateJobState updateJobCompletionDate updateJobLog finishJob getDBH);
@EXPORT_OK   = qw(getDBH);
%EXPORT_TAGS = ( );

# Load general configuration

INIT{
    $logger = Log::Log4perl::get_logger( 'DBOD.Database' );
    $logger->debug( "Logger created" );
    # DB configuration parameters
    $DSN = $config->{'DB_DSN'} ;
    $DATEFORMAT = $config->{'DB_DATE_FORMAT'};
    $DBTAG = $config->{'DB_TAG'};
    $user = $config->{'DB_USER'};
    $password = $config->{'DB_PASSWORD'};
    $JOB_MAX_RUNNING = $config->{'JOB_MAX_RUNNING'};
    $JOB_MAX_PENDING = $config->{'JOB_MAX_PENDING'};
} # INIT BLOCK

sub getInstanceList{
    my $dbh = shift;
    my @result;
    eval {
        my $sql = "select username, db_name, db_type as type, state from dod_instances";
        $logger->debug( $sql );
        my $sth = $dbh->prepare( $sql );
        $logger->debug("Executing statement");
        $sth->execute();
        $logger->debug( "[Instances]" );
        while ( my $ref = $sth->fetchrow_hashref() ){
            push @result, $ref;
            foreach my $key ( keys(%{$ref}) ) {
                my %h = %{$ref};
                $logger->debug( $key, "->", $h{$key} );
                }
            }
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == -1){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do{
        $logger->error( "Error fetching instances List : $!" );
        return ();
    };
    return @result;
}   

sub isShared{
    my ($dbh, $db_name) = @_;
    eval{
        my $sql = "select db_name from dod_instances 
        where shared_instance in 
        (select shared_instance 
            from dod_instances 
            where db_name= ?) 
        and db_name <> ?";
        my $sth = $dbh->prepare( $sql );
        $sth->bind_param(1, $db_name);
        $sth->bind_param(2, $db_name);
        $logger->debug("Executing statement");
        $sth->execute();
        my $ref = $sth->fetchrow_hashref();
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if (defined $ref){
            $logger->debug( "$db_name is shared with " . $ref->{'DB_NAME'});
            return $ref->{'DB_NAME'};
        }
        1;
    } or do {
        $logger->error( "Error fetching shared state for $db_name : $!" );
        return undef;
    }
}

sub isMaster{
    my ($dbh, $db_name) = @_;
    eval{
        my $sql = "select slave from dod_instances where db_name = ?";
        my $sth = $dbh->prepare( $sql );
        $sth->bind_param(1, $db_name);
        $logger->debug("Executing statement");
        $sth->execute();
        my $ref = $sth->fetchrow_hashref();
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if (defined $ref){
            $logger->debug( "$db_name is slave to " . $ref->{'SLAVE'});
            return $ref->{'SLAVE'};
        }
        1;
    } or do {
        $logger->error( "Error fetching master status for $db_name : $!" );
        return undef;
    }
}

sub isSlave{
    my ($dbh, $db_name) = @_;
    eval{
        my $sql = "select master from dod_instances where db_name = ?";
        my $sth = $dbh->prepare( $sql );
        $sth->bind_param(1, $db_name);
        $logger->debug("Executing statement");
        $sth->execute();
        my $ref = $sth->fetchrow_hashref();
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if (defined $ref){
            $logger->debug( "$db_name is slave to " . $ref->{'MASTER'});
            return $ref->{'MASTER'};
        }
        1;
    } or do {
        $logger->error( "Error fetching slave status for $db_name : $!" );
        return undef;
    }
}


sub getJobList{
    my $dbh = shift;
    my @result;
    eval {
        my $sql = "select a.username, a.db_name, a.command_name, a.type, a.creation_date
            from dod_jobs a inner join (
                select username, db_name, min(creation_date) as creation_date 
                from dod_jobs
                where state = 'PENDING'
                group by username, db_name) b
            on a.username = b.username
            and a.db_name = b.db_name
            and a.creation_date = b.creation_date
            where a.state = 'PENDING'"; 
        $logger->debug( $sql );
        my $sth = $dbh->prepare( $sql );
        $logger->debug("Executing statement");
        $sth->execute();
        while ( my $ref = $sth->fetchrow_hashref() ){
            push @result, $ref;
            $logger->debug( "[New Job]");
            foreach my $key ( keys(%{$ref}) ) {
                my %h = %{$ref};
                $logger->debug( $key, "->", $h{$key} );
                }
            }
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == -1){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do{
        $logger->error( "Error fetching job list : $!" );
        return ();
    };
    return @result;
}   

sub getTimedOutJobs{
    my $dbh = shift;
    my @result;
    eval {
        my $sql = "select username, db_name, command_name, type, creation_date
            from dod_jobs where (state = 'RUNNING' or state = 'PENDING')
            and creation_date < (select sysdate from dual) -$JOB_MAX_RUNNING/24"; 
        $logger->debug( $sql );
        my $sth = $dbh->prepare( $sql );
        $logger->debug("Executing statement");
        $sth->execute();
        while ( my $ref = $sth->fetchrow_hashref() ){
            push @result, $ref;
            $logger->debug( "[Timeout Job]");
            foreach my $key ( keys(%{$ref}) ) {
                my %h = %{$ref};
                $logger->debug( $key, "->", $h{$key} );
                }
            }
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == -1){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "Unable to check for TIMED OUT jobs : $!" );
        return (); # Returns an empty array in an attempt to fail smoothly
    };
    return @result;
}   

sub getPendingJobs {
    my $dbh = shift;
    my $sql = "select count(*) from dod_jobs where sysdate - creation_date > ( $JOB_MAX_PENDING /86400 )";
    my $count
    eval{
        $count = $dbh->selectrow_array($sql);
    } or do {
        $logger->error( "Unable to check for PENDING jobs : $!" );
        return undef;
    }
    return $count;
}

sub updateInstance{
    my ($job, $col_name, $col_value, $dbh) = @_; 
    eval {
        my $sql = "update DOD_INSTANCES set $col_name = ?
        where username = ? and db_name = ?";
        $logger->debug( "Preparing statement: \n\t$sql" );
        my $sth = $dbh->prepare( $sql );
        $logger->debug( "Binding parameters");
        $sth->bind_param(1, $col_value);
        $sth->bind_param(2, $job->{'USERNAME'});
        $sth->bind_param(3, $job->{'DB_NAME'});
        $logger->debug("Executing statement");
        $sth->execute();
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == 2){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "Unable update instance status : $!" );
        return undef;
    };
}


sub updateJob{
    my ($job, $col_name, $col_value, $dbh) = @_;
    eval {
        my $sth;
        if ($col_name eq 'COMPLETION_DATE'){
            my $sql = "update DOD_JOBS set $col_name = sysdate
            where username = ? and db_name = ? and command_name = ? and type = ? and creation_date = ?";
            $logger->debug( "Preparing statement: \n\t$sql" );
            $sth = $dbh->prepare( $sql );
            $logger->debug( "Binding parameters");
            $sth->bind_param(1, $job->{'USERNAME'});
            $sth->bind_param(2, $job->{'DB_NAME'});
            $sth->bind_param(3, $job->{'COMMAND_NAME'});
            $sth->bind_param(4, $job->{'TYPE'});
            $sth->bind_param(5, $job->{'CREATION_DATE'});
        }
        else{
            my $sql = "update DOD_JOBS set $col_name = ?
            where username = ? and db_name = ? and command_name = ? and type = ? and creation_date = ?";
            $logger->debug( "Preparing statement: \n\t$sql" );
            $sth = $dbh->prepare( $sql );
            $logger->debug( "Binding parameters");
            $sth->bind_param(1, $col_value);
            $sth->bind_param(2, $job->{'USERNAME'});
            $sth->bind_param(3, $job->{'DB_NAME'});
            $sth->bind_param(4, $job->{'COMMAND_NAME'});
            $sth->bind_param(5, $job->{'TYPE'});
            $sth->bind_param(6, $job->{'CREATION_DATE'});
        }
        $logger->debug("Executing statement");
        $sth->execute();
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == 1){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "Unable to update job status : $!" );
        return undef;
    };
}

sub finishJob{
    my ($job, $resultCode, $log, $dbh, $params) = @_;
    eval{
        my $state_checker = DBOD::get_state_checker($job);
        if (!defined $state_checker){
            $logger->error( "Not state checker defined for this DB type" );
        }
        my ($job_state, $instance_state) = $state_checker->($job, $resultCode);
        $logger->debug( "Updating job Completion Date" );
        updateJob($job, 'COMPLETION_DATE', 'sysdate', $dbh);
        $logger->debug( "Updating job LOG" );
        updateJob($job, 'LOG', $log, $dbh);
        $logger->debug( "Updating job STATE" );
        updateJob($job, 'STATE', $job_state, $dbh);
        $logger->debug( "Updating Instance State" );
        updateInstance($job, 'STATE', $instance_state, $dbh); 
        my $callback = DBOD::get_callback($job);
        if (defined $callback){
            $logger->debug( "Executing callback" );
            $callback->($job, $dbh);
        }
        if ($#_ == 2){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "An error occured trying to finish the job : $!");
        return undef;
    };
}

sub getJobParams{
    my ($job, $dbh) = @_;
    my $res;
    eval{
        $dbh->{LongReadLen} = 32768; 
        my $sql = "select NAME, VALUE from DOD_COMMAND_PARAMS
        where USERNAME = ? and DB_NAME = ? and COMMAND_NAME = ? and TYPE = ? and CREATION_DATE = ?";
        $logger->debug( "Preparing statement: \n\t$sql" );
        my $sth = $dbh->prepare( $sql, { ora_pers_lob => 1 } );
        $logger->debug( "Binding parameters");
        $sth->bind_param(1, $job->{'USERNAME'});
        $sth->bind_param(2, $job->{'DB_NAME'});
        $sth->bind_param(3, $job->{'COMMAND_NAME'});
        $sth->bind_param(4, $job->{'TYPE'});
        $sth->bind_param(5, $job->{'CREATION_DATE'});
        $logger->debug("Executing statement");
        $sth->execute();
        my @result;
        while ( my $ref = $sth->fetchrow_hashref() ){
            push @result, $ref;
        }
        $res = \@result;
        if ($sth->rows == 0){
            $res = undef;
        }
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == 0){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "Unable to fetch job parameters : $!" );
        return undef;
    };
    return $res; 
}

sub getExecString{
    my ($job, $dbh ) = @_;
    my $ref;
    eval {
        my $sql = "select EXEC from DOD_COMMAND_DEFINITION
        where COMMAND_NAME = ? and TYPE = ?";
        $logger->debug( "Preparing statement: \n\t$sql" );
        my $sth = $dbh->prepare( $sql );
        $logger->debug( "Binding parameters");
        $sth->bind_param(1, $job->{'COMMAND_NAME'});
        $sth->bind_param(2, $job->{'TYPE'});
        $logger->debug("Executing statement");
        $sth->execute();
        $ref = $sth->fetchrow_hashref();
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == 0){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "Unable to fetch command execution string : $!" );
        return undef;
    };
    return $ref->{'EXEC'}; 
}

sub getConfigFile{
    my ($job, $file_type, $dbh) = @_;
    my $result;
    eval {
        $dbh->{LongReadLen} = 32768; 
        my $sql = "select CONFIG_FILE from DOD_CONFIG_FILES
        where DB_NAME = ? and USERNAME = ? and FILE_TYPE = ?";
        $logger->debug( "Preparing statement: \n\t$sql" );
        my $sth = $dbh->prepare( $sql, { ora_pers_lob => 1 } );
        $logger->debug( "Binding parameters");
        $sth->bind_param(1, $job->{'DB_NAME'});
        $sth->bind_param(2, $job->{'USERNAME'});
        $sth->bind_param(3, $file_type);
        $logger->debug("Executing statement");
        $sth->execute();
        my $ref = $sth->fetchrow_hashref(); 
        my $result = $ref->{'CONFIG_FILE'}; 
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == 1){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "Unable to fetch config file : $!" );
        return undef;
    };
    return $result;
}

sub getDBH{
    my $dbh;
    eval {
        $dbh = DBI->connect( $DSN, $user, $password, { AutoCommit => 1, ora_client_info => 'dbod_daemon', ora_verbose => 0 });
        if (1){ # call to $dbh->ora_can_taf() causes error
            $logger->debug( "Enabling Oracle TAF");
            $dbh->{ora_taf} = 1;
            $dbh->{ora_taf_function} = 'oracle_taf_event';
            $dbh->{ora_taf_sleep} = 5;
        }
        $logger->debug( "Setting date format: $DATEFORMAT" );
        $dbh->do( "alter session set NLS_DATE_FORMAT='$DATEFORMAT'" );
    } or do {
        $logger->error("Unable to obtain DB handler.\n Interpreter($@)\n libc($!)\n OS($^E)\n");
        $dbh = undef;
    };
    return $dbh;
}

sub oracle_taf_event{
    my ($event, $type) = @_;
    $logger->error("TAF event");
    $logger->error(" TAF event: $event\n TAF type: $type");
    return;
}


# End of Module
END{

}

1;
