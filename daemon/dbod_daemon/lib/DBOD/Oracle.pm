package DBOD::Oracle;

use strict;
use warnings;
use Exporter;

use File::Temp;
use POSIX qw(strftime);

use DBOD::Config qw( $config );
use DBOD::All qw( %job_status_table %instance_status_table );

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, $config_dir, $logger);

$VERSION     = 1.7;
@ISA         = qw(Exporter);
@EXPORT      = qw();
@EXPORT_OK   = qw();
%EXPORT_TAGS = ( );

# Load general configuration

INIT{
    $logger = Log::Log4perl::get_logger( 'DBOD.Oracle' );
    $logger->debug( "Logger created" );
} # INIT BLOCK

sub test_instance{
    my $entity = shift;
    $logger->debug( "Fetching state of entity $entity" );
    my $cmd = "/etc/init.d/syscontrol -i $entity ORACLE_ping -debug";
    my $res = `$cmd`;
    $logger->debug( "\n$res" );
    return $res;
    }

sub state_checker{
    my ($job, $code) = @_;
    my $entity = DBOD::All::get_entity($job);
    my $output = test_instance($entity);
    my $retcode = DBOD::All::result_code($output);
    my $job_state = job_status_table{$retcode};
    my $instance_state = instance_status_table{$retcode};
    $logger->debug( "Resulting states are: ($job_state, $instance_state)" );
    return ($job_state, $instance_state);
}

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
