-- Trigger to not allow jobs to be created when the instance is
-- awaiting approval or with a pending job. Unless there is an admin_action
CREATE OR REPLACE TRIGGER dod_jobs_insert
BEFORE INSERT ON dod_jobs
FOR EACH ROW
DECLARE
    pending INTEGER;
BEGIN
    SELECT COUNT(*) INTO pending
    FROM dod_instances
    WHERE username = :NEW.username
        AND db_name = :NEW.db_name
        AND (state = 'JOB_PENDING' OR state = 'AWAITING_APPROVAL');

    IF pending > 0 AND :NEW.admin_action = 0
    THEN
        raise_application_error(-20000,'INSTANCE PENDING JOB OR AWAITING APPROVAL');
    END IF;
END;
/

-- Trigger to update username on dod_jobs on cascade when updating dod_instances.
CREATE OR REPLACE TRIGGER dod_instances_update_username
AFTER UPDATE OF username ON dod_instances
FOR EACH ROW
BEGIN
    update dod_jobs
        SET username = :NEW.username
        WHERE username = :OLD.username;
END;
/

-- Trigger to update username on dod_command_params on cascade when updating dod_jobs.
CREATE OR REPLACE TRIGGER dod_jobs_update_username
AFTER UPDATE OF username ON dod_jobs
FOR EACH ROW
BEGIN
    update dod_command_params
        SET username = :NEW.username
        WHERE username = :OLD.username;
END;
/