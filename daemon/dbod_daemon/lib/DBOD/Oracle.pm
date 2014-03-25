package DBOD::Oracle;

use strict;
use warnings;
use Exporter;

use POSIX qw(strftime);

use DBOD::Config qw( $config );
use DBOD::All;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, $config_dir, $logger);

$VERSION     = 2.1;
@ISA         = qw(Exporter);
@EXPORT      = qw();
@EXPORT_OK   = qw();
%EXPORT_TAGS = ( );

# Load general configuration

INIT{
    $logger = Log::Log4perl::get_logger( 'DBOD.Oracle' );
    $logger->debug( "Logger created" );
} # INIT BLOCK

sub upgrade_callback{
    my $job = shift;
    my $entity = DBOD::All::get_entity($job);
    eval{
        my $version = get_version($entity);
        $logger->debug( "Updating $entity version to $version in DB");
        DBOD::Database::updateInstance($job, 'VERSION', $version);
        $logger->debug( "Updating $entity version to $version in LDAP");
        my $date = strftime "%H:%M:%S %m/%d/%Y", localtime;
        DBOD::LDAP::updateEntity($entity, [['SC-VERSION', $version],['SC-COMMENT', "Upgraded at $date"]]);
        1;
    } or do {
        $logger->error( "A problem occured when trying to update $entity version");
        return undef;
    };
}

1;
