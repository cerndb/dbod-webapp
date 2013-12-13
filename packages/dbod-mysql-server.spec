#
# Database On Demand (DBOD) MySQL server SPEC file
#

# The version to be compiled must be passed via an environment variable
%define version %(echo $MYSQL_VERSION)

Summary: DB On Demand MySQL server 
Name: DBOD-MySQL-server
Version: %{version}
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
/usr/local/mysql/mysql-%{version}/bin
/usr/local/mysql/mysql-%{version}/COPYING
/usr/local/mysql/mysql-%{version}/data
%doc /usr/local/mysql/mysql-%{version}/docs
/usr/local/mysql/mysql-%{version}/include
/usr/local/mysql/mysql-%{version}/INSTALL-BINARY
/usr/local/mysql/mysql-%{version}/lib
%doc /usr/local/mysql/mysql-%{version}/man
%doc /usr/local/mysql/mysql-%{version}/README
/usr/local/mysql/mysql-%{version}/scripts
/usr/local/mysql/mysql-%{version}/share
/usr/local/mysql/mysql-%{version}/support-files
%attr(755,root,root) /etc/init.d/mysql

%changelog
* Mon Dec 09 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
* Fri Dec 13 2013 Ignacio Coterillo <icoteril@cern.ch>
- Added use of %{version}
