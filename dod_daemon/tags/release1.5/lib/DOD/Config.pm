package DOD::Config;

use strict;
use warnings;
use Exporter;

use YAML::Syck;
use File::ShareDir;
use Log::Log4perl;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, %cfg, $logger_cfg);

$VERSION     = 0.01;
@ISA         = qw(Exporter);
@EXPORT      = qw( );
@EXPORT_OK   = qw( $config );
%EXPORT_TAGS = ( );

my $config_dir = File::ShareDir::dist_dir('DOD');
$config = LoadFile( "$config_dir/dod.conf" );
$logger_cfg = "$config_dir/$config->{'LOGGER_CONFIG'}";

%cfg = %{$config};

Log::Log4perl::init( $logger_cfg );

my $logger = Log::Log4perl::get_logger( 'DOD.Config' );
$logger->debug( "Logger created" );
$logger->debug( "Loaded configuration from $config_dir" );
foreach my $key ( keys(%{$config}) ) {
    my %h = %{$config};
    if ($key =~ /PASSWORD/) {
        $logger->debug( "\t$key -> XXXXXXXXX" );
    }
    else{
        $logger->debug( "\t$key -> $h{$key}" );
    }
}

1;
