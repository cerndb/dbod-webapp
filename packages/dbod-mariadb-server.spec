#
# Database On Demand (DBOD) MariaDB server SPEC file
#

# Gets version of package from environment variable
%define version=$(echo $MARIADB_VERSION)

Summary: DB On Demand MariaDB server 
Name: DBOD-MariaDB-server
Version: %{version}
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
/usr/local/mysql/mariadb-%{version}/bin
/usr/local/mysql/mariadb-%{version}/COPYING
/usr/local/mysql/mariadb-%{version}/COPYING.LESSER
/usr/local/mysql/mariadb-%{version}/data
%doc /usr/local/mysql/mariadb-%{version}/docs
/usr/local/mysql/mariadb-%{version}/include
/usr/local/mysql/mariadb-%{version}/INSTALL-BINARY
/usr/local/mysql/mariadb-%{version}/lib
%doc /usr/local/mysql/mariadb-%{version}/man
%doc /usr/local/mysql/mariadb-%{version}/README
/usr/local/mysql/mariadb-%{version}/scripts
/usr/local/mysql/mariadb-%{version}/share
/usr/local/mysql/mariadb-%{version}/support-files
%attr(755,root,root) /etc/init.d/mariadb

%changelog
* Mon Dec 09 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
* Fri Dec 13 2013 Ignacio Coterillo <icoteril@cern.ch>
- Use of %{version}
