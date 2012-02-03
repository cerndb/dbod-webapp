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
	INSERT INTO dbondemand.dod_command_params (username, db_name, command_name, type, creation_date, name, value)
		VALUES (username_param, db_name_param, 'BACKUP', type_param, now, 'INSTANCE_NAME', 'dod_' || db_name_param);
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
		VALUES (username, db_name, 'ENABLE_AUTOMATIC_BACKUPS', type, now, now, requester, admin_action, 'FINISHED_OK', 'Automatic backups enabled every ' || interval_hours || ' hours!');
	-- Initialise name and action
	name := db_name || '_BACKUP';
	action := 'BEGIN
			dbondemand.insert_backup_job (' || '''' || username || '''' || ', ' || '''' || db_name || '''' || ', ' 
			|| '''' || type || '''' || ', ' || '''' || requester || ''');END;';

	-- Query for any job with the same name running at the moment
	SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;

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
       
       
EXCEPTION
       WHEN NO_DATA_FOUND THEN
         NULL;
       WHEN OTHERS THEN
         RAISE;
         ROLLBACK;
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
                VALUES (username, db_name, 'DISABLE_AUTOMATIC_BACKUPS', type, now, now, requester, admin_action, 'FINISHED_OK', 'Automatic backups disabled!');

	-- Initialise name
	name := db_name || '_BACKUP';

	-- Query for any job with the same name running at the moment
	SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;

	-- Quote name to create object
	name := '"' || db_name || '_BACKUP"';

	-- If there is previous job, drop it
	IF job_count > 0
    	THEN
        	DBMS_SCHEDULER.DROP_JOB (
			job_name   =>  name,
			force      =>  TRUE);
    	END IF;

EXCEPTION
       WHEN NO_DATA_FOUND THEN
         NULL;
       WHEN OTHERS THEN
         RAISE;
         ROLLBACK;
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
	INSERT INTO dbondemand.dod_command_params (username, db_name, command_name, type, creation_date, name, value)
		VALUES (username_param, db_name_param, 'BACKUP_TO_TAPE', type_param, now, 'INSTANCE_NAME', 'dod_' || db_name_param);
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
		VALUES (username, db_name, 'ENABLE_BACKUPS_TO_TAPE', type, now, now, requester, admin_action, 'FINISHED_OK', 'Backups to tape enabled starting on ' || TO_CHAR(start_date_param,'DD/MM/YYYY HH24:MI:SS') || '!');
	-- Initialise name and action
	name := db_name || '_BACKUP_TO_TAPE';
	action := 'BEGIN
			dbondemand.insert_backup_to_tape_job (' || '''' || username || '''' || ', ' || '''' || db_name || '''' || ', ' 
			|| '''' || type || '''' || ', ' || '''' || requester || ''');END;';

	-- Query for any job with the same name running at the moment
	SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;

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
       
EXCEPTION
       WHEN NO_DATA_FOUND THEN
         NULL;
       WHEN OTHERS THEN
         RAISE;
         ROLLBACK;
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
                VALUES (username, db_name, 'DISABLE_BACKUPS_TO_TAPE', type, now, now, requester, admin_action, 'FINISHED_OK', 'Backups to tape disabled!');

	-- Initialise name
	name := db_name || '_BACKUP_TO_TAPE';

	-- Query for any job with the same name running at the moment
	SELECT COUNT(*)
		INTO job_count
		FROM user_scheduler_jobs
		WHERE job_name = name;

	-- Quote name to create object
	name := '"' || db_name || '_BACKUP_TO_TAPE"';

	-- If there is previous job, drop it
	IF job_count > 0
    	THEN
        	DBMS_SCHEDULER.DROP_JOB (
			job_name   =>  name,
			force      =>  TRUE);
    	END IF;

EXCEPTION
       WHEN NO_DATA_FOUND THEN
         NULL;
       WHEN OTHERS THEN
         RAISE;
         ROLLBACK;
END;
/

-- Approves an instance creation by changing the state of the isntance
CREATE OR REPLACE PROCEDURE approve_instance (id_param IN VARCHAR2, db_name_param IN VARCHAR2)
IS
    user VARCHAR2 (32);
BEGIN
        -- Select user from instances table
        SELECT username
            INTO user
            FROM dod_instances
            WHERE db_name = db_name_param;

        -- Insert FIM object row
        INSERT INTO dod_fim_objects (id, username, db_name)
		VALUES (id_param, user, db_name_param);

        -- Update instance status
        UPDATE dod_instances
            SET state = 'RUNNING'
            WHERE db_name = db_name_param;
        
END;
/

-- Destroys an instance by deleting the FIM object and setting the status of the instance to 0
CREATE OR REPLACE PROCEDURE destroy_instance (id_param IN VARCHAR2)
IS
    backup_count INTEGER;
    backup_name VARCHAR2 (512);
    tape_count INTEGER;
    tape_name VARCHAR2 (512);
    user VARCHAR2 (32);
    instance VARCHAR2(128);
BEGIN
    -- Get username and instance from FIM objects table
    SELECT username, db_name
            INTO user, instance
            FROM dod_fim_objects
            WHERE id = id_param;

    -- Initialise backup name
    backup_name := instance || '_BACKUP';

    -- Query for any scheduled backups with the same name running at the moment
    SELECT COUNT(*)
            INTO backup_count
            FROM user_scheduler_jobs
            WHERE job_name = backup_name;

    -- Quote backup name to create object
    backup_name := '"' || instance || '_BACKUP"';

    -- If there is a scheduled backup, drop it
    IF backup_count > 0
    THEN
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  backup_name,
                    force      =>  TRUE);
    END IF;

    -- Initialise tape name
    tape_name := instance || '_BACKUP_TO_TAPE';

    -- Query for any scheduled backups to tape with the same name running at the moment
    SELECT COUNT(*)
            INTO tape_count
            FROM user_scheduler_jobs
            WHERE job_name = tape_name;

    -- Quote tape name to create object
    tape_name := '"' || instance || '_BACKUP_TO_TAPE"';

    -- If there is a scheduled backup to tape, drop it
    IF tape_count > 0
    THEN
            DBMS_SCHEDULER.DROP_JOB (
                    job_name   =>  tape_name,
                    force      =>  TRUE);
    END IF;

    -- Update instance status
    UPDATE dod_instances
        SET status = '0'
        WHERE username = user AND db_name = instance;

    -- Delete the row in FIM objects table
    DELETE FROM dod_fim_objects
        WHERE id = id_param;

EXCEPTION
       WHEN NO_DATA_FOUND THEN
         NULL;
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
BEGIN
    -- Update instance username
    UPDATE dod_instances
        SET username = new_user
        WHERE username = old_user AND db_name = instance;

    -- Drop and create new automatic backups in case there were any
    -- Initialise name
    backup_name := instance || '_BACKUP';

    -- Query for any schedule backups with the same name running at the moment
    SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO backup_count, backup_action, backup_start_date, backup_interval
            FROM user_scheduler_jobs
            WHERE job_name = backup_name
            GROUP BY job_action, start_date, repeat_interval;
            
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
    SELECT COUNT(*), job_action, start_date, repeat_interval
            INTO tape_count, tape_action, tape_start_date, tape_interval
            FROM user_scheduler_jobs
            WHERE job_name = tape_name
            GROUP BY job_action, start_date, repeat_interval;
            
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
                    comments             => 'Scheduled backup job for DB On Demand');
    END IF;
EXCEPTION
       WHEN NO_DATA_FOUND THEN
         NULL;
       WHEN OTHERS THEN
         RAISE;
         ROLLBACK;
END;
/

-- Checks for changes in ownership, comparing FIM table to own table (to be used with dbms_scheduler)
CREATE OR REPLACE PROCEDURE check_ownership ()
IS
    CURSOR instances IS
        SELECT id
        FROM dod_fim_objects;
    user VARCHAR2 (32);
BEGIN
    FOR instance IN instances
    LOOP
        SELECT owner INTO user
            FROM fim_objects
            WHERE id = instance.id;
        IF user <> instance.username THEN
            change_owner (instance.db_name, instance.username, user);
        END IF;
    END LOOP;
END;
/