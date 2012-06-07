package DOD::Oracle;

use strict;
use warnings;
use Exporter;

use YAML::Syck;
use File::ShareDir;
use Log::Log4perl;
use File::Temp;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, $config_dir, $logger);

$VERSION     = 0.03;
@ISA         = qw(Exporter);
@EXPORT      = qw(getJobList updateJobState updateJobCompletionDate updateJobLog finishJob getDBH);
@EXPORT_OK   = qw(getDBH);
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
    my ($job_state, $instance_state);
    if ($code){
        $job_state = "FINISHED_FAIL";
    }   
    else{
        $job_state = "FINISHED_OK";
    }
    my $entity = DOD::All::get_entity($job);
    my $output = test_instance($entity);
    my $retcode = DOD::All::result_code($output);
    if ($retcode) {
        $instance_state = "STOPPED";
    }   
    else{
        $instance_state = "RUNNING";
    }
    $logger->debug( "Resulting states are: ($job_state, $instance_state)" );
    return ($job_state, $instance_state);
}

END {
1;
}
