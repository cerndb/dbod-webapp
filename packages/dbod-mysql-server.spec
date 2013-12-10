#
# Database On Demand (DBOD) MySQL server SPEC file
#
Summary: DB On Demand MySQL server 
Name: DBOD-MySQL-server
Version: 5.5.35
Release: 0 
License: GPL
Group: Applications
URL: https://cern.ch/DBOnDemand/
Distribution: DBOD
Vendor: CERN
Packager: Ignacio Coterillo Coz <icoteril@cern.ch>

%description
DB On Demand MySQL Server. 

RPM package for the MySQL General Linux binary tarball distributed 
with customized startup script and destination folder.

Tests and SQL benchmarks are not packaged to avoid extra Perl
library dependencies.

#Requires: 

%prep
exit 0

%build
exit 0

%install
exit 0

%clean
exit 0

%files 
%defattr(-,mysql,mysql)
/usr/local/mysql/mysql-5.5.35/bin
/usr/local/mysql/mysql-5.5.35/COPYING
/usr/local/mysql/mysql-5.5.35/data
%doc /usr/local/mysql/mysql-5.5.35/docs
/usr/local/mysql/mysql-5.5.35/include
/usr/local/mysql/mysql-5.5.35/INSTALL-BINARY
/usr/local/mysql/mysql-5.5.35/lib
%doc /usr/local/mysql/mysql-5.5.35/man
%doc /usr/local/mysql/mysql-5.5.35/README
/usr/local/mysql/mysql-5.5.35/scripts
/usr/local/mysql/mysql-5.5.35/share
/usr/local/mysql/mysql-5.5.35/support-files
%attr(755,root,root) /etc/init.d/mysql

%changelog
* Mon Dec 09 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
