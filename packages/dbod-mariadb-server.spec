#
# Database On Demand (DBOD) MariaDB server SPEC file
#
Summary: DB On Demand MariaDB server 
Name: DBOD-MariaDB-server
Version: 5.5.34
Release: 0 
License: GPL
Group: Applications
URL: https://cern.ch/DBOnDemand/
Distribution: DBOD
Vendor: CERN
Packager: Ignacio Coterillo Coz <icoteril@cern.ch>

%description
DB On Demand MariaDB Server. 

RPM package for the MariaDB General Linux binary tarball distributed 
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
/usr/local/mysql/mariadb-5.5.34/bin
/usr/local/mysql/mariadb-5.5.34/COPYING
/usr/local/mysql/mariadb-5.5.34/COPYING.LESSER
/usr/local/mysql/mariadb-5.5.34/data
%doc /usr/local/mysql/mariadb-5.5.34/docs
/usr/local/mysql/mariadb-5.5.34/include
/usr/local/mysql/mariadb-5.5.34/INSTALL-BINARY
/usr/local/mysql/mariadb-5.5.34/lib
%doc /usr/local/mysql/mariadb-5.5.34/man
%doc /usr/local/mysql/mariadb-5.5.34/README
/usr/local/mysql/mariadb-5.5.34/scripts
/usr/local/mysql/mariadb-5.5.34/share
/usr/local/mysql/mariadb-5.5.34/support-files
%attr(755,root,root) /etc/init.d/mariadb




%changelog
* Mon Dec 09 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
