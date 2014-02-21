#
# Database On Demand (DBOD) PostgreSQL server SPEC file
#

# The version to be compiled must be passed via an environment variable
%define version %(echo $PG_VERSION)

Summary: DB On Demand PostgreSQL server 
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
# Creates folder for storing the socket
mkdir -p /var/lib/pgsql
chown postgres:postgres /var/lib/pgsql

%clean
exit 0

%files 
%defattr(-,postgres,postgres)
/usr/local/pgsql/pgsql-%{version}/bin
/usr/local/pgsql/pgsql-%{version}/include
/usr/local/pgsql/pgsql-%{version}/lib
/usr/local/pgsql/pgsql-%{version}/share/extension
/usr/local/pgsql/pgsql-%{version}/share/timezone
/usr/local/pgsql/pgsql-%{version}/share/timezonesets
/usr/local/pgsql/pgsql-%{version}/share/tsearch_data
/usr/local/pgsql/pgsql-%{version}/share/conversion_create.sql
/usr/local/pgsql/pgsql-%{version}/share/information_schema.sql
/usr/local/pgsql/pgsql-%{version}/share/pg_hba.conf.sample
/usr/local/pgsql/pgsql-%{version}/share/pg_ident.conf.sample
/usr/local/pgsql/pgsql-%{version}/share/pg_service.conf.sample
/usr/local/pgsql/pgsql-%{version}/share/postgres.bki
/usr/local/pgsql/pgsql-%{version}/share/postgres.description
/usr/local/pgsql/pgsql-%{version}/share/postgresql.conf.sample
/usr/local/pgsql/pgsql-%{version}/share/postgres.shdescription
/usr/local/pgsql/pgsql-%{version}/share/psqlrc.sample
/usr/local/pgsql/pgsql-%{version}/share/recovery.conf.sample
/usr/local/pgsql/pgsql-%{version}/share/snowball_create.sql
/usr/local/pgsql/pgsql-%{version}/share/sql_features.txt
/usr/local/pgsql/pgsql-%{version}/share/system_views.sql
%docdir /usr/local/pgsql/pgsql-%{version}/share/man
%docdir /usr/local/pgsql/pgsql-%{version}/share/doc
%attr (-, postgres, postgres) /var/lib/pgsql

# Post-installation
%post
/sbin/ldconfig

# Post-uninstallation
%postun
/sbin/ldconfig

%changelog
* Thu Jan 30 2014 Ignacio Coterillo <icoteril@cern.ch>
- Changed Summary, plus included documentation, and socket folder, Uses docdir, post and post-un sections
* Thu Dec 12 2013 Ignacio Coterillo <icoteril@cern.ch>
- Added use of %{version}
* Tue Dec 10 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
