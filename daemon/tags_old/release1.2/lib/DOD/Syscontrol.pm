package DOD::Syscontrol;

use strict;
use warnings;
use Exporter;

use YAML::Syck;
use File::ShareDir;
use Log::Log4perl;

use Net::LDAP;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, $config_dir, $logger);

$VERSION     = 0.03;
@ISA         = qw(Exporter);
@EXPORT      = qw(update_entity);
@EXPORT_OK   = qw(update_entity);
%EXPORT_TAGS = ( );

# Load general configuration

BEGIN{

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

} # BEGIN BLOCK

sub get_LDAP_conn {
    if ($#_) {
       ($url, $port, $protocol, $userdn, $pass) = @_;
    }
    $logger->debug( "Opening connection to $protocol//$url:$port" );
    my $conn = Net::LDAP->new($url, port => $port, scheme => $protocol) or die("$@");
    if (!defined($conn)){
        $logger->error( "Error connecting to LDAP server\n$@" );
        return undef;
    }
    $logger->debug( "Binding to $user" ); 
    $msg = $conn->bind($userdn, password => $pass);
    if ($mesg->code){
        $logger->error( "Error in LDAP binding" );
        $logger->error( $msg->error );
        return undef;
    }
    return $conn;
}

sub update_entity {
    my ($entity, $params) = @_;
    my $entity_base = "SC-ENTITY=$entity, $SC_ENTITIES";
    my $mesg;
    my $conn = get_LDAP_conn();
    $logger->debug( "Modifying LDAP entity: $entity");
    foreach my $pair (@{$params}) {
        my ($attr_name, $attr_value) = @{$pair};
        $logger->debug( " Updating parameter pair ($attr_name, $attr_value)" );
        $mesg = $conn->modify($entity_base, replace => {$attr_name => $attr_value});
        if ($mesg->code){
            $logger->error( "Error in LDAP operation" );
            $logger->error( $msg->error );
        }
    }
    destroy_LDAP_conn();
}

sub destroy_LDAP_conn {
    my $conn = shift;
    $logger->debug( "Unbinding connector" );
    $conn->unbind();
    $logger->debug( "Disconnecting LDAP server" );
    $conn->disconnect();
}

