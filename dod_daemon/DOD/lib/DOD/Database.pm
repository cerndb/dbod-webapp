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
if (defined $password){
    $logger->debug( "Testing database connection for $user:$DSN:XXXXXXXX" );
    my $dbh = DBI->connect( $DSN, $user, $password) ;
    $logger->debug( "Disconnecting" );
    $dbh->disconnect();
    }
else {
    $logger->error_die("Check DB connection parameters. Couldn't start a connection" );
}

} # BEGIN BLOCK

sub getInstanceList{
    my $dbh;
    if ($#_ == 0){
        $dbh = shift;
    }
    else{
        $dbh = getDBH();
    }
    my @result;
    eval {
        my $sql = "select username, db_name, db_type, state from dod_instances";
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
        $logger->error( "Unable to connect to database !!!" );
        return ();
    };
    return @result;
}   

sub getJobList{
    my $dbh;
    if ($#_ == 0){
        $dbh = shift;
    }
    else{
        $dbh = getDBH();
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
        $logger->error( "Unable to connect to database !!!" );
        return ();
    };
    return @result;
}   

sub getTimedOutJobs{
    my $dbh;
    if ($#_ == 0){
        $dbh = shift;
    }
    else{
        $dbh = getDBH();
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
        $logger->error( "Unable to connect to database !!!" );
        return (); # Returns an empty array in an attempt to fail smoothly
    };
    return @result;
}   

sub updateInstanceState{
    my ($job, $state, $dbh);
    if ($#_ == 1){
        ($job, $state) = @_;
        $dbh = getDBH();
    }
    elsif($#_ == 2){
        ($job, $state, $dbh) = @_;
    }
    else{
        $logger->error( "Wrong number of parameters" );
        return undef;
    }
    eval {
        my $sql = "update DOD_INSTANCES set state = ?
        where username = ? and db_name = ?";
        $logger->debug( "Preparing statement: \n\t$sql" );
        my $sth = $dbh->prepare( $sql );
        $logger->debug( "Binding parameters");
        $sth->bind_param(1, $state);
        $sth->bind_param(2, $job->{'USERNAME'});
        $sth->bind_param(3, $job->{'DB_NAME'});
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
        $logger->error( "Unable to connect to database" );
        return undef;
    };
}

sub updateJobState{
    my ($job, $state, $dbh);
    if($#_ == 2){
        ($job, $state, $dbh) = @_;
    }
    elsif ($#_ == 1){
        ($job, $state) = @_;
        $dbh = getDBH();
    }
    else{
        $logger->error( "Wrong number of parameters" );
        return undef;
    }
    eval {
        my $sql = "update DOD_JOBS set state = ?
        where username = ? and db_name = ? and command_name = ? and type = ? and creation_date = ?";
        $logger->debug( "Preparing statement: \n\t$sql" );
        my $sth = $dbh->prepare( $sql );
        $logger->debug( "Binding parameters");
        $sth->bind_param(1, $state);
        $sth->bind_param(2, $job->{'USERNAME'});
        $sth->bind_param(3, $job->{'DB_NAME'});
        $sth->bind_param(4, $job->{'COMMAND_NAME'});
        $sth->bind_param(5, $job->{'TYPE'});
        $sth->bind_param(6, $job->{'CREATION_DATE'});
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
        $logger->error( "Unable to connect to database !!!");
        return undef;
    };
}

sub updateJobCompletionDate{
    my ($job, $dbh);
    if ($#_ == 1){
        ($job, $dbh) = @_;
    }
    elsif($#_ == 0){
        $job = shift;
        $dbh = getDBH();
    }
    else{
        $logger->error( "Wrong number of parameters" );
        return undef;
    }
    eval {
        my $sql = "update DOD_JOBS set COMPLETION_DATE = sysdate
        where username = ? and db_name = ? and command_name = ? and type = ? and creation_date = ?";
        $logger->debug( "Preparing statement: \n\t$sql" );
        my $sth = $dbh->prepare( $sql );
        $logger->debug( "Binding parameters");
        $sth->bind_param(1, $job->{'USERNAME'});
        $sth->bind_param(2, $job->{'DB_NAME'});
        $sth->bind_param(3, $job->{'COMMAND_NAME'});
        $sth->bind_param(4, $job->{'TYPE'});
        $sth->bind_param(5, $job->{'CREATION_DATE'});
        $logger->debug("Executing statement");
        $sth->execute();
        $logger->debug( "Finishing statement" );
        $sth->finish();
        if ($#_ == 0){
            $logger->debug( "Disconnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "Unable to connect to database !!!" );
        return undef;
    };
}

sub updateJobLog{
    my ($job, $log, $dbh);
    if ($#_ == 2){
        ($job, $log, $dbh) = @_;
    }
    elsif ($#_ == 1){
        ($job, $log) = @_;
        $dbh = getDBH();
    }
    else{
        $logger->error( "Wrong number of parameters" );
        return undef;
    }
    eval {
        my $sql = "update DOD_JOBS set LOG = ?
        where username = ? and db_name = ? and command_name = ? and type = ? and creation_date = ?";
        $logger->debug( "Preparing statement: \n\t$sql" );
        my $sth = $dbh->prepare( $sql );
        $logger->debug( "Binding parameters");
        $sth->bind_param(1, $log);
        $sth->bind_param(2, $job->{'USERNAME'});
        $sth->bind_param(3, $job->{'DB_NAME'});
        $sth->bind_param(4, $job->{'COMMAND_NAME'});
        $sth->bind_param(5, $job->{'TYPE'});
        $sth->bind_param(6, $job->{'CREATION_DATE'});
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
        $logger->error( "Unable to connect to database !!!" );
        return undef;
    };
}

sub finishJob{
    my ($job, $state, $log, $dbh);
    if ($#_ == 3){
        ($job, $state, $log, $dbh) = @_;
    }
    elsif($#_ == 2){
        ($job, $state, $log) = @_;
        $dbh = getDBH();
    }
    else{
        $logger->error( "Wrong number of parameters" );
        return undef;
    }
    eval{
        $logger->debug( "Updating job Completion Date" );
        updateJobCompletionDate($job, $dbh);
        $logger->debug( "Updating job LOG" );
        updateJobLog($job, $log, $dbh);
        $logger->debug( "Updating job LOG" );
        updateJobState($job, $state, $dbh);
        $logger->debug( "Updating Instance State" );
        updateInstanceState($job, 'RUNNING', $dbh);
        if ($#_ == 2){
            $logger->debug( "Disconnectingnnecting from database" );
            $dbh->disconnect();
        }
        1;
    } or do {
        $logger->error( "Unable to connect to database !!!");
        return undef;
    };
}

sub getJobParams{
    my ($job, $dbh);
    if ($#_ == 1){
        ($job, $dbh) = @_;
    }
    elsif($#_ == 0){
        $job = shift;
        $dbh = getDBH();
    }
    else{
        $logger->error( "Wrong number of parameters" );
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
        $logger->error( "Unable to connect to database" );
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
        $job = shift;
        $dbh = getDBH();
    }
    else{
        $logger->error( "Wrong number of parameters" );
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
        $logger->error( "Unable to connect to database !!!" );
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
        $job = shift; 
        $file_type = shift;
        $dbh = getDBH();
    }
    else{
        $logger->error( "Wrong number of parameters" );
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
        $job = shift; 
        $dbh = getDBH();
    }
    else{
        $logger->error( "Wrong number of parameters" );
        return undef;
    }
    my $cmd;
    eval{
        $logger->debug( "Fetching execution string" );
        $cmd = $job->{'TYPE'} . '_' . lc($job->{'COMMAND_NAME'}) . ' ' . getExecString($job, $dbh);
        $logger->debug( " $cmd " );
        $logger->debug( "Fetching Job params" );
        my $params = getJobParams($job, $dbh);
        $logger->debug( "Disconnecting from DB" );
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
                    $logger->debug("clob :\n$clob");
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
                $logger->error( "Some of the command parameters could not be parsed ");
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
            $logger->error( "The number of parameters is wrong. $expected_nparams expected, $nparams obtained." );
            $cmd = undef;
        }
        1;
    } or do {
        $logger->error( "Unable to prepare command" );
        $cmd = undef;
    };
    if ($#_ == 0){
        $logger->debug("Disconnecting from database");
        $dbh->disconnect();
        }
    return $cmd;
}

sub getDBH{
    $logger->debug( "Obtaining DB connection handle" );
    my $dbh;
    eval {
        $dbh = DBI->connect( $DSN, $user, $password, { AutoCommit => 1 });
        $logger->debug( "Setting date format: $DATEFORMAT" );
        $dbh->do( "alter session set NLS_DATE_FORMAT='$DATEFORMAT'" );
    } or do {
        $logger->error( "Unable to connect to database !!!" );
        $dbh = undef;
    };
    return $dbh;
}


# End of Module
END{

}

1;
