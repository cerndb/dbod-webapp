use inc::Module::Install;

author      'Ignacio Coterillo <ignacio.coterillo@cern.ch>';
version    '1.5';
license     'perl';

name          'dbod_daemon';
requires      'Log::Log4perl' => 0;
requires      'YAML::Syck' => 0;
requires      'File::ShareDir' => 0;
requires      'DBI' => 0;
requires      'DBD::Oracle' => 0;
requires      'Proc::Daemon' => 0.14;
requires      'Net::LDAP' => 0;

install_script 'scripts/dbod_daemon';
install_script 'scripts/dbod_state_checker';
install_script 'scripts/dbod_daemon_monitor';
install_share 'share';

WriteAll;

