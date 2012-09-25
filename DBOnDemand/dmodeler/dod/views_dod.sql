-- View to select command stats
CREATE VIEW COMMAND_STATS AS
    SELECT command_name, COUNT(*) AS COUNT, ROUND(AVG(completion_date - creation_date) * 24*60*60) AS mean_duration
        FROM dod_jobs GROUP BY command_name;

-- View to select job stats
CREATE VIEW JOB_STATS AS
    SELECT db_name, command_name, COUNT(*) as COUNT, ROUND(AVG(completion_date - creation_date) * 24*60*60) AS mean_duration
        FROM dod_jobs GROUP BY command_name, db_name;
