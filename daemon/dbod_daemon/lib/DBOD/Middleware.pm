package DBOD::Middleware;

use strict;
use warnings;
use Exporter;

use YAML::Syck;
use File::ShareDir;
use Log::Log4perl;
use File::Temp;

use DBOD::Database;
use DBOD::All;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, $config_dir, $logger,);

$VERSION     = 0.03;
@ISA         = qw(Exporter);
@EXPORT      = qw(state_checker);
@EXPORT_OK   = qw();
%EXPORT_TAGS = ( );

# Load general configuration

BEGIN{
    $logger = Log::Log4perl::get_logger( 'DBOD.Middleware' );
    $logger->debug( "Logger created" );
} # BEGIN BLOCK

sub state_checker{
    my ($job, $code) = @_;
    my ($job_state, $instance_state);
    if ($code){
        $job_state = "FINISHED_FAIL";
    }
    else{
        $job_state = "FINISHED_OK";
    }
    # In the case of the MIDDLEWARE we do not want to check the entity/machines status here. 
    # In this way we do not need to touch the source code of the DOD package (dispatcher)
    $instance_state = "RUNNING";
    $logger->debug( "MIDDLEWARE always return 0 here (We will check it for it via SYSCONTROL MWMGR)" );
    return ($job_state, $instance_state);
}

1;
