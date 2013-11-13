-- Add check_ownership, monitor_jobs, clean_jobs and check_expired to dbms_scheduler
BEGIN
    DBMS_SCHEDULER.CREATE_JOB (
        job_name             => 'DBOD_OWNERSHIP_CHECK',
        job_type             => 'PLSQL_BLOCK',
        job_action           => 'BEGIN dbondemand.check_ownership; END;',
        repeat_interval      => 'FREQ=MINUTELY;INTERVAL=10',
        enabled              =>  TRUE,
        comments             => 'Scheduled job to check for ownership changes on instances');

    DBMS_SCHEDULER.CREATE_JOB (
        job_name             => 'DBOD_MONITOR_JOBS',
        job_type             => 'PLSQL_BLOCK',
        job_action           => 'BEGIN dbondemand.monitor_jobs; END;',
        repeat_interval      => 'FREQ=MINUTELY;INTERVAL=5',
        enabled              =>  TRUE,
        comments             => 'Scheduled job to check for timed out jobs');

    DBMS_SCHEDULER.CREATE_JOB (
        job_name             => 'DBOD_CLEAN_JOBS',
        job_type             => 'PLSQL_BLOCK',
        job_action           => 'BEGIN dbondemand.clean_jobs; END;',
        repeat_interval      => 'FREQ=DAILY;',
        enabled              =>  TRUE,
        comments             => 'Scheduled job to clean jobs table');

    DBMS_SCHEDULER.CREATE_JOB (
        job_name             => 'DBOD_BACKUP_WARNING',
        job_type             => 'PLSQL_BLOCK',
        job_action           => 'BEGIN dbondemand.backup_warning; END;',
        repeat_interval      => 'FREQ=MONTHLY; BYMONTHDAY=1;',
        enabled              =>  TRUE,
        comments             => 'Warns users when automatic backups are not enabled');

    DBMS_SCHEDULER.CREATE_JOB (
        job_name             => 'DBOD_CHECK_EXPIRED',
        job_type             => 'PLSQL_BLOCK',
        job_action           => 'BEGIN dbondemand.check_expired; END;',
        repeat_interval      => 'FREQ=DAILY;',
        enabled              =>  TRUE,
        comments             => 'Checks for expired instances');
END;
/