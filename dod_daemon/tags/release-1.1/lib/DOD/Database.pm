package DOD::Database;

use strict;
use warnings;
use Exporter;

use YAML::Syck;
use File::ShareDir;
use Log::Log4perl;
use File::Temp;

use DBI;
use DBD::Oracle qw(:ora_types);
use POSIX qw(strftime);

use DOD::ConfigParser;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, $config_dir, $logger,
    $DSN, $DBTAG, $DATEFORMAT, $user, $password);

$VERSION     = 0.03;
@ISA         = qw(Exporter);
@EXPORT      = qw(getJobList updateJobState updateJobCompletionDate updateJobLog finishJob getDBH);
@EXPORT_OK   = qw(getDBH);
%EXPORT_TAGS = ( );

# Load general configuration

BEGIN{

sub getPassword 
{
    my ($tag, $password_file) = @_;
    my @passwd_lines;
    $logger->debug("password_file = $password_file, tag= $tag");
    open (PASS, $password_file) && (defined $tag) or return;
    @passwd_lines = <PASS>;
    close PASS;

    my @line = grep(/^\s*TAG\s+$tag=/, @passwd_lines);
    if ((defined $line[0]) && ($line[0] =~ m/^\s*TAG\s+$tag=(.*)/))
    {
        return $1;
    }
    return undef;
}
    
$config_dir = File::ShareDir::dist_dir( "DOD" );
$config = LoadFile( "$config_dir/dod.conf" );
Log::Log4perl::init( "$config_dir/$config->{'LOGGER_CONFIG'}" );
$logger = Log::Log4perl::get_logger( 'DOD' );
$logger->debug( "Logger created" );
$logger->debug( "Loaded configuration from $config_dir" );
foreach my $key ( keys(%{$config}) ) {
    my %h = %{$config};
    $logger->debug( "\t$key -> $h{$key}" );
    }

# DB configuration parameters
$DSN = $config->{'DB_DSN'} ;
$DBTAG = $config->{'DB_USER'};
$DATEFORMAT = $config->{'DB_DATE_FORMAT'};
my @buf = split( /_/, $DBTAG );
$user = pop( @buf );
$password = getPassword( $DBTAG, $config->{'PASSWORD_FILE'} );

} # BEGIN BLOCK

sub getInstanceList{
    my $dbh;
    if ($#_ == 0){
        ($dbh) = @_;
    }
    else{
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return ();
        }
    }
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
        $logger->error( "Unable to connect to database !!!\n $!" );
        return ();
    };
    return @result;
}   

sub getJobList{
    my $dbh;
    if ($#_ == 0){
        ($dbh) = @_;
    }
    else{
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return ();
        }
    }
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
        $logger->error( "Unable to connect to database !!!\n $!" );
        return ();
    };
    return @result;
}   

sub getTimedOutJobs{
    my $dbh;
    if ($#_ == 0){
        ($dbh) = @_;
    }
    else{
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return ();
        }
    }
    my @result;
    eval {
        my $sql = "select username, db_name, command_name, type, creation_date
            from dod_jobs where (state = 'RUNNING' or state = 'PENDING')
            and creation_date < (select sysdate from dual)-1/24"; 
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
        $logger->error( "Unable to connect to database !!!\n $!" );
        return (); # Returns an empty array in an attempt to fail smoothly
    };
    return @result;
}   

sub updateInstance{
    my ($job, $col_name, $col_value, $dbh);
    if ($#_ == 2){
        ($job, $col_name, $col_value) = @_;
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return undef;
        }
    }
    elsif($#_ == 3){
        ($job, $col_name, $col_value, $dbh) = @_;
    }
    else{
        $logger->error( "Wrong number of parameters\n $!" );
        return undef;
    }
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
        $logger->error( "Unable to connect to database\n $!" );
        return undef;
    };
}


sub updateJob{
    my ($job, $col_name, $col_value, $dbh);
    if ($#_ == 2){
        ($job, $col_name, $col_value) = @_;
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return undef;
        }
    }
    elsif ($#_ == 3){
        ($job, $col_name, $col_value, $dbh) = @_;
    }
    else{
        $logger->error( "Wrong number of parameters\n $!" );
        return undef;
    }
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
        $logger->error( "Unable to connect to database !!!\n $!" );
        return undef;
    };
}

sub finishJob{
    my ($job, $resultCode, $log, $dbh);
    if ($#_ == 3){
        ($job, $resultCode, $log, $dbh) = @_;
    }
    elsif($#_ == 2){
        ($job, $resultCode, $log) = @_;
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return undef;
        }
    }
    else{
        $logger->error( "Wrong number of parameters\n $!" );
        return undef;
    }
    eval{
        my $state_checker = DOD::get_state_checker($job);
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
        my $callback = DOD::get_callback($job);
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
        $logger->error( "An error occured trying to finish the job.\n $!");
        return undef;
    };
}

sub getJobParams{
    my ($job, $dbh);
    if ($#_ == 1){
        ($job, $dbh) = @_;
    }
    elsif($#_ == 0){
        ($job) = @_;
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return undef;
        }
    }
    else{
        $logger->error( "Wrong number of parameters\n $!" );
        return undef;
    }
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
        $logger->error( "Unable to connect to database\n $!" );
        return undef;
    };
    return $res; 
}

sub getExecString{
    my ($job, $dbh);
    if ($#_ == 1){
        ($job, $dbh) = @_;
    }
    elsif($#_ == 0){
        ($job) = @_;
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return undef;
        }
    }
    else{
        $logger->error( "Wrong number of parameters\n $!" );
        return undef;
    }
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
        $logger->error( "Unable to connect to database !!!\n $!" );
        return undef;
    };
    return $ref->{'EXEC'}; 
}

sub getConfigFile{
    my ($job, $file_type, $dbh);
    if ($#_ == 2){
        ($job, $file_type, $dbh) = @_;
    }
    elsif($#_ == 1){
        ($job, $file_type) = @_;
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return undef;
        }
    }
    else{
        $logger->error( "Wrong number of parameters\n $!" );
        return undef;
    }
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
        $logger->debug( "Unable to connect to database !!!");
        return undef;
    };
    return $result;
}

sub prepareCommand {
    my ($job, $dbh);
    if ($#_ == 1){
        ($job, $dbh) = @_;
    }
    elsif($#_ == 0){
        ($job) = @_; 
        $dbh = getDBH();
        unless(defined($dbh)){
            $logger->error( "Unable to get DB handler" );
            return undef;
        }
    }
    else{
        $logger->error( "Wrong number of parameters\n $!" );
        return undef;
    }
    my $cmd;
    eval{
        $logger->debug( "Fetching execution string" );
        $cmd = $job->{'TYPE'} . '_' . lc($job->{'COMMAND_NAME'}) . ' ' . getExecString($job, $dbh);
        $logger->debug( " $cmd " );
        $logger->debug( "Fetching Job params" );
        my $params = getJobParams($job, $dbh);
        my $nparams = scalar(@{$params});
        my $expected_nparams = 0;
        my $optional_nparams = 0;
        $expected_nparams++ while ($cmd =~ m/:/g);
        $optional_nparams++ while ($cmd =~ m/#/g);
        $logger->debug("Expected: $expected_nparams, Optional: $optional_nparams");

        if ($expected_nparams == 0){
            if ($#_ == 0){
                $logger->debug("Disconnecting from database");
                $dbh->disconnect();
                }
            return $cmd;
        }
        
        if ($nparams >= $expected_nparams){
            $logger->debug( "Substituting params");
            foreach my $param (@{$params}){
                if ($param->{'NAME'} =~ /FILE/){
                    my ($pname, $type) = split( /=/, $param->{'NAME'} );
                    $logger->debug("pname: $pname, type: $type");
                    my $clob = $param->{'VALUE'};
                    my $parser = DOD::ConfigParser::get( $type );
                    $logger->debug("parser: $parser");
                    my $filename = $parser->($clob); 
                    $cmd =~ s/:$param->{'NAME'}/$filename/;
                    $logger->debug( "cmd: $cmd" );
                    # Distribute required files 
                    my $entity = DOD::entityName($job);
                    $logger->debug( "Copying files to $entity" );
                    DOD::copyToEntity( $filename, $entity );
                    $logger->debug( "Deleting temporal files");
                    system("rm -fr $filename");
                    }
                else{
                    $cmd =~ s/:$param->{'NAME'}=/$param->{'VALUE'}/;
                    $cmd =~ s/#$param->{'NAME'}=/$param->{'VALUE'}/;
                    }
                }
            my $buf = 0;
            $buf++ while ($cmd =~ m/:(.*)+=/g);
            if ($buf) {
                $logger->error( "Some of the command parameters could not be parsed\n $!" );
                }
            $buf = 0;
            $buf++ while ($cmd =~ m/#(.*)+=/g);
            if ($buf>0) {
                $logger->debug( "Some of the Optional command parameters could not be parsed" );
                $logger->debug( "Buf: $buf, cmd: $cmd" );
                my @items = split( /-/, $cmd);
                my @result;
                for my $item (@items){
                    if ($item !~ m/#(.*)+=/){
                        $logger->debug("Item: $item");
                        push(@result, $item);
                    }
                }
                $cmd = join('-', @result);
                $logger->debug( "Num. optional params: $buf, \ncmd: $cmd" );
                }
        }
        else {
            $logger->error( "The number of parameters is wrong. $expected_nparams expected, $nparams obtained.\n $!" );
            $cmd = undef;
        }
        1;
    } or do {
        $logger->error( "Unable to prepare command\n $!" );
        $cmd = undef;
    };
    if ($#_ == 0){
        $logger->debug("Disconnecting from database");
        $dbh->disconnect();
        }
    return $cmd;
}

sub getDBH{
    my $dbh;
    eval {
        $dbh = DBI->connect( $DSN, $user, $password, { AutoCommit => 1, ora_client_info => 'dod_daemon', ora_verbose => 6 });
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
