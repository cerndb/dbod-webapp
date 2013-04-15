use inc::Module::Install;

author      'Ignacio Coterillo <ignacio.coterillo[at]cern[dot]ch>';
version    '5.0';
license     'perl';

name          'DOD';
requires      'Log::Log4perl' => 0;
requires      'YAML::Syck' => 0;
requires      'File::ShareDir' => 0;
requires      'DBI' => 0;
requires      'DBD::Oracle' => 0;
requires      'Proc::Daemon' => 0.14;
requires      'Net::LDAP' => 0;

install_script 'scripts/dod_daemon';
install_script 'scripts/dod_state_checker';
install_share 'share';

WriteAll;

