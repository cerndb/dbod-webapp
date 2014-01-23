ARCH=x86_64
DBODUTILS=/ORA/dbs03/DODUTILS
# This folder is an NFS mount @ dbnasg403:/vol/dodutils
RELEASE=0
PKGSPATH=$(DBODUTILS)/rpms
RPMBUILD=rpmbuild
RPMFLAGS=-bb
SRCPATH=/usr/src/redhat
SPECPATH=$(SRCPATH)/SPECS
SOURCESPATH=$(SRCPATH)/SOURCES
RPMPATH=$(SRCPATH)/RPMS/$(ARCH)

install-daemon:
	cd daemon/dbod_daemon && \
	make clean && perl Makefile.PL && make install

daemon/dbod_daemon/dbod_daemon-$(VERSION).tar.gz:
	cd daemon/dbod_daemon && \
	make clean && perl Makefile.PL && make dist

rpm-daemon: daemon/dbod_daemon/dbod_daemon-$(VERSION).tar.gz 
	cp daemon/dbod_daemon/dbod_daemon-$(VERSION).tar.gz $(SOURCESPATH)
	export DAEMON_VERSION=$(VERSION) && $(RPMBUILD) $(RPMFLAGS) packages/dbod-daemon.spec
	cp $(RPMPATH)/DBOD-daemon-$(VERSION)-$(RELEASE).$(ARCH).rpm $(PKGSPATH)

rpm-mysql:
	cp $(DBODUTILS)/mysql/mysql-$(VERSION).tar.gz $(SOURCESPATH)
	export MYSQL_VERSION=$(VERSION) && $(RPMBUILD) $(RPMFLAGS) packages/dbod-mysql-server.spec
	cp $(RPMPATH)/DBOD-MySQL-server-$(VERSION)-$(RELEASE).$(ARCH).rpm $(PKGSPATH)

rpm-mariadb:
	cp $(DBODUTILS)/mariadb/mariadb-$(VERSION).tar.gz $(SOURCESPATH)
	export MARIADB_VERSION=$(VERSION) && $(RPMBUILD) $(RPMFLAGS) packages/dbod-mariadb-server.spec
	cp $(RPMPATH)/DBOD-MariaDB-server-$(VERSION)-$(RELEASE).$(ARCH).rpm $(PKGSPATH)

rpm-postgres:
	cp $(DBODUTILS)/pgsql/postgresql-$(VERSION).tar.gz $(SOURCESPATH)
	export PG_VERSION=$(VERSION) && $(RPMBUILD) $(RPMFLAGS) packages/dbod-postgresql-server.spec
	cp $(RPMPATH)/DBOD-PostgreSQL-server-$(VERSION)-$(RELEASE).$(ARCH).rpm $(PKGSPATH)

clean:

