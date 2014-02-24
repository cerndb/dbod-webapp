rm -f /usr/lib64/perl5/site_perl/5.8.8/x86_64-linux-thread-multi/auto/dbod_daemon/dbod_daemon.conf
rm -f /usr/lib64/perl5/site_perl/5.8.8/x86_64-linux-thread-multi/auto/dbod_daemon/dbod_daemon_logger.conf
rm -f /usr/lib64/perl5/site_perl/5.8.8/x86_64-linux-thread-multi/auto/dbod_daemon/templates

for a in 0 1 6; do rm /etc/rc$a.d/K85dbod_daemon; done
for a in 2 3 4 5; do rm /etc/rc$a.d/S85dbod_daemon; done

rm -fr /var/run/dbod_daemon
rm -fr /var/log/dbod_daemon
