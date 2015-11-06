-- Inserts a backup job in the database
CREATE OR REPLACE PROCEDURE insert_backup_job (username_param IN VARCHAR2, db_name_param IN VARCHAR2,
						type_param IN VARCHAR2, requester_param IN VARCHAR2)
IS
	now DATE;
BEGIN
	SELECT sysdate
		INTO now
		FROM dual;
	INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state)
		VALUES (username_param, db_name_param, 'BACKUP', type_param, now, requester_param, 2, 'PENDING');
        UPDATE dbondemand.dod_instances
                SET state = 'JOB_PENDING'
                WHERE username = username_param AND db_name = db_name_param;
END;
/

-- Creates a new scheduled job in the database
CREATE OR REPLACE PROCEDURE create_scheduled_backup (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER,
							start_date_param IN DATE, interval_hours IN INTEGER)
IS
	name VARCHAR2(512); -- job name
	action VARCHAR(1024); -- action to be taken, in this case is a call to insert_backup_job
	job_count INTEGER; -- number of jobs with the same name running (1 or 0)
        now DATE;
BEGIN
       -- Insert job as finished in the jobs table
       SELECT sysdate
		INTO now
		FROM dual;
       INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, completion_date, requester, admin_action, state, log)
		VALUES (username, db_name, 'ENABLE_AUTOMATIC_BACKUPS', 'ALL', now, now, requester, admin_action, 'FINISHED_OK', 'Automatic backups enabled every ' || interval_hours || ' hours! Starting on ' || TO_CHAR(start_date_param,'DD/MM/YYYY HH24:MI:SS'));
	-- Initialise name and action
	name := db_name || '_BACKUP';
	action := 'BEGIN
			dbondemand.insert_backup_job (' || '''' || username || '''' || ', ' || '''' || db_name || '''' || ', ' 
			|| '''' || type || '''' || ', ' || '''' || requester || ''');END;';

	-- Query for any job with the same name running at the moment
        BEGIN
            SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                job_count := 0;
            WHEN OTHERS THEN
                RAISE;
        END;

	-- Quote name to create object
	name := '"' || db_name || '_BACKUP"';

	-- If there is previous job, drop it
	IF job_count > 0
    	THEN
        	DBMS_SCHEDULER.DROP_JOB (
			job_name   =>  name,
			force      =>  TRUE);
    	END IF;

	-- Creates the scheduled job
	DBMS_SCHEDULER.CREATE_JOB (
		job_name             => name,
	  	job_type             => 'PLSQL_BLOCK',
	   	job_action           => action,
	  	start_date           => start_date_param,
	   	repeat_interval      => 'FREQ=HOURLY;INTERVAL=' || interval_hours,
	   	enabled              =>  TRUE,
	   	comments             => 'Scheduled backup job for DB On Demand');
END;
/

-- Deletes a scheduled job in the database
CREATE OR REPLACE PROCEDURE delete_scheduled_backup (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER)
IS
	name VARCHAR2(512); -- job name
	job_count INTEGER; -- number of jobs with the same name running (1 or 0)
        now DATE;
BEGIN
        -- Insert a row in the jobs table
        SELECT sysdate
                INTO now
                FROM dual;
        INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, completion_date, requester, admin_action, state, log)
                VALUES (username, db_name, 'DISABLE_AUTOMATIC_BACKUPS', 'ALL', now, now, requester, admin_action, 'FINISHED_OK', 'Automatic backups disabled');

	-- Initialise name
	name := db_name || '_BACKUP';

	-- Query for any job with the same name running at the moment
        BEGIN
            SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                job_count := 0;
            WHEN OTHERS THEN
                RAISE;
        END;

	-- Quote name to create object
	name := '"' || db_name || '_BACKUP"';

	-- If there is previous job, drop it
	IF job_count > 0
    	THEN
        	DBMS_SCHEDULER.DROP_JOB (
			job_name   =>  name,
			force      =>  TRUE);
    	END IF;
END;
/

-- Inserts a backup to tape job in the database
CREATE OR REPLACE PROCEDURE insert_backup_to_tape_job (username_param IN VARCHAR2, db_name_param IN VARCHAR2,
                                                        type_param IN VARCHAR2, requester_param IN VARCHAR2)
IS
	now DATE;
BEGIN
	SELECT sysdate
		INTO now
		FROM dual;
	INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state)
		VALUES (username_param, db_name_param, 'BACKUP_TO_TAPE', type_param, now, requester_param, 2, 'PENDING');
        UPDATE dbondemand.dod_instances
                SET state = 'JOB_PENDING'
                WHERE username = username_param AND db_name = db_name_param;
END;
/

CREATE OR REPLACE PROCEDURE insert_backup_logs_to_tape_job (username_param IN VARCHAR2, db_name_param IN VARCHAR2,
                                                        type_param IN VARCHAR2, requester_param IN VARCHAR2)
IS
	now DATE;
BEGIN
	SELECT sysdate
		INTO now
		FROM dual;
	INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state)
		VALUES (username_param, db_name_param, 'BACKUP_LOGS_TO_TAPE', type_param, now, requester_param, 2, 'PENDING');
        UPDATE dbondemand.dod_instances
                SET state = 'JOB_PENDING'
                WHERE username = username_param AND db_name = db_name_param;
END;
/

-- Creates a new scheduled job in the database
CREATE OR REPLACE PROCEDURE create_backup_to_tape (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER,
							start_date_param IN DATE)
IS
	name VARCHAR2(512); -- job name
	action VARCHAR(1024); -- action to be taken, in this case is a call to insert_backup_to_tape_job
	job_count INTEGER; -- number of jobs with the same name running (1 or 0)
        now DATE;
BEGIN
       -- Insert job as finished in the jobs table
       SELECT sysdate
		INTO now
		FROM dual;
       INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, completion_date, requester, admin_action, state, log)
		VALUES (username, db_name, 'ENABLE_BACKUPS_TO_TAPE', 'ALL', now, now, requester, admin_action, 'FINISHED_OK', 'Backups to tape enabled starting on ' || TO_CHAR(start_date_param,'DD/MM/YYYY HH24:MI:SS'));

	-- Initialise name and action
	name := db_name || '_BACKUP_TO_TAPE';
	action := 'BEGIN
			dbondemand.insert_backup_to_tape_job (' || '''' || username || '''' || ', ' || '''' || db_name || '''' || ', ' 
			|| '''' || type || '''' || ', ' || '''' || requester || ''');END;';

	-- Query for any job with the same name running at the moment
        BEGIN
            SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                job_count := 0;
            WHEN OTHERS THEN
                RAISE;
        END;

	-- Quote name to create object
	name := '"' || db_name || '_BACKUP_TO_TAPE"';

	-- If there is previous job, drop it
	IF job_count > 0
    	THEN
        	DBMS_SCHEDULER.DROP_JOB (
			job_name   =>  name,
			force      =>  TRUE);
    	END IF;

	-- Creates the scheduled job
	DBMS_SCHEDULER.CREATE_JOB (
		job_name             => name,
	  	job_type             => 'PLSQL_BLOCK',
	   	job_action           => action,
	  	start_date           => start_date_param,
	   	repeat_interval      => 'FREQ=WEEKLY;INTERVAL=1',
	   	enabled              =>  TRUE,
	   	comments             => 'Scheduled backup to tape job for DB On Demand');

        -- Initialise name and action for logs
	name := db_name || '_BACKUP_LOGS_TO_TAPE';
	action := 'BEGIN
			dbondemand.insert_backup_logs_to_tape_job (' || '''' || username || '''' || ', ' || '''' || db_name || '''' || ', ' 
			|| '''' || type || '''' || ', ' || '''' || requester || ''');END;';

	-- Query for any job with the same name running at the moment
        job_count := 0;
        BEGIN
            SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                job_count := 0;
            WHEN OTHERS THEN
                RAISE;
        END;

	-- Quote name to create object
	name := '"' || db_name || '_BACKUP_LOGS_TO_TAPE"';

	-- If there is previous job, drop it
	IF job_count > 0
    	THEN
        	DBMS_SCHEDULER.DROP_JOB (
			job_name   =>  name,
			force      =>  TRUE);
    	END IF;

	-- Creates the scheduled job (every 24 hours 12 hours after the start date)
	DBMS_SCHEDULER.CREATE_JOB (
		job_name             => name,
	  	job_type             => 'PLSQL_BLOCK',
	   	job_action           => action,
	  	start_date           => start_date_param + 12/24,
	   	repeat_interval      => 'FREQ=DAILY;INTERVAL=1',
	   	enabled              =>  TRUE,
	   	comments             => 'Scheduled backup logs to tape job for DB On Demand');
END;
/

-- Deletes a scheduled job in the database
CREATE OR REPLACE PROCEDURE delete_backup_to_tape (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER)
IS
	name VARCHAR2(512); -- job name
	job_count INTEGER; -- number of jobs with the same name running (1 or 0)
        now DATE;
BEGIN
        -- Insert a row in the jobs table
        SELECT sysdate
                INTO now
                FROM dual;
        INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, completion_date, requester, admin_action, state, log)
                VALUES (username, db_name, 'DISABLE_BACKUPS_TO_TAPE', 'ALL', now, now, requester, admin_action, 'FINISHED_OK', 'Backups to tape disabled');

	-- Initialise name
	name := db_name || '_BACKUP_TO_TAPE';

	-- Query for any job with the same name running at the moment
        BEGIN
            SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                job_count := 0;
            WHEN OTHERS THEN
                RAISE;
        END;

	-- Quote name to create object
	name := '"' || db_name || '_BACKUP_TO_TAPE"';

	-- If there is previous job, drop it
	IF job_count > 0
    	THEN
        	DBMS_SCHEDULER.DROP_JOB (
			job_name   =>  name,
			force      =>  TRUE);
    	END IF;

        -- Initialise name for logs backup
	name := db_name || '_BACKUP_LOGS_TO_TAPE';

	-- Query for any job with the same name running at the moment
        job_count := 0;
        BEGIN
            SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;
        EXCEPTION
            WHEN NO_DATA_FOUND THEN
                job_count := 0;
            WHEN OTHERS THEN
                RAISE;
        END;

	-- Quote name to create object
	name := '"' || db_name || '_BACKUP_LOGS_TO_TAPE"';

	-- If there is previous job, drop it
	IF job_count > 0
    	THEN
        	DBMS_SCHEDULER.DROP_JOB (
			job_name   =>  name,
			force      =>  TRUE);
    	END IF;
END;
/

-- Approves an instance creation by changing the state of the isntance
CREATE OR REPLACE PROCEDURE approve_instance (id_param IN VARCHAR2, db_name_param IN VARCHAR2, result OUT INTEGER)
IS
    db_type VARCHAR2 (32);
    username VARCHAR2 (32);
BEGIN
    result := 1;

    -- If instance is not in database do nothing and return error
    BEGIN
        SELECT username, db_type
            INTO username, db_type
            FROM dod_instances
            WHERE db_name = db_name_param;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            username := NULL;
        WHEN OTHERS THEN
            RAISE;
    END;

    IF username IS NOT NULL
    THEN
        -- Insert FIM object row
        INSERT INTO fim_ora_ma.dod_fim_objects (id, db_name)
                VALUES (id_param, db_name_param);

        -- Update instance status
        UPDATE dod_instances
            SET state = 'RUNNING'
            WHERE db_name = db_name_param;

        -- If Oracle instance, add scheduled cleanup job
        IF db_type = 'ORACLE' OR db_type = 'PG'
        THEN
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => '"' || db_name_param || '_CLEANUP"',
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => 'BEGIN dbondemand.insert_cleanup_job(''' || username || ''',''' 
                                            || db_name_param || ''',''' || db_type || ''',''dbod''); END;',
                    repeat_interval      => 'FREQ=DAILY;',
                    enabled              =>  TRUE,
                    comments             => 'Scheduled cleanup job for DB On Demand');
        END IF;

        -- Return 0 for success
        result := 0;
    END IF;
END;
/

-- Destroys an instance by deleting the FIM object and setting the status of the instance to 0
CREATE OR REPLACE PROCEDURE destroy_instance (id_param IN VARCHAR2, result OUT INTEGER)
IS
    backup_count INTEGER;
    backup_name VARCHAR2 (512);
    tape_count INTEGER;
    tape_name VARCHAR2 (512);
    tape_logs_count INTEGER;
    tape_logs_name VARCHAR2 (512);
    cleanup_count INTEGER;
    cleanup_name VARCHAR2 (512);
    instance VARCHAR2(128);
BEGIN
    result := 1;
    -- Get instance from FIM objects table
    SELECT db_name
            INTO instance
            FROM fim_ora_ma.dod_fim_objects
            WHERE id = id_param;

    -- Initialise backup name
    backup_name := instance || '_BACKUP';

    -- Query for any scheduled backups with the same name running at the moment
    BEGIN
        SELECT COUNT(*)
            INTO backup_count
            FROM user_scheduler_jobs
            WHERE job_name = backup_name;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            backup_count := 0;
        WHEN OTHERS THEN
            RAISE;
    END;

    -- If there is a scheduled backup, drop it
    IF backup_count > 0
    THEN
            -- Quote backup name to create object
            backup_name := '"' || instance || '_BACKUP"';

            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  backup_name,
                    force      =>  TRUE);
    END IF;

    -- Initialise tape name
    tape_name := instance || '_BACKUP_TO_TAPE';

    -- Query for any scheduled backups to tape with the same name running at the moment
    BEGIN
        SELECT COUNT(*)
            INTO tape_count
            FROM user_scheduler_jobs
            WHERE job_name = tape_name;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            tape_count := 0;
        WHEN OTHERS THEN
            RAISE;
    END;

    -- If there is a scheduled backup to tape, drop it
    IF tape_count > 0
    THEN
            -- Quote tape name to create object
            tape_name := '"' || instance || '_BACKUP_TO_TAPE"';

            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  tape_name,
                    force      =>  TRUE);
    END IF;

     -- Initialise logs to tape name
    tape_logs_name := instance || '_BACKUP_LOGS_TO_TAPE';

    -- Query for any scheduled log backups to tape with the same name running at the moment
    BEGIN
        SELECT COUNT(*)
            INTO tape_logs_count
            FROM user_scheduler_jobs
            WHERE job_name = tape_logs_name;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            tape_logs_count := 0;
        WHEN OTHERS THEN
            RAISE;
    END;

    -- If there is a scheduled log backup to tape, drop it
    IF tape_logs_count > 0
    THEN
            -- Quote tape name to create object
            tape_logs_name := '"' || instance || '_BACKUP_LOGS_TO_TAPE"';

            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  tape_logs_name,
                    force      =>  TRUE);
    END IF;

    -- Initialise cleanup name
    cleanup_name := instance || '_CLEANUP';

    -- Query for any scheduled cleanups with the same name running at the moment
    BEGIN
        SELECT COUNT(*)
            INTO cleanup_count
            FROM user_scheduler_jobs
            WHERE job_name = cleanup_name;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            cleanup_count := 0;
        WHEN OTHERS THEN
            RAISE;
    END;

    -- If there is a cleanup, drop it
    IF cleanup_count > 0
    THEN
            -- Quote tape name to create object
            cleanup_name := '"' || instance || '_CLEANUP"';

            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  cleanup_name,
                    force      =>  TRUE);
    END IF;

    -- Update instance status
    UPDATE dod_instances
        SET status = '0'
        WHERE db_name = instance;

    -- Delete the row in FIM objects table
    DELETE FROM fim_ora_ma.dod_fim_objects
        WHERE id = id_param;

    -- Return 0 for success
    result := 0;

EXCEPTION
       WHEN NO_DATA_FOUND THEN
         RETURN;
       WHEN OTHERS THEN
         RAISE;
         ROLLBACK;
END;
/

-- Updates the username to change the owner of an instance
CREATE OR REPLACE PROCEDURE change_owner (instance IN VARCHAR2, old_user IN VARCHAR2, new_user IN VARCHAR2)
IS
    backup_count INTEGER;
    backup_name VARCHAR2 (512);
    backup_action VARCHAR2 (1024);
    backup_interval VARCHAR2 (64);
    backup_start_date DATE;
    tape_count INTEGER;
    tape_name VARCHAR2 (512);
    tape_action VARCHAR2 (1024);
    tape_interval VARCHAR2 (64);
    tape_start_date DATE;
    tape_logs_count INTEGER;
    tape_logs_name VARCHAR2 (512);
    tape_logs_action VARCHAR2 (1024);
    tape_logs_interval VARCHAR2 (64);
    tape_logs_start_date DATE;
    cleanup_count INTEGER;
    cleanup_name VARCHAR2 (512);
    cleanup_action VARCHAR2 (1024);
    cleanup_interval VARCHAR2 (64);
    cleanup_start_date DATE;
BEGIN
    -- Update instance username
    UPDATE dod_instances
        SET username = new_user
        WHERE username = old_user AND db_name = instance;

    -- Drop and create new automatic backups in case there were any
    -- Initialise name
    backup_name := instance || '_BACKUP';

    -- Query for any schedule backups with the same name running at the moment
    BEGIN
        SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO backup_count, backup_action, backup_start_date, backup_interval
            FROM user_scheduler_jobs
            WHERE job_name = backup_name
            GROUP BY job_action, start_date, repeat_interval;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            backup_count := 0;
	WHEN OTHERS THEN
            RAISE;
    END;
            
    -- If there is a scheduled backup
    IF backup_count > 0
    THEN
            -- Quote name for object
            backup_name := '"' || instance || '_BACKUP"';

            -- Drop previous job
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  backup_name,
                    force      =>  TRUE);
            
            -- Create the scheduled job
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => backup_name,
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => REPLACE(backup_action, '''' || old_user || '''', '''' || new_user || ''''),
                    start_date           => backup_start_date,
                    repeat_interval      => backup_interval,
                    enabled              =>  TRUE,
                    comments             => 'Scheduled backup job for DB On Demand');
    END IF;

    -- Drop and create new backups to tape in case there were any
    -- Initialise name
    tape_name := instance || '_BACKUP_TO_TAPE';

    -- Query for any schedule backups with the same name running at the moment
    BEGIN
        SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO tape_count, tape_action, tape_start_date, tape_interval
            FROM user_scheduler_jobs
            WHERE job_name = tape_name
            GROUP BY job_action, start_date, repeat_interval;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            tape_count := 0;
	WHEN OTHERS THEN
            RAISE;
    END;
            
    -- If there is a scheduled backups to tape
    IF tape_count > 0
    THEN
            -- Quote name for object
            tape_name := '"' || instance || '_BACKUP_TO_TAPE"';

            -- Drop previous job
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  tape_name,
                    force      =>  TRUE);
            
            -- Create the scheduled job
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => tape_name,
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => REPLACE(tape_action, '''' || old_user || '''', '''' || new_user || ''''),
                    start_date           => tape_start_date,
                    repeat_interval      => tape_interval,
                    enabled              =>  TRUE,
                    comments             => 'Scheduled backup to tape job for DB On Demand');
    END IF;

    -- Drop and create new log backups to tape in case there were any
    -- Initialise name
    tape_logs_name := instance || '_BACKUP_LOGS_TO_TAPE';

    -- Query for any schedule log backups with the same name running at the moment
    BEGIN
        SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO tape_logs_count, tape_logs_action, tape_logs_start_date, tape_logs_interval
            FROM user_scheduler_jobs
            WHERE job_name = tape_logs_name
            GROUP BY job_action, start_date, repeat_interval;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            tape_logs_count := 0;
	WHEN OTHERS THEN
            RAISE;
    END;
            
    -- If there is a scheduled backups to tape
    IF tape_logs_count > 0
    THEN
            -- Quote name for object
            tape_logs_name := '"' || instance || '_BACKUP_LOGS_TO_TAPE"';

            -- Drop previous job
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  tape_logs_name,
                    force      =>  TRUE);
            
            -- Create the scheduled job
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => tape_logs_name,
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => REPLACE(tape_logs_action, '''' || old_user || '''', '''' || new_user || ''''),
                    start_date           => tape_logs_start_date,
                    repeat_interval      => tape_logs_interval,
                    enabled              =>  TRUE,
                    comments             => 'Scheduled backup logs to tape job for DB On Demand');
    END IF;

    -- Drop and create new cleanups in case there were any
    -- Initialise name
    cleanup_name := instance || '_CLEANUP';

    -- Query for any schedule cleanups with the same name running at the moment
    BEGIN
        SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO cleanup_count, cleanup_action, cleanup_start_date, cleanup_interval
            FROM user_scheduler_jobs
            WHERE job_name = cleanup_name
            GROUP BY job_action, start_date, repeat_interval;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            cleanup_count := 0;
	WHEN OTHERS THEN
            RAISE;
    END;

    -- If there is a scheduled cleanups
    IF cleanup_count > 0
    THEN
            -- Quote name for object
            cleanup_name := '"' || instance || '_CLEANUP"';

            -- Drop previous job
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  cleanup_name,
                    force      =>  TRUE);
            
            -- Create the scheduled job
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => cleanup_name,
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => REPLACE(cleanup_action, '''' || old_user || '''', '''' || new_user || ''''),
                    start_date           => cleanup_start_date,
                    repeat_interval      => cleanup_interval,
                    enabled              =>  TRUE,
                    comments             => 'Scheduled cleanup for DB On Demand');
    END IF;
END;
/

-- Checks for changes in ownership, comparing FIM table to own table (to be used with dbms_scheduler)
CREATE OR REPLACE PROCEDURE check_ownership
IS
    CURSOR instances IS
        SELECT owner_login, internal_id
        FROM fim_ora_ma.db_on_demand;
    user VARCHAR2 (32);
    name VARCHAR2 (128);
BEGIN
    FOR instance IN instances
    LOOP
        SELECT username, db_name INTO user, name
            FROM dbondemand.dod_instances
            WHERE db_name = ( SELECT db_name
                                    FROM fim_ora_ma.dod_fim_objects objects
                                    WHERE objects.id = instance.internal_id );
        IF user <> instance.owner_login THEN
            dbondemand.change_owner(name, user, instance.owner_login);
        END IF;
    END LOOP;
END;
/

-- Monitor jobs
CREATE OR REPLACE PROCEDURE monitor_jobs
IS
    -- Jobs that are pending for more than 30 seconds and no job is running on that instance
    CURSOR pending_jobs IS
        SELECT *
        FROM dbondemand.dod_jobs a
        WHERE a.state = 'PENDING'
            AND 30 <= (((SELECT sysdate FROM dual) - a.creation_date) * 86400)
            AND 0 = (SELECT COUNT(*)
                        FROM dbondemand.dod_jobs b
                        WHERE a.db_name = b.db_name
                            AND b.state = 'RUNNING')
            AND a.email_sent IS NULL
        FOR UPDATE OF a.email_sent;
    -- Jobs that are running for more than 6 hours
    CURSOR running_jobs IS
        SELECT *
        FROM dbondemand.dod_jobs
        WHERE state = 'RUNNING'
            AND 6 <= (((SELECT sysdate FROM dual) - creation_date) * 24)
            AND email_sent IS NULL
        FOR UPDATE OF email_sent;
    -- Message to send
    message VARCHAR2 (1024);
BEGIN
    FOR pending_job IN pending_jobs
    LOOP
        message := '<html>
                        <body>
                            The following job has been pending for more than 30 seconds:
                            <ul>
                                <li><b>Username</b>: ' || pending_job.username || '</li>
                                <li><b>DB Name</b>: ' || pending_job.db_name || '</li>
                                <li><b>Command name</b>: ' || pending_job.command_name || '</li>
                                <li><b>Type</b>: ' || pending_job.type || '</li>
                                <li><b>Creation date</b>: ' || TO_CHAR(pending_job.creation_date,'DD/MM/YYYY HH24:MI:SS') || '</li>
                                <li><b>Requester</b>: ' || pending_job.requester || '</li>
                                <li><b>Admin action</b>: ' || pending_job.admin_action || '</li>
                                <li><b>State</b>: ' || pending_job.state || '</li>
                            </ul>
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => 'dbondemand-admin@cern.ch',
            subject => 'DBOD: WARNING: Pending job on "' || pending_job.db_name || '"',
            message => message,
            mime_type => 'text/html');

        UPDATE dbondemand.dod_jobs
            SET email_sent = (SELECT sysdate FROM dual)
            WHERE CURRENT OF pending_jobs;
        
    END LOOP;

    FOR running_job IN running_jobs
    LOOP
        message := '<html>
                        <body>
                            The following job has been running for more than 6 hours:
                            <ul>
                                <li><b>Username</b>: ' || running_job.username || '</li>
                                <li><b>DB Name</b>: ' || running_job.db_name || '</li>
                                <li><b>Command name</b>: ' || running_job.command_name || '</li>
                                <li><b>Type</b>: ' || running_job.type || '</li>
                                <li><b>Creation date</b>: ' || TO_CHAR(running_job.creation_date,'DD/MM/YYYY HH24:MI:SS') || '</li>
                                <li><b>Requester</b>: ' || running_job.requester || '</li>
                                <li><b>Admin action</b>: ' || running_job.admin_action || '</li>
                                <li><b>State</b>: ' || running_job.state || '</li>
                            </ul>
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => 'dbondemand-admin@cern.ch',
            subject => 'DBOD: WARNING: Running job on "' || running_job.db_name || '"',
            message => message,
            mime_type => 'text/html');

        UPDATE dbondemand.dod_jobs
            SET email_sent = (SELECT sysdate FROM dual)
            WHERE CURRENT OF running_jobs;
        
    END LOOP;
END;
/

-- Backup warning: emails users when they do not have automatic backups enabled
CREATE OR REPLACE PROCEDURE backup_warning
IS
    -- Users and databases with no bacups activated
    CURSOR instances IS
        SELECT db_name, username
            FROM dod_instances
            WHERE 0 = (SELECT COUNT(*)
                        FROM user_scheduler_jobs
                        WHERE job_name = db_name || '_BACKUP')
                AND status = '1';
    message VARCHAR2 (2056);
BEGIN
    FOR instance IN instances
    LOOP
        message := '<html>
                        <body>
                            <p>
                            Dear DB On Demand user,
                            </p>
                            <p>
                            We have detected that your instance <b>' || instance.db_name || '</b> is not being automatically backed up. It is strongly recommended to enable automatic backups. In order to do so, open the backup management window from the overview or the instance view page, and check "Perform automatic backups every xx hours". Then click on "Apply changes".
                            </p>
                            <p>
                            For more information visit our <a href="https://j2eeps.cern.ch/DBOnDemand/help.zul">help page</a>.
                            </p>
                            <p>
                            Kind regards,
                            </p>
                            <p>
                            The DBOD team
                            </p>
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => instance.username || '@cern.ch',
            cc => 'dbondemand-admin@cern.ch',
            subject => 'Automatic backups not enabled on DB On Demand instance "' || instance.db_name || '"',
            message => message,
            mime_type => 'text/html');   
    END LOOP;
END;
/

-- Clean the jobs table, deleting jobs older than 90 days
CREATE OR REPLACE PROCEDURE clean_jobs
IS
    now DATE;
BEGIN
    -- Get system date
    SELECT sysdate
        INTO now
        FROM dual;

    -- Delete old jobs
    DELETE FROM dod_jobs
        WHERE now - creation_date > 90
            AND command_name <> 'UPLOAD_CONFIG';
END;
/

-- Checks for expired instances. Updating the status of the instance and sending an email to admins in case an instance is expired
CREATE OR REPLACE PROCEDURE check_expired
IS
    -- Users and databases of expired instances
    CURSOR expired_instances IS
        SELECT db_name, username
            FROM dod_instances
            WHERE expiry_date < sysdate
                AND status = '1'
            FOR UPDATE OF status;
    -- Users and databases and days of instances to expire in 30, 15 or 1 days
    CURSOR to_expire_instances IS
        SELECT db_name, username, TRUNC (expiry_date - SYSDATE) + 1 AS days
            FROM dod_instances
            WHERE (TRUNC (expiry_date - SYSDATE) + 1 = 30
                OR TRUNC (expiry_date - SYSDATE) + 1 = 15
                OR TRUNC (expiry_date - SYSDATE) + 1= 1)
                AND status = '1';
    message VARCHAR2 (2056);
BEGIN
    FOR instance IN expired_instances
    LOOP
        message := '<html>
                        <body>
                            <p>
                            Dear DB On Demand user,
                            </p>
                            <p>
                            Your instance <b>' || instance.db_name || '</b> has expired today. The resources used by this instance will be reallocated shortly. If this is an error, and this instance should not be expired, please contact DB On Demand admins immediately.
                            </p>
                            <p>
                            Kind regards,
                            </p>
                            <p>
                            The DBOD team
                            </p>
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => instance.username || '@cern.ch',
            cc => 'dbondemand-admin@cern.ch',
            subject => 'DB On Demand instance "' || instance.db_name || '" has expired',
            message => message,
            mime_type => 'text/html');
        
        UPDATE dod_instances
            SET status = '0' WHERE db_name = instance.db_name;
    END LOOP;

    FOR instance IN to_expire_instances
    LOOP
        message := '<html>
                        <body>
                            <p>
                            Dear DB On Demand user,
                            </p>
                            <p>
                            Your instance <b>' || instance.db_name || '</b> will expire in ' || instance.days || ' days. If you want to extend the expiry date for your instance, please go to the instance view in our website, where you can modify important information related to your instance.
                            </p>
                            <p>
                            Kind regards,
                            </p>
                            <p>
                            The DBOD team
                            </p>
                        </body>
                    </html>';
        
        UTL_MAIL.send(sender => 'dbondemand-admin@cern.ch',
            recipients => instance.username || '@cern.ch',
            cc => 'dbondemand-admin@cern.ch',
            subject => 'DB On Demand instance "' || instance.db_name || '" will expire in ' || instance.days || ' days',
            message => message,
            mime_type => 'text/html');
    END LOOP;
END;
/

-- Inserts a cleanup job in the database
CREATE OR REPLACE PROCEDURE insert_cleanup_job (username_param IN VARCHAR2, db_name_param IN VARCHAR2,
						type_param IN VARCHAR2, requester_param IN VARCHAR2)
IS
	now DATE;
BEGIN
	SELECT sysdate
		INTO now
		FROM dual;
	INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state)
		VALUES (username_param, db_name_param, 'CLEANUP', type_param, now, requester_param, 2, 'PENDING');
        UPDATE dbondemand.dod_instances
                SET state = 'JOB_PENDING'
                WHERE username = username_param AND db_name = db_name_param;
END;
/

-- Updates the DB name  of an instance
CREATE OR REPLACE PROCEDURE change_db_name (old_instance IN VARCHAR2, new_instance IN VARCHAR2)
IS
    backup_count INTEGER;
    backup_name VARCHAR2 (512);
    backup_action VARCHAR2 (1024);
    backup_interval VARCHAR2 (64);
    backup_start_date DATE;
    tape_count INTEGER;
    tape_name VARCHAR2 (512);
    tape_action VARCHAR2 (1024);
    tape_interval VARCHAR2 (64);
    tape_start_date DATE;
    tape_logs_count INTEGER;
    tape_logs_name VARCHAR2 (512);
    tape_logs_action VARCHAR2 (1024);
    tape_logs_interval VARCHAR2 (64);
    tape_logs_start_date DATE;
    cleanup_count INTEGER;
    cleanup_name VARCHAR2 (512);
    cleanup_action VARCHAR2 (1024);
    cleanup_interval VARCHAR2 (64);
    cleanup_start_date DATE;
BEGIN
    -- Update instance username
    UPDATE dod_instances
        SET db_name = new_instance
        WHERE db_name = old_instance;

    -- Drop and create new automatic backups in case there were any
    -- Initialise name
    backup_name := old_instance || '_BACKUP';

    -- Query for any schedule backups with the same name running at the moment
    BEGIN
        SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO backup_count, backup_action, backup_start_date, backup_interval
            FROM user_scheduler_jobs
            WHERE job_name = backup_name
            GROUP BY job_action, start_date, repeat_interval;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            backup_count := 0;
        WHEN OTHERS THEN
            RAISE;
    END;
            
    -- If there is a scheduled backup
    IF backup_count > 0
    THEN
            -- Quote name for object
            backup_name := '"' || old_instance || '_BACKUP"';

            -- Drop previous job
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  backup_name,
                    force      =>  TRUE);

            -- Quote name for object
            backup_name := '"' || new_instance || '_BACKUP"';
            
            -- Create the scheduled job
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => backup_name,
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => REPLACE(backup_action, '''' || old_instance || '''', '''' || new_instance || ''''),
                    start_date           => backup_start_date,
                    repeat_interval      => backup_interval,
                    enabled              =>  TRUE,
                    comments             => 'Scheduled backup job for DB On Demand');
    END IF;

    -- Drop and create new backups to tape in case there were any
    -- Initialise name
    tape_name := old_instance || '_BACKUP_TO_TAPE';

    -- Query for any schedule backups with the same name running at the moment
    BEGIN
        SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO tape_count, tape_action, tape_start_date, tape_interval
            FROM user_scheduler_jobs
            WHERE job_name = tape_name
            GROUP BY job_action, start_date, repeat_interval;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            tape_count := 0;
        WHEN OTHERS THEN
            RAISE;
    END;
            
    -- If there is a scheduled backups to tape
    IF tape_count > 0
    THEN
            -- Quote name for object
            tape_name := '"' || old_instance || '_BACKUP_TO_TAPE"';

            -- Drop previous job
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  tape_name,
                    force      =>  TRUE);

            -- Quote name for object
            tape_name := '"' || new_instance || '_BACKUP_TO_TAPE"';
            
            -- Create the scheduled job
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => tape_name,
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => REPLACE(tape_action, '''' || old_instance || '''', '''' || new_instance || ''''),
                    start_date           => tape_start_date,
                    repeat_interval      => tape_interval,
                    enabled              =>  TRUE,
                    comments             => 'Scheduled backup to tape job for DB On Demand');
    END IF;

    -- Drop and create new backups to tape in case there were any
    -- Initialise name
    tape_logs_name := old_instance || '_BACKUP_LOGS_TO_TAPE';

    -- Query for any schedule backups with the same name running at the moment
    BEGIN
        SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO tape_logs_count, tape_logs_action, tape_logs_start_date, tape_logs_interval
            FROM user_scheduler_jobs
            WHERE job_name = tape_logs_name
            GROUP BY job_action, start_date, repeat_interval;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            tape_logs_count := 0;
        WHEN OTHERS THEN
            RAISE;
    END;
            
    -- If there is a scheduled backups to tape
    IF tape_logs_count > 0
    THEN
            -- Quote name for object
            tape_logs_name := '"' || old_instance || '_BACKUP_LOGS_TO_TAPE"';

            -- Drop previous job
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  tape_logs_name,
                    force      =>  TRUE);

            -- Quote name for object
            tape_logs_name := '"' || new_instance || '_BACKUP_LOGS_TO_TAPE"';
            
            -- Create the scheduled job
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => tape_logs_name,
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => REPLACE(tape_logs_action, '''' || old_instance || '''', '''' || new_instance || ''''),
                    start_date           => tape_logs_start_date,
                    repeat_interval      => tape_logs_interval,
                    enabled              =>  TRUE,
                    comments             => 'Scheduled backup logs to tape job for DB On Demand');
    END IF;

    -- Drop and create new cleanups in case there were any
    -- Initialise name
    cleanup_name := old_instance || '_CLEANUP';

    -- Query for any schedule cleanups with the same name running at the moment
    BEGIN
        SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO cleanup_count, cleanup_action, cleanup_start_date, cleanup_interval
            FROM user_scheduler_jobs
            WHERE job_name = cleanup_name
            GROUP BY job_action, start_date, repeat_interval;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            cleanup_count := 0;
	WHEN OTHERS THEN
            RAISE;
    END;
            
    -- If there is a scheduled cleanups
    IF cleanup_count > 0
    THEN
            -- Quote name for object
            cleanup_name := '"' || old_instance || '_CLEANUP"';

            -- Drop previous job
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  cleanup_name,
                    force      =>  TRUE);

            cleanup_name := '"' || new_instance || '_CLEANUP"';
            
            -- Create the scheduled job
            DBMS_SCHEDULER.CREATE_JOB (
                    job_name             => cleanup_name,
                    job_type             => 'PLSQL_BLOCK',
                    job_action           => REPLACE(cleanup_action, '''' || old_instance || '''', '''' || new_instance || ''''),
                    start_date           => cleanup_start_date,
                    repeat_interval      => cleanup_interval,
                    enabled              =>  TRUE,
                    comments             => 'Scheduled cleanup for DB On Demand');
    END IF;
END;
/

-- Inserts the number of instances created so far this month
CREATE OR REPLACE PROCEDURE merge_stats_monthly_instances
IS
  p_agg_num_instances NUMBER;
  p_inst_last_month NUMBER;
  p_inst_this_month NUMBER;
  p_last_month_date DATE;
  p_this_month_date DATE;
BEGIN
  -- Get start date of current month
  p_this_month_date:=trunc(sysdate,'MM');

  -- Get start date of previous month
  p_last_month_date:=trunc(ADD_MONTHS(sysdate,-1),'MON');

  -- Query the number of instances created so far this month
  SELECT COUNT(*) INTO p_inst_this_month
  FROM dod_instances 
  WHERE creation_date >= p_this_month_date 
    and (expiry_date>=TRUNC(SYSDATE) OR expiry_date IS NULL);

  -- Query the number of instances aggregated until last month
  SELECT agg_num_instances INTO p_agg_num_instances
  FROM stats_monthly_instances
  WHERE monthly_date = p_last_month_date;

  -- Insert or update row with monthly instances
  MERGE INTO stats_monthly_instances 
  USING dual ON (monthly_date=p_this_month_date)
    WHEN MATCHED THEN 
      UPDATE SET num_instances=p_inst_this_month, agg_num_instances=p_agg_num_instances+p_inst_this_month
    WHEN NOT MATCHED THEN 
      INSERT (monthly_date, num_instances, agg_num_instances) VALUES (p_this_month_date, p_inst_this_month, p_agg_num_instances+p_inst_this_month);
END;
/

