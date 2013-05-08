package DBOD::Commands;

use strict;
use warnings;
use Exporter;

use DBOD::Config qw( $config );
use DBOD::All;
use DBOD::Database; 
use DBOD::Templates;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $logger);

$VERSION     = 0.03;
@ISA         = qw(Exporter);
@EXPORT      = qw();
@EXPORT_OK   = qw();
%EXPORT_TAGS = ( );

# Load general configuration

INIT{
    $logger = Log::Log4perl::get_logger( 'DBOD.Commands' );
    $logger->debug( "Logger created" );
} # INIT BLOCK

sub prepareCommand {
    my ($job, $dbh) = @_;
    my $cmd;
    eval{
        $logger->debug( "Fetching execution string" );
        my $exe_string = DBOD::Database::getExecString($job, $dbh);
        if (defined $exe_string){
            $cmd = $job->{'TYPE'} . '_' . lc($job->{'COMMAND_NAME'}) . ' ' . $exe_string;
        }
        else{
            $cmd = $job->{'TYPE'} . '_' . lc($job->{'COMMAND_NAME'});
        }
        $logger->debug( " $cmd " );
        $logger->debug( "Fetching Job params" );
        my $params = $job->{'PARAMS'};
        my $nparams;
        if (defined $params){
            $nparams = scalar(@{$params});
            }
        else{
            $nparams = 0;
        }
        my $expected_nparams = 0;
        my $optional_nparams = 0;
        $expected_nparams++ while ($cmd =~ m/:/g);
        $optional_nparams++ while ($cmd =~ m/#/g);
        $logger->debug("Expected: $expected_nparams, Optional: $optional_nparams");

        if ($expected_nparams == 0){
            $logger->debug("No parameters, returning cmd: $cmd " );
            return $cmd;
        }
        if ($nparams >= $expected_nparams){
            $logger->debug( "Substituting params");
            foreach my $param (@{$params}){
                if ($param->{'NAME'} =~ /FILE/){
                    my ($pname, $type) = split( /=/, $param->{'NAME'} );
                    $logger->debug("pname: $pname, type: $type");
                    my $clob = $param->{'VALUE'};
                    my $parser = DBOD::Templates::parser( $type );
                    $logger->debug("parser: $parser");
                    my $filename = $parser->($clob); 
                    $cmd =~ s/:$param->{'NAME'}/$filename/;
                    $logger->debug( "cmd: $cmd" );
                    # Distribute required files
                    $logger->debug("Fetching entity name");
                    my $entity = DBOD::All::get_entity($job);
                    $logger->debug( "Copying files to $entity" );
                    DBOD::All::copy_to_entity( $filename, $entity );
                    $logger->debug( "Deleting temporal files");
                    system("rm -fr $filename");
                    }
                else{
                    $cmd =~ s/:$param->{'NAME'}=/$param->{'VALUE'}/;
                    $cmd =~ s/#$param->{'NAME'}=/$param->{'VALUE'}/;
                    }
                }
            my $buf = 0;
            $buf++ while ($cmd =~ m/:(.*)+=/g);
            if ($buf) {
                $logger->error( "Some of the command parameters could not be parsed\n $!" );
                }
            $buf = 0;
            $buf++ while ($cmd =~ m/#(.*)+=/g);
            if ($buf>0) {
                $logger->debug( "Some of the Optional command parameters could not be parsed" );
                $logger->debug( "Buf: $buf, cmd: $cmd" );
                my @items = split( /-/, $cmd);
                my @result;
                for my $item (@items){
                    if ($item !~ m/#(.*)+=/){
                        $logger->debug("Item: $item");
                        push(@result, $item);
                    }
                }
                $cmd = join('-', @result);
                $logger->debug( "Num. optional params: $buf, \ncmd: $cmd" );
                }
        }
        else {
            $logger->error( "The number of parameters is wrong. $expected_nparams expected, $nparams obtained.\n $!" );
            $cmd = undef;
        }
        1;
    } or do {
        $logger->error( "Unable to prepare command\n $!" );
        $cmd = undef;
    };
    return $cmd;
}
