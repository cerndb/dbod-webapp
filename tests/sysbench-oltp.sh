#!/bin/bash

# Performs a series of sysbench OLTP executions agains a MySQL/MariaDB database,
# incresing the number of threads/connections everytime
#
# The database must have been previously created


for th in 1, 2, 4, 8, 16;
    do sysbench --db-driver=mysql \
        --test=oltp \
        --mysql-table-engine=innodb \
        --oltp-table-size=100000000 \
        --mysql-host=dbod-testinstance2 \
        --mysql-port=5501 \
        --mysql-user=dod_mysql \
        --mysql-password= XXXXXXXX \
        --oltp-test-mode=complex \
        --max-requests=100000 \
        --max-time=600 \
        --num-threads=$th \
        --mysql-db=sbtest run >> LOG_FILE;
done

