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
        AND (state = 'JOB_PENDING' OR state = 'AWAITING_APPROVAL' OR state = 'MAINTENANCE');

    IF pending > 0 AND :NEW.admin_action = 0
    THEN
        raise_application_error(-20000,'INSTANCE PENDING JOB, AWAITING APPROVAL OR UNDER MAINTENANCE');
    END IF;
END;
/

-- Trigger to update username on dod_jobs and dod_fim_objects on cascade when updating dod_instances.
CREATE OR REPLACE TRIGGER dod_instances_update_username
AFTER UPDATE OF username, db_name ON dod_instances
FOR EACH ROW
BEGIN
    UPDATE dod_jobs
        SET username = :NEW.username, db_name = :NEW.db_name
        WHERE username = :OLD.username AND db_name = :OLD.db_name; 
END;
/

-- Trigger to update username on dod_command_params on cascade when updating dod_jobs.
CREATE OR REPLACE TRIGGER dod_jobs_update_username
AFTER UPDATE OF username, db_name ON dod_jobs
FOR EACH ROW
BEGIN
    UPDATE dod_command_params
        SET username = :NEW.username, db_name = :NEW.db_name
        WHERE username = :OLD.username AND db_name = :OLD.db_name;
END;
/

-- Trigger to monitor failed jobs.
CREATE OR REPLACE TRIGGER dod_jobs_monitor_fail
BEFORE UPDATE OF state ON dod_jobs
FOR EACH ROW
DECLARE
    message VARCHAR2 (1024);
BEGIN
    IF :NEW.state = 'FINISHED_FAIL'
    THEN
        message := '<html>
                        <body>
                            The execution of the following job has failed:
                            <ul>
                                <li><b>Username</b>: ' || :NEW.username || '</li>
                                <li><b>DB Name</b>: ' || :NEW.db_name || '</li>
                                <li><b>Command name</b>: ' || :NEW.command_name || '</li>
                                <li><b>Type</b>: ' || :NEW.type || '</li>
                                <li><b>Creation date</b>: ' || TO_CHAR(:NEW.creation_date,'DD/MM/YYYY HH24:MI:SS') || '</li>
                                <li><b>Completion date</b>: ' || TO_CHAR(:NEW.completion_date,'DD/MM/YYYY HH24:MI:SS') || '</li>
                                <li><b>Requester</b>: ' || :NEW.requester || '</li>
                                <li><b>Admin action</b>: ' || :NEW.admin_action || '</li>
                                <li><b>State</b>: ' || :NEW.state || '</li>
                            </ul>
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => 'dbondemand-admin@cern.ch',
            subject => 'DBOD: FAILED job on ' || :NEW.db_name,
            message => message,
            mime_type => 'text/html');

        :NEW.email_sent := sysdate;
    END IF;
END;
/

-- Trigger to monitor any database error
CREATE OR REPLACE TRIGGER monitor_db_errors
AFTER SERVERERROR ON SCHEMA
DECLARE
   sql_text ORA_NAME_LIST_T;
   error_msg VARCHAR2(1024) := NULL;
   stmt VARCHAR2(1024) := NULL;
   message VARCHAR2 (3072);
BEGIN
  FOR depth IN 1 .. ORA_SERVER_ERROR_DEPTH LOOP
    error_msg := error_msg || ORA_SERVER_ERROR_MSG(depth);
  END LOOP;

  FOR i IN 1 .. ORA_SQL_TXT(sql_text) LOOP
     stmt := stmt || sql_text(i);
  END LOOP;

  message := '<html>
                    <body>
                        <p>
                        The management database has encountered the following error:
                        </p>
                        <p>
                        '|| error_msg ||'
                        </p>
                        <p>
                        Caused by the following statement:
                        </p>
                        <p>
                        '|| stmt ||'
                        </p>
                    </body>
                </html>';

    UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
        recipients => 'dbondemand-admin@cern.ch',
        subject => 'DBOD: ERROR IN MANAGEMENT DATABASE ',
        message => message,
        mime_type => 'text/html');
END;
/

-- Trigger to send an email when an instance is deleted.
CREATE OR REPLACE TRIGGER dod_instances_delete
AFTER UPDATE OF status ON dod_instances
FOR EACH ROW
DECLARE
    message VARCHAR2 (1024);
BEGIN
    IF :NEW.status = '0'
    THEN
        message := '<html>
                        <body>
                            Instance ' || :NEW.db_name || ' has been removed from FIM and marked for deletion.
                            Please take the necessary actions to free the allocated resources.
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => 'dbondemand-admin@cern.ch',
            subject => 'DBOD: ' || :NEW.db_name || ' marked for deletion',
            message => message,
            mime_type => 'text/html');
    END IF;
END;
/