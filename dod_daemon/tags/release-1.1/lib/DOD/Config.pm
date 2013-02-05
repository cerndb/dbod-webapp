package DOD::Config;

use strict;
use warnings;
use Exporter;

use YAML::Syck;
use File::ShareDir;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $config, %cfg);

$VERSION     = 0.01;
@ISA         = qw(Exporter);
@EXPORT      = qw($config %cfg);
@EXPORT_OK   = qw($config %cfg);
%EXPORT_TAGS = ( );

# Load general configuration

#my $config_dir = File::ShareDir::dist_dir(__PACKAGE__);
my $config_dir = File::ShareDir::dist_dir('DOD');
$config = LoadFile( "$config_dir/dod.conf" );
%cfg = %{$config};
Log::Log4perl::init( "$config_dir/$config->{'LOGGER_CONFIG'}" );
my $logger = Log::Log4perl::get_logger( 'DOD.Config' );
$logger->debug( "Logger created" );
$logger->debug( "Loaded configuration from $config_dir" );
foreach my $key ( keys(%{$config}) ) {
    my %h = %{$config};
    $logger->debug( "\t$key -> $h{$key}" );
    }

1;
