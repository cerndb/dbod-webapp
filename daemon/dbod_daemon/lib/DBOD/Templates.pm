package DBOD::Templates;

use strict;
use warnings;
use Exporter;

use File::Temp;

use DBOD::Config qw($config);

our ($VERSION, @ISA, @EXPORT, @EXPORT_OK, %EXPORT_TAGS, $logger, %processors);

$VERSION     = 0.03;
@ISA         = qw( Exporter );
@EXPORT      = ();
@EXPORT_OK   = qw( parser );
%EXPORT_TAGS = ( );

# Load general configuration

INIT{
    $logger = Log::Log4perl::get_logger( 'DBOD.Templates' );
    $logger->debug( "Logger created" );
} # INIT BLOCK

sub MYSQL_parser
{
    my $clob = shift;
    my (%hash, $section, $keyword, $value);
    my $buf = {};
    my @lines = split(/\n/, $clob);
    foreach (@lines) {
        next if /^#/ or /^(\s)*$/;
        chomp;
        if (/^\s*\[(\w+)\].*/) {
            if (defined $buf && defined $section){
                $hash{$section} = $buf ;
                $buf = {};
                }
            $section = $1;
        }
        elsif (/.*=.*/) {
            my ($k,$v) = split(/\s*=\s*/, $_);
            $keyword = $k;
            $value = $v ;
            $buf->{$keyword} = $value;
        }
        elsif (/^s*(.*)?$/){
            $keyword = $1;
            $buf->{$keyword} = "";
        }
    }
        
    # Neccesary if only one section is defined in the file
    if (defined $buf && defined $section){
        $hash{$section} = $buf;
    }
    return \%hash;

}

sub MYSQL_writeFile{
    my $hashref = shift;
    #my $tempdir = File::Temp::tempdir( 'dod_job_XXXX', DIR => '/tmp/', CLEANUP => 1 );
    my $tempdir = '/tmp/';
    $logger->debug( "Created temporary folder $tempdir" );
    my ($fh, $filename) = File::Temp::tempfile( DIR => $tempdir );
    $logger->debug( "Created temporary file $filename" );
    chmod(0644, $filename); # Remote user doing the reading will be sysctl
    open(FP, ">$filename") or $logger->error_die( "Error opening file\n $!" );
    while ( my($section, $valueref) = each( %{$hashref} ) ){
        print FP "[$section]\n";
        my %values = %{$valueref};
        foreach (keys (%values)){
            if ($values{$_}){
                print FP "$_ = $values{$_}\n";
            }
            else{
                print FP "$_\n";
            }
        }
        print FP "\n";
    }
    close(FP);
    return $filename;
}

sub MYSQL_enforce{
    my ($new_config, $filename) = @_;
    my $template;
    my $config_dir = File::ShareDir::dist_dir( "dbod_daemon" );
    my $template_ref = YAML::Syck::LoadFile( "$config_dir/templates/$filename" );
    my $res = {};
    my %template = %{$template_ref};
    
    while ( my($section, $paramsref) = each( %{$new_config} ) ){
        $logger->debug(" Creating section: [$section] ");
        my %new_params = %{$paramsref};
        my $buf = {};
        while ( my ($p_name, $p_value) = each (%new_params) ){
            $logger->debug(" Adding: $section: ($p_name, $p_value) ");
            $buf->{$p_name} = $p_value;
        }
        $res->{$section} = $buf;
    }
   
    # At this poing parsed version is OK.

    # Enforces pre-defined parameters. Parameters marked for deletion
    # are added with negative value for their posterior deletion
    
    foreach my $section_name (keys %{$res}){
        my $section_ref = $template{$section_name};
        my %section = %{$section_ref};
        my $new_paramsref = $res->{$section_name};
        my %new_params = %{$new_paramsref}; 
        my %buf = ();
       
        foreach my $param_name (keys %new_params){
            if (exists $section{$param_name}){
                my $param_value = $section{$param_name};
                # Param type checks;
                if (defined ($param_value)){
                    # If the parameter is defined, is a array or an scalar
                    if (ref($param_value)){
                        # The parameter has a range of definition
                        my ($min, $max) = @{$param_value};
                        my $default = $max;
                        my $pval  = $new_params{$param_name} ;
                        if ($pval){
                            $pval = $pval . "regexp_token";
                            $pval =~ s/\D//g;
                            $min =~ s/\D//g;
                            $max =~ s/\D//g;
                            if (defined($new_params{$param_name}) 
                                    and( $pval >= $min) 
                                    and ($pval <= $max)){
                                $logger->debug(" Enforcing r_value [$section_name] : ($param_name, $min, $max, $pval)");
                                $buf{$param_name} = $new_params{$param_name};
                            }
                            else{
                                $logger->debug(" Enforcing r_value [$section_name] : ($param_name, $min, $max, default) ");
                                $buf{$param_name} = $default;
                            }
                        }
                    }
                    else{
                        # The parameter is an scalar
                        my $pval = $param_value;
                        my $nval = $pval;
                        $nval =~ s/\D//g;
                        $logger->debug(" Escalar pval, nval : $pval, $nval ");
                        if (($nval ne "") and ($pval < 0)) {
                            # Clean parameters marked for deletion
                           $logger->debug(" Deleting e_value [$section_name]: $param_name ");
                           delete $buf{$param_name};
                        }
                        else{
                            $logger->debug(" Enforcing e_value [$section_name] : $param_name ");
                            $buf{$param_name} = $param_value; 
                        }
                    }
                }
                else{
                    # The parameter is set just by name mention
                    $logger->debug(" Enforcing m_value [$section_name] : $param_name ");
                    $buf{$param_name} = ''; # Empty param
                }
            }
            else{
                $buf{$param_name} = $new_params{$param_name};
            }
        }
        $res->{$section_name} = \%buf;
    }

    return $res;
}

sub MYSQL_process {
    my $clob = shift;
    $logger->debug( "clob:\n$clob" );
    my $parsed = MYSQL_parser( $clob );
    $logger->debug( "parsed: $parsed" );
    my $enforced = MYSQL_enforce( $parsed, 'MY_CNF' );
    $logger->debug( "enforced: $enforced" );
    my $filename = MYSQL_writeFile( $enforced );
    return $filename;
}

sub PG_writeFile {
    my ($hashref, $type) = @_;    
    my ($fh, $filename) = File::Temp::tempfile( DIR => '/tmp' );
    $logger->debug( "Created temporary file $filename" );
    chmod(0644, $filename); # Remote user doing the reading will be sysctl
    open(FP, ">$filename") or $logger->error_die( "Error opening file\n $!" );
    if ($type eq 'HBA'){
        $logger->debug( 'Passing through HBA clob to file' );
        print FP $hashref;
    }
    else { # PG
        $logger->debug( 'Converting PG hash to file');
        my %hash = %{$hashref};
        foreach (keys (%hash)){
                print FP "$_ = $hash{$_}\n";
            }
    }
    close(FP);
    return $filename;
}


sub PG_enforce {
    my ($new_config, $filename) = @_;
    my $template;
    my $config_dir = File::ShareDir::dist_dir( "dbod_daemon" );
    my $template_ref = YAML::Syck::LoadFile( "$config_dir/templates/$filename" );
    my $res = {};
    foreach my $key (keys (%{$new_config})) {
        if (exists($template_ref->{$key})) {
            my $buf = $template_ref->{$key};
            $logger->debug( "Enforcing template value ($key: $buf)" );
            if (ref($buf) eq 'ARRAY'){
                my ($min, $max, $default) = @{$buf};
                $logger->debug( "Range parameter check (min, max, default): ($min, $max, $default)" );
                if (($new_config->{$key} >= $min) && ($new_config->{$key} <= $max)) {
                    $logger->debug( 'Parameter value accepted');
                    $res->{$key} = $new_config->{$key};
                }
                else {
                    # Default value
                    $logger->debug( 'Parameter defaulted' );
                    $res->{$key} = $default;
                }
            }
            else {
                $logger->debug( 'Parameter enforced' );
                $res->{$key} = $template_ref->{$key};
            }
        }
        else {
            # Take value from new configuration file
            $res->{$key} = $new_config->{$key};
        }
    }
    return $res;
}

sub PG_parser
{
    my ($clob, $type) = @_;
    my (%hash, $keyword, $value);
    my @lines = split(/\n/, $clob);
    foreach (@lines) {
        next if /^#/ or /^(\s)*$/;
        chomp;
        if (/.*=.*/) {
            my ($k,$v) = split(/\s*=\s*/, $_);
            $keyword = $k;
            $value = $v ;
            $hash{$keyword} = $value;
        }
    }
    return \%hash;
}

sub PG_process {
    my ($clob, $type) = @_;
    if ($type eq 'HBA') {
        $logger->debug( 'Passing through HBA config file' );
        return PG_writeFile($clob, $type)
    }
    else{
        $logger->debug( 'Parsing PG configuration file' );
        $logger->debug( "type: $type, clob:\n$clob" );
        my $parsed = PG_parser( $clob, $type );
        $logger->debug( "parsed: $parsed" );
        my $enforced = PG_enforce( $parsed, $type );
        $logger->debug( "enforced: $enforced" );
        return PG_writeFile( $enforced );
    }
}

sub parser{
    my $type = shift;
    $logger->debug( "Returning parser for $type");
    return $processors{$type};
}

%processors = ( 
    'MY_CNF' => \&MYSQL_process,
    'PG' => \&PG_process,
    'HBA' => \&PG_process
    );


# End of Module
END{

}

1;
