#
# Database On Demand (DBOD) PostgreSQL server SPEC file
#
Summary: DB On Demand MySQL server 
Name: DBOD-PostgreSQL-server
Version: 9.2.4
Release: 0 
License: GPL
Source: postgresql-9.2.4.tar.gz
Group: Applications
URL: https://cern.ch/DBOnDemand/
Distribution: DBOD
Vendor: CERN
Packager: Ignacio Coterillo Coz <icoteril@cern.ch>

%description
DB On Demand PostgreSQL Server. 

RPM package for the PostgreSQL server. 
#Requires: 

%prep
%setup -n postgresql-9.2.4
./configure --prefix=/usr/local/pgsql/pgsql-9.2.4 \
--with-openssl \
--with-krb5 \
--with-pam \
--with-python \
--with-perl 

%build
gmake world

%install
gmake install
gmake install-docs
gmake install-world

%clean
exit 0

%files 
%defattr(-,postgres,postgres)
%dir /usr/local/pgsql/pgsql-9.2.4
%doc /usr/local/pgsql/pgsql-9.2.4/share/man
%doc /usr/local/pgsql/pgsql-9.2.4/share/doc

%changelog
* Tue Dec 10 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
