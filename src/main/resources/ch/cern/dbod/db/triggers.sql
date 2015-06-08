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

-- Trigger to update username on dod_jobs and dod_instance_changes on cascade when updating dod_instances.
CREATE OR REPLACE TRIGGER dod_instances_update_username
AFTER UPDATE OF username, db_name ON dod_instances
FOR EACH ROW
BEGIN
    UPDATE dod_jobs
        SET username = :NEW.username, db_name = :NEW.db_name
        WHERE username = :OLD.username AND db_name = :OLD.db_name;
    UPDATE dod_instance_changes
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
    max_attachment_size Number:= 32767;
    attachment VARCHAR2(32767);
    attachment_size Number ;
BEGIN
    IF :NEW.state = 'FINISHED_FAIL' OR :NEW.state = 'FINISHED_WARNING'
    THEN
        message := '<html>
                        <body>
                            The execution of the following job has failed or finished with errors:
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
        attachment_size := LENGTH(:NEW.log); 
        IF attachment_size > max_attachment_size
        THEN attachment := '[...] ' || DBMS_LOB.SUBSTR(:NEW.log,max_attachment_size-6,attachment_size-max_attachment_size+6+1);
        ELSE attachment := CAST(:NEW.log AS VARCHAR2);
        END IF;
        UTL_MAIL.send_ATTACH_VARCHAR2(sender => 'dbondemand-admin@cern.ch',
            recipients => 'dbondemand-admin@cern.ch',
            subject => 'DBOD: CRITICAL: Failed job on "' || :NEW.db_name || '"',
            message => message,
            mime_type => 'text/html',
            attachment => attachment);

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
   os_user VARCHAR2 (32);
   hostname VARCHAR2 (64);
   module VARCHAR2 (64);
   ip VARCHAR2 (32);
   sid NUMBER;
   db_name VARCHAR2 (8);
BEGIN
  FOR depth IN 1 .. ORA_SERVER_ERROR_DEPTH LOOP
    error_msg := error_msg || ORA_SERVER_ERROR_MSG(depth);
  END LOOP;

  FOR i IN 1 .. ORA_SQL_TXT(sql_text) LOOP
     stmt := stmt || sql_text(i);
  END LOOP;

  SELECT SYS_CONTEXT('USERENV','OS_USER') INTO os_user from DUAL;
  SELECT SYS_CONTEXT('USERENV','HOST') INTO hostname from DUAL;
  SELECT SYS_CONTEXT('USERENV','IP_ADDRESS') INTO ip from DUAL;
  SELECT SYS_CONTEXT('USERENV','SID') INTO sid from DUAL;
  SELECT SYS_CONTEXT('USERENV','DB_NAME') INTO db_name from DUAL;
  SELECT SYS_CONTEXT('USERENV','MODULE') INTO module from DUAL;

  message := '<html>
                    <body>
                        <p>
                        <b>ERROR</b>:
                        </p>
                        <p>
                        '|| error_msg ||'
                        </p>
                        <p>
                        <b>STATEMENT</b>:
                        </p>
                        <p>
                        '|| stmt ||'
                        </p>
                        <p>
                        <b>USER</b>: ' || ora_login_user || '
                        </p>
                        <p>
                        <b>OS USER</b>: ' || os_user || '
                        </p>
                        <p>
                        <b>HOST</b>: ' || hostname || '
                        </p>
                        <p>
                        <b>IP</b>: ' || ip || '
                        </p>
                        <p>
                        <b>SID</b>: ' || sid || '
                        </p>
                    </body>
                </html>';
    IF module <> 'SQL Developer' THEN
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => 'dbondemand-admin@cern.ch',
            subject => 'DBOD: CRITICAL: Error in management database ' || db_name,
            message => message,
            mime_type => 'text/html');
    END IF;
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
                            <p>
                                Instance <b>' || :NEW.db_name || '</b> has been removed from FIM, or has expired, and has been marked for deletion.
                            </p>
                            <p>
                                The database will still be running until manually stopped. Please take the necessary actions to free the allocated resources
                                as documented in the corresponding <a href="https://twiki.cern.ch/twiki/bin/viewauth/DB/Private/DBOnDemandDeletion">TWiki article</a>.
                            </p>
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => 'dbondemand-admin@cern.ch',
            subject => 'DBOD: INFO: "' || :NEW.db_name || '" marked for deletion',
            message => message,
            mime_type => 'text/html');
    END IF;
END;
/

-- Trigger to send an email when an upgrade is performed.
CREATE OR REPLACE TRIGGER dod_upgrade_performed
AFTER UPDATE OF state ON dod_jobs
FOR EACH ROW
DECLARE
    message VARCHAR2 (1024);
BEGIN
    IF :NEW.COMMAND_NAME = 'UPGRADE' AND :NEW.state = 'FINISHED_OK'
    THEN
        message := '<html>
                        <body>
                            Instance <b>' || :NEW.db_name || '</b> has been successfully upgraded to the latest version!
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => 'dbondemand-admin@cern.ch',
            subject => 'DBOD: INFO: "' || :NEW.db_name || '" has been upgraded',
            message => message,
            mime_type => 'text/html');
    END IF;
END;
/
