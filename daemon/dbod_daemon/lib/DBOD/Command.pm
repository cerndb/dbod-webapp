package DBOD::Command;

use strict;
use warnings;
use Exporter;

use DBOD::Config qw( $config );
use DBOD::All;
use DBOD::Database; 
use DBOD::Templates;

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $logger);

$VERSION     = 1.7;
@ISA         = qw(Exporter);
@EXPORT      = qw();
@EXPORT_OK   = qw();
%EXPORT_TAGS = ( );

# Load general configuration

INIT{
    $logger = Log::Log4perl::get_logger( 'DBOD.Command' );
    $logger->debug( "Logger created" );
} # INIT BLOCK

sub prepareCommand {
    my ($job, $dbh) = @_;
    eval{

        my $cmd;
        $logger->debug( "Fetching execution string" );
        my $exe_string = DBOD::Database::getExecString($job, $dbh);

        if (defined $exe_string){
            $cmd = $job->{'TYPE'} . '_' . lc($job->{'COMMAND_NAME'}) . ' ' . $exe_string;
            $logger->debug( "Unprocessed command line: $cmd " );
        }
        else{
            $cmd = $job->{'TYPE'} . '_' . lc($job->{'COMMAND_NAME'});
            $logger->debug( "Command line: $cmd" );
        }
        
        my $nparams = 0;
        if (defined(@{$job->{'PARAMS'}})) {
            $nparams = scalar(@{$job->{'PARAMS'}});
        }
        
        my $expected_nparams = 0;
        my $optional_nparams = 0;
        $expected_nparams++ while ($cmd =~ m/:/g);
        $optional_nparams++ while ($cmd =~ m/#/g);
        
        $logger->debug("# of parameters: $nparams, Expected: $expected_nparams, Optional: $optional_nparams");

        if ($expected_nparams == 0) {
            return $cmd;
        }

        # UPLOAD_CONFIG commands

        if ($job->{'COMMAND_NAME'} eq 'UPLOAD_CONFIG') {

            my @buf = grep($_->{'NAME'} =~ /FILE/, @{$job->{'PARAMS'}});
            my $clob = $buf[0]->{'VALUE'};
            @buf = grep($_->{'NAME'} =~ /TYPE/, @{$job->{'PARAMS'}});
            my $filetype = $buf[0]->{'VALUE'};
            
            $logger->debug("Fetching parser for config file type: $filetype");
            my $parser = DBOD::Templates::parser( $filetype );
            $logger->debug("parser: $parser");
            my $filename = $parser->($clob, $filetype); 

            # Distribute file to target entity
            my $entity = DBOD::All::get_entity($job);
            $logger->debug( "Copying file to target entity: $entity" );
            DBOD::All::copy_to_entity( $filename, $entity );
            
            $cmd =~ s/:CONFIG_FILE=/$filename/;
            $cmd =~ s/:CONFIG_TYPE=/$filetype/;
            $logger->debug( "Processed command line: $cmd" );
            
            #$logger->debug( "Deleting temporal files");
            #system("rm -fr $filename");

            return $cmd;

        }

        # Regular execution

        if ($nparams >= $expected_nparams){

            # Parameter substitution
            $logger->debug( "Substituting params");
            foreach my $param (@{$job->{'PARAMS'}}){
                $logger->debug( "param name: " . $param->{'NAME'} . "param value: " . $param->{'VALUE'} );
                $logger->debug(" Cmd : $cmd" );
                $cmd =~ s/:$param->{'NAME'}=/$param->{'VALUE'}/;
                $logger->debug(" Cmd (mandatory): $cmd" );
                $cmd =~ s/#$param->{'NAME'}=/$param->{'VALUE'}/;
                $logger->debug(" Cmd (optional): $cmd" );
                }

            # Checks that all mandatory parameter have been substituted
            my $buf = 0;
            $buf++ while ($cmd =~ m/:(.*)+=/g);
            if ($buf) {
                $logger->error( "Some of the command parameters have not been substituted\n $!" );
                return undef;
                }

            # Checks optional parameter substitution
            $buf = 0;
            $buf++ while ($cmd =~ m/#(.*)+=/g);
            if ($buf) {
                $logger->debug( "Some of the Optional command parameters have not been substituted" );
                $logger->debug( "Cmd line: $cmd" );
                $logger->debug( "Removing placeholders" );
                my @items = split( /-/, $cmd);
                my @result;
                for my $item (@items){
                    if ($item !~ m/#(.*)+=/){
                        $logger->debug("Item: $item");
                        push(@result, $item);
                    }
                }
                $cmd = join('-', @result);
                $logger->debug( "Processed commandline: $cmd" );
                return $cmd;
                }
            return $cmd;
        }
        else {
            $logger->error( "The number of parameters is wrong. $expected_nparams expected, $nparams obtained." );
            return undef;
        }
        1;
    } or do {
        $logger->error( "Unable to prepare command: $!" );
        return undef;
    };
}



END{

}

1;
