#!/bin/bash

# Performs a series of sysbench OLTP executions agains a MySQL/MariaDB database,
# incresing the number of threads/connections everytime, while using SSL connections
#
# The database must have been previously created


for th in 1, 2, 4, 8, 16;
    do sysbench --db-driver=mysql \
        --test=oltp \
        --mysql-table-engine=innodb \
        --oltp-table-size=100000000 \
        --mysql-host=dbod-testinstance \
        --mysql-port=5500 \
        --mysql-user=sysbench \
        --mysql-password= XXXXXXXXX \
        --oltp-test-mode=complex \
        --max-requests=100000 \
        --max-time=600 \
        --num-threads=$th \
        --mysql-ssl \
        --mysql-db=sbtest run >> LOG_FILE;
done
