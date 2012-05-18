package DOD::Oracle;

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
    $logger->error_die("Check DB connection parameters. Couldn't start a connection\n $!" );
}

} # BEGIN BLOCK

