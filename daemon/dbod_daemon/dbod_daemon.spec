#
# Database On Demand (DBOD) Damon SPEC file
#
Summary: DB On Demand spec file
Name: dbod_daemon
Version: 1
Release: 5
Copyright: GPL
Group: Applications/Sound
Source: https://git.cern.ch/reps/DBOnDemand 
URL: https://cern.ch/DBOnDemand/
Distribution: DBOD
Vendor: CERN
Packager: Ignacio Coterillo Coz <icoteril@cern.ch>

%description
DBOD job dispatching daemon

%prep
#rm -rf $RPM_BUILD_DIR/dbod_daemon
#zcat $RPM_SOURCE_DIR/dbod_daemon-1.5.tgz | tar -xvf -
%setup

%build
perl Makefile.PL
make

%install
make install

%files
%doc 
/usr/

%changelog
* Mon Apr 15 2013 Ignacio Coterillo <icoteril@cern.ch>
- Initial packaging
