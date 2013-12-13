#
# Database On Demand (DBOD) PostgreSQL server SPEC file
#

# The version to be compiled must be passed via an environment variable
%define version %(echo $PG_VERSION)

Summary: DB On Demand MySQL server 
Name: DBOD-PostgreSQL-server
Version: %{version}
Release: 0 
License: GPL
Source: postgresql-%{version}.tar.gz
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
%setup -n postgresql-%{version}
./configure --prefix=/usr/local/pgsql/pgsql-%{version} \
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
%dir /usr/local/pgsql/pgsql-%{version}
%doc /usr/local/pgsql/pgsql-%{version}/share/man
%doc /usr/local/pgsql/pgsql-%{version}/share/doc

%changelog
* Tue Dec 10 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
* Thu Dec 12 2013 Ignacio Coterillo <icoteril@cern.ch>
- Added use of %{version}
