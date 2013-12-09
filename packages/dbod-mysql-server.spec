#
# Database On Demand (DBOD) MySQL server SPEC file
#
Summary: DB On Demand MariaDB server 
Name: DBOD-MySQL-server
Version: 5.5.30
Release: 0 
License: GPL
Group: Applications
URL: https://cern.ch/DBOnDemand/
Distribution: DBOD
Vendor: CERN
Packager: Ignacio Coterillo Coz <icoteril@cern.ch>

%description
DB On Demand MySQL Server. 

RPM package for the MySQL GA general Linux binary tarball distributed 
with customized startup script and destination folder.

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
/usr/local/mysql/mysql-5.5.30

%changelog
* Mon Dec 09 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
