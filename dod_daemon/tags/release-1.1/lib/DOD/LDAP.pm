package DOD::LDAP;

use strict;
use warnings;
use Exporter;

use File::ShareDir;
use Log::Log4perl;
use Net::LDAP;
use Net::LDAP::Entry;
use DOD;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, 
    $ldap_server, $ldap_user, $ldap_port, $ldap_protocol, $ldap_password,
    $ldap_userdn, $basedn, $sc_entities, $logger);

$VERSION     = 0.01;
@ISA         = qw(Exporter);
@EXPORT      = qw(getEntity updateEntity);
@EXPORT_OK   = ( );
%EXPORT_TAGS = ( );

BEGIN{
    $ldap_server = $DOD::config->{'LDAP_SERVER'};
    $ldap_user = $DOD::config->{'LDAP_USER'};
    $ldap_password = $DOD::config->{'LDAP_PASSWORD'}; # TO BE CHANGED
    $ldap_port = $DOD::config->{'LDAP_PORT'};
    $ldap_protocol = $DOD::config->{'LDAP_PROTOCOL'};
    $ldap_userdn = $DOD::config->{'LDAP_USERDN'};
    $basedn = $DOD::config->{'LDAP_BASE'};
    $sc_entities = $DOD::config->{'LDAP_BASE_ENTITIES'};

    my $config_dir = File::ShareDir::dist_dir( "DOD" );
    Log::Log4perl::init( "$config_dir/$DOD::config->{'LOGGER_CONFIG'}" );
    $logger = Log::Log4perl::get_logger( 'DOD.LDAP' );
    $logger->debug( "Logger created" );
    
}

# Returns a LDAP server connection object
sub LDAPconnect {
    if ($#_) {
       ($url, $port, $protocol, $userdn, $pass) = @_;
    }
    my $conn = Net::LDAP->new($url, port => $port, scheme => $protocol) or die("$@");
    $msg = $conn->bind($userdn, password => $pass);
    if ($msg->code) {
        $logger->error( "Error connecting to LDAP server: $protocol://$url:$port as $user");
    }
    return $conn;
}

# Returns an LDAP entry dump as a hash reference
sub getEntity {
    $entity = shift;
    $conn = LDAPconnect();                                                                      

    my $filter = "(&(SC-ENTITY=$entity)(SC-DOMAIN=DOD))";
    logger->debug( "Searching LDAP entity with filter: $filter ");
    my $mesg = $conn->search(
            base => $basedn,
            timelimit => 50,
            scope => 'sub',
            filter => $filter);

    my @entries = $mesg->entries;
    my $entry = $entries[0];
    my %result;
    foreach my $attribute ($entry->attributes){
        $result{$attribute} = $entry->get_value($attrbute);
    }
    $conn->unbind();
    $conn->disconnect();
    logger->debug("returning hashref: \%result");
    return \%result;
}


# Updates a list of parameters for a given LDAP entity
sub updateEntity {
    my ($entity, $params) = @_;
    my $conn = LDAP_connect($ldap_server, $ldap_port, $ldap_protocol, $userdn, $ldap_password);
    my $entity_base = "SC-ENTITY=$entity, $sc_entities";
    my $mesg;
    foreach my $pair (@{$params}) {
        my ($attr_name, $attr_value) = @{$pair};
        $mesg = $conn->modify($entity_base, replace => {$attr_name => $attr_value});
        $mesg->code && die $mesg->error;
    }
    $conn->unbind();
    $conn->disconnect();
}


