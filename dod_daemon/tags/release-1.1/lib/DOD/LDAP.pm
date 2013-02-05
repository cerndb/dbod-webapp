package DOD::LDAP;

use strict;
use warnings;
use Exporter;

use Net::LDAP;
use Net::LDAP::Entry;

use DOD::Config qw( $config );

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, 
    $ldap_server, $ldap_user, $ldap_port, $ldap_protocol, $ldap_password,
    $ldap_userdn, $basedn, $sc_entities, $logger);

$VERSION     = 0.01;
@ISA         = qw(Exporter);
@EXPORT      = ( );
@EXPORT_OK   = ( );
%EXPORT_TAGS = ( );

INIT{
    $logger = Log::Log4perl::get_logger( 'DOD.LDAP' );
    $logger->debug( "Logger created" );
    $ldap_server = $config->{'LDAP_SERVER'};
    $ldap_user = $config->{'LDAP_USER'};
    $ldap_password = $config->{'LDAP_PASSWORD'}; # TO BE CHANGED
    $ldap_port = $config->{'LDAP_PORT'};
    $ldap_protocol = $config->{'LDAP_PROTOCOL'};
    $ldap_userdn = $config->{'LDAP_USERDN'};
    $basedn = $config->{'LDAP_BASE'};
    $sc_entities = $config->{'LDAP_BASE_ENTITIES'};
}

# Returns a LDAP server connection object
sub LDAPconnect {
    my ($url, $port, $protocol, $userdn, $pass) = @_;
    my $conn = Net::LDAP->new($url, port => $port, scheme => $protocol) or die("$@");
    my $msg = $conn->bind($userdn, password => $pass);
    if ($msg->code) {
        $logger->error( "Error connecting to LDAP server: $protocol://$url:$port $userdn");
    }
    return $conn;
}

# Returns an LDAP entry dump as a hash reference
sub getEntity {
    my $entity = shift;
    my $conn = LDAPconnect($ldap_server, $ldap_port, $ldap_protocol, $ldap_userdn, $ldap_password);
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
        $result{$attribute} = $entry->get_value($attribute);
    }
    $conn->unbind();
    $conn->disconnect();
    logger->debug("returning hashref: \%result");
    return \%result;
}


# Updates a list of parameters for a given LDAP entity
sub updateEntity {
    my ($entity, $params) = @_;
    my $conn = LDAPconnect($ldap_server, $ldap_port, $ldap_protocol, $ldap_userdn, $ldap_password);
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

1;
