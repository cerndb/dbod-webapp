package DBOD::All;

use strict;
use warnings;
use Exporter;

use DBOD::Config qw( $config );

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $logger, $job_status_table, $instance_status_table);

$VERSION     = 2.1;
@ISA         = qw(Exporter);
@EXPORT      = qw( );
@EXPORT_OK   = qw( $job_status_table $instance_status_table test_instance);
%EXPORT_TAGS = ( );

# Load general configuration

$logger = Log::Log4perl::get_logger( 'DBOD.All' );
$logger->debug( "Logger created" );

$job_status_table = {
    0 => 'FINISHED_OK',
    1 => 'FINISHED_FAIL',
    2 => 'TIMED_OUT',
    3 => 'FINISHED_WARNING'
};

$instance_status_table = {
    0 => 'RUNNING',
    1 => 'STOPPED',
    2 => 'BUSY',
};

sub get_entity {
    my $job = shift;
    return join('_', 'dod', $job->{'DB_NAME'});
}

sub get_host_from_entity{
    my $entity = shift;
    $logger->debug( "Fetching host corresponding to entity $entity" );
    my $cmd = "/ORA/dbs01/syscontrol/bin/netservicestab.sh sc_entity=$entity host";
    my $res = `$cmd`;
    chomp $res;
    $logger->debug( "Hostname: <$res>" );
    return $res;
    }

sub copy_to_entity{
    my ($item, $entity) = @_;
    my $host = get_host_from_entity($entity);
    my $cmd = "scp -r $item sysctl\@$host:/tmp";
    my $output = `$cmd`;
    $logger->debug( $cmd );
    $logger->debug( "Cmd Output: $output" );
    $logger->debug( "Cmd exit code: $?" );
    return $?;
}

sub write_file {
    my ($clob) = shift;    
    my ($fh, $filename) = File::Temp::tempfile( DIR => '/tmp' );
    $logger->debug( "Created temporary file $filename" );
    chmod(0644, $filename); # Remote user doing the reading will be sysctl
    open(FP, ">$filename") or $logger->error_die( "Error opening file\n $!" );
    print FP $clob;
    close(FP);
    return $filename;
}

sub result_code{
    my $log = shift;
    my @lines = split(/\n/, $log);
    my $code = undef;
    foreach (@lines){
        if ($_ =~ /\[(\d)\]/){
            $code = $1;
            print $_,"\n";
            print $code,"\n";
        }
    }
    if (defined $code){
        return int($code);
    }
    else{
        # If the command doesn't return any result code, we take it as bad
        return 1;
    }
}

sub test_instance{
    my ($entity, $type, $log) = @_;
    $logger->debug( "Fetching state of entity $entity" );
    my $cmd = "/etc/init.d/syscontrol -i $entity $type\_ping -debug";
    my $res = `$cmd`;
    if (defined $log) {
        $logger->debug( "\n$res" );
    }
    return $res;
    }

sub state_checker{
    my ($job, $code, $log) = @_;
    my $entity = get_entity($job);
    my $output = test_instance($entity, $job->{'TYPE'}, $log);
    my $retcode = result_code($output);
    my $job_state = $job_status_table->{$code};
    my $instance_state = $instance_status_table->{$retcode};
    $logger->debug( "Resulting states are: ($job_state, $instance_state)" );
    return ($job_state, $instance_state);
}

1;
