package DBOD::MySQL;

use strict;
use warnings;
use Exporter;

use POSIX qw(strftime);

use DBOD::Config qw( $config );
use DBOD::Database;
use DBOD::All qw( $job_status_table $instance_status_table );
use DBOD::LDAP;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $logger,);

$VERSION     = 2.1;
@ISA         = qw(Exporter);
@EXPORT      = qw();
@EXPORT_OK   = qw();
%EXPORT_TAGS = ( );

# Load general configuration

BEGIN{
    $logger = Log::Log4perl::get_logger( 'DBOD.MySQL' );
    $logger->debug( "Logger created" );
} # BEGIN BLOCK

sub get_variable{
    my ($entity, $varname) = @_;
    $logger->debug( "Fetching state of entity $entity" );
    my $cmd = "/etc/init.d/syscontrol -i $entity MYSQL_get_status -entity $entity -variable $varname";
    my $output = `$cmd`;
    $logger->debug( "\n$output" );
    return $output;
    }

sub get_version{
    my $entity = shift;
    my $cad = get_variable($entity, 'version');
    my @buf = split(/-/, $cad); # We have to remove the -log at the end of the version string
    return $buf[0];
    }

sub upgrade_callback{
    my ($job, $dbh) = @_;
    my $entity = DBOD::All::get_entity($job);
    eval{
        my $version;
        my $params = $job->{'PARAMS'};
        foreach (@{$params}){
            if ($_->{'NAME'} =~ /VERSION_TO/){
                $version = $_->{'VALUE'};
                }
            }
        $logger->debug( "Updating $entity version to $version in DB");
        DBOD::Database::updateInstance($job, 'VERSION', $version, $dbh);
        $logger->debug( "Updating $entity version to $version in LDAP");
        my $date = strftime "%H:%M:%S %m/%d/%Y", localtime;
        DBOD::LDAP::updateEntity($entity, [['SC-VERSION', $version],
                                          ['SC-COMMENT', "Upgraded at $date"],
                                          ['SC-BINDIR-LOCATION', "/usr/local/mysql/mysql-$version/bin"],
                                          ['SC-BASEDIR-LOCATION', "/usr/local/mysql/mysql-$version"]]);
        1;
    } or do {
        $logger->error( "A problem occured when trying to update $entity version");
        return undef;
    };
}

1;
