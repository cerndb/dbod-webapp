-- Inserts a backup job in the database
CREATE OR REPLACE PROCEDURE insert_backup_job (username_param IN VARCHAR2, db_name_param IN VARCHAR2,
						type_param IN VARCHAR2, requester_param IN VARCHAR2, admin_action_param IN INTEGER)
IS
	now DATE;
BEGIN
	SELECT sysdate
		INTO now
		FROM dual;
	INSERT INTO dbondemand.dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state)
		VALUES (username_param, db_name_param, 'BACKUP', type_param, now, requester_param, admin_action_param, 'PENDING');
	INSERT INTO dbondemand.dod_command_params (username, db_name, command_name, type, creation_date, name, value)
		VALUES (username_param, db_name_param, 'BACKUP', type_param, now, 'INSTANCE_NAME', 'DOD_' || db_name_param);
END;
/

-- Creates a new scheduled job in the database
CREATE OR REPLACE PROCEDURE create_scheduled_backup (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER,
							start_date_param IN DATE, interval_hours IN INTEGER)
IS
	name VARCHAR2(512); -- job name
	action VARCHAR(1024); -- action to be taken, in this case is a call to insert_backup_job
	job_count INTEGER; -- number of jobs with the same name running (1 or 0)
BEGIN
	-- Initialise name and action
	name := db_name || '_BACKUP';
	action := 'BEGIN
			dbondemand.insert_backup_job (' || '''' || username || '''' || ', ' || '''' || db_name || '''' || ', ' 
			|| '''' || type || '''' || ', ' || '''' || requester || '''' || ', ' || admin_action || ');
			END;';

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
END;
/

-- Deletes a scheduled job in the database
CREATE OR REPLACE PROCEDURE delete_scheduled_backup (db_name IN VARCHAR2)
IS
	name VARCHAR2(512); -- job name
	job_count INTEGER; -- number of jobs with the same name running (1 or 0)
BEGIN
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
END;
/
