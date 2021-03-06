/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.dao;

import ch.cern.dbod.db.entity.CommandParam;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Job;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.RestHelper;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.http.ParseException;

/**
 * DAO for Job entity.
 * @author Daniel Gomez Blanco
 * @author Jose Andres Cordero Benitez
 */
public class JobDAO {

    /**
     * Date formatter date and time
     */
    DateFormat dateTimeFormatter = new SimpleDateFormat(CommonConstants.DATE_TIME_FORMAT);
    
    /**
     * Obtains a new connection from the pool.
     * @return a connection to the database.
     * @throws NamingException if the context cannot be found.
     * @throws SQLException if the datasource cannot be created.
     */
    private Connection getConnection() throws NamingException, SQLException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup(CommonConstants.ENVIRONMENT_CONTEXT);
        DataSource dataSource = (DataSource) envContext.lookup(CommonConstants.DATA_SOURCE_DBOD);
        return dataSource.getConnection();
    }

    /**
     * Obtains the list of jobs for a specific instance.
     * @param instance DBOD instance to get the jobs from.
     * @return list of jobs for the specified instance.
     */
    public List<Job> selectByInstance(Instance instance) {
        ArrayList<Job> jobs = null;
        try {
            jobs = RestHelper.getObjectListFromRestApi("api/v1/instance/" + instance.getDbName() + "/job", null, Job.class, null, "response");
        } catch (Exception ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "Error getting jobs for instance: " + instance.getDbName(), ex);
        } finally {
            
        }
        return jobs;
    }

    /**
     * Selects a log for a specific job.
     * @param instance
     * @param job job to obtain the log from.
     * @return String containing the job log.
     */
    public String selectLogByJob(String instance, Job job) {
        String log = "";
        try {
            JsonObject json = RestHelper.getJsonObjectFromRestApi("api/v1/instance/" + instance + "/job/" + job.getId());
            log = json.getAsJsonObject("response").get("log").getAsString();
        } catch (ParseException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING LOG FOR INSTANCE: " + instance, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR COMMUNICATING WITH APIATO", ex);
        }
        catch (IllegalStateException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR PARSING RESPONSE", ex);
        }
        return log;
    }

    /**
     * Selects an specific log.
     * @param jobId
     * @return Selected job.
     */
    public Job selectJob(String instance, int jobId) {
        Logger.getLogger(JobDAO.class.getName()).log(Level.INFO, String.format("SELECTING JOB: %d", jobId));
        try {
            return RestHelper.getObjectFromRestApi("api/v1/instance/" + instance + "/job/" + jobId, Job.class, "response");
        } catch (Exception ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "Error getting job " + jobId + " for instance: " + instance, ex);
        } finally {
            
        }
        return null;
    }
    
    /**
     * Obtains the list of last executed jobs.
     * @return list of last executed jobs.
     */
    public List<Job> selectLastJobs(String username) {
        ArrayList<Job> jobs = new ArrayList<>();
        try {
            JsonObject authHeader = new JsonObject();
            authHeader.addProperty("owner", username);
            authHeader.addProperty("groups", "[]");
            authHeader.addProperty("admin", true);
            HashMap<String, String> params = new HashMap<>();
            params.put("size", "500");
            return RestHelper.getObjectListFromRestApi("api/v1/job", params, Job.class, authHeader.toString(), "response");
        } catch (Exception ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "Error getting last executed jobs", ex);
        } finally {
            
        }
        return jobs;
    }
    
    /**
     * Inserts a job in the database with a log. This method is used to insert
     * jobs only for logging purposes, like enabling or disabling backups. It is
     * a single insert, it does not update the instance or insert any parameters.
     * It simple inserts the job for logging purposes. That is why the job is always in state FINISHED.
     * @param job job to be inserted.
     * @param log log for the job.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int insert(Job job, String log) {
        Connection connection = null;
        PreparedStatement insertJobStatement = null;
        PreparedStatement insertJobLogStatement = null;
        int insertJobResult = 0;
        try {
            //Get connection
            connection = getConnection();
            connection.setAutoCommit(false);
            //Prepare query for the prepared statement (to avoid SQL injection)
            String insertQuery = "INSERT INTO job (instance_id, command_name, creation_date, requester, admin_action, state)"
                            + " VALUES (?, ?, ?, ?, ?, ?)";
            insertJobStatement = connection.prepareStatement(insertQuery);
            //Assign values to variables
            insertJobStatement.setInt(1, job.getInstance_id());
            insertJobStatement.setString(2, job.getCommandName());
            insertJobStatement.setTimestamp(3, new java.sql.Timestamp(job.getCreationDate().getTime()));
            insertJobStatement.setString(4, job.getRequester());
            insertJobStatement.setInt(5, job.getAdminAction());
            insertJobStatement.setString(6, job.getState());

            //Execute query
            insertJobResult = insertJobStatement.executeUpdate();
            
            if (insertJobResult != PreparedStatement.EXECUTE_FAILED) {
                try (ResultSet rs = insertJobStatement.getGeneratedKeys()) {
                    if (rs.next()) {
                        //Prepare query for the prepared statement (to avoid SQL injection)
                        String insertLogQuery = "INSERT INTO job_log (id, log) VALUES (?, ?)";
                        insertJobLogStatement = connection.prepareStatement(insertLogQuery);
                        
                        insertJobLogStatement.setInt(1, rs.getInt(1));
                        insertJobLogStatement.setString(2, log);
                        
                        //Execute query
                        insertJobResult = insertJobLogStatement.executeUpdate();
                    }
                }
            }
            
            //Commit queries
            connection.commit();
        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR INSTANCE " + job.getInstance_id(), ex);
        }
        finally {
            try {
                insertJobStatement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return insertJobResult;
    }

    /**
     * Inserts a job in the database, to be picked up by the daemon.
     * @param job job to be inserted.
     * @param params parameters to be inserted with the job.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int insert(Job job, List<CommandParam> params) {
        Connection connection = null;
        PreparedStatement insertJobStatement = null;
        PreparedStatement insertParamsStatement = null;
        PreparedStatement updateInstanceStatement = null;
        int insertJobResult;
        int insertParamsResult;
        int updateInstanceResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Set autocommit to false to execute multiple queries and rollback in case something goes wrong
            connection.setAutoCommit(false);
            //Prepare query for the prepared statement (to avoid SQL injection)
            String insertQuery = "INSERT INTO job (instance_id, command_name, creation_date, requester, admin_action, state)"
                            + " VALUES (?, ?, ?, ?, ?, ?)";
            insertJobStatement = connection.prepareStatement(insertQuery);
            //Assign values to variables
            insertJobStatement.setInt(1, job.getInstance_id());
            insertJobStatement.setString(2, job.getCommandName());
            insertJobStatement.setTimestamp(3, new java.sql.Timestamp(job.getCreationDate().getTime()));
            insertJobStatement.setString(4, job.getRequester());
            insertJobStatement.setInt(5, job.getAdminAction());
            insertJobStatement.setString(6, job.getState());

            //Execute query
            insertJobResult = insertJobStatement.executeUpdate();
            
            //If the insert operation was successful, insert params (if any) and update intance
            if (insertJobResult != PreparedStatement.EXECUTE_FAILED) {
                if (params != null && !params.isEmpty()) {
                    //Prepare query for the prepared statement (to avoid SQL injection)
                    String paramsQuery = "INSERT INTO command_param (job_id, name, value) VALUES (?, ?, ?)";
                    insertParamsStatement = connection.prepareStatement(paramsQuery);
                    //If there are parameters
                    for (int i=0; i<params.size(); i++) {
                        CommandParam commandParam = params.get(i);
                        //Assign values to variables
                        insertParamsStatement.setInt(1, job.getId());
                        insertParamsStatement.setString(2, commandParam.getName());
                        insertParamsStatement.setString(3, commandParam.getValue());
                        insertParamsStatement.addBatch();
                    }
                    int[] results = insertParamsStatement.executeBatch();
                    insertParamsResult = results.length;
                    for (int i=0; i<results.length; i++){
                        if (results[i] == PreparedStatement.EXECUTE_FAILED) {
                            insertParamsResult = PreparedStatement.EXECUTE_FAILED;
                            break;
                        }
                    }
                }
                else
                    insertParamsResult = 1;

                //Only update instance if the operation was succesful
                if (insertParamsResult != PreparedStatement.EXECUTE_FAILED) {
                    //Prepare query for the prepared statement (to avoid SQL injection)
                    String updateQuery = "UPDATE instance SET state = '" + CommonConstants.INSTANCE_STATE_JOB_PENDING + "' WHERE id = ?";
                    updateInstanceStatement = connection.prepareStatement(updateQuery);
                    //Assign values to variables
                    updateInstanceStatement.setInt(1, job.getInstance_id());
                    //Execute query
                    updateInstanceResult = updateInstanceStatement.executeUpdate();

                    if (updateInstanceResult <= 0) {
                        connection.rollback();
                        return 0;
                    }
                }
                else {
                    connection.rollback();
                    return 0;
                }
            }
            
            //Commit queries
            connection.commit();
        }
        catch (NamingException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR INSTANCE " + job.getInstance_id(), ex);
        }
        catch (SQLException ex) {
            try {
                //Rollback updates
                connection.rollback();
            }
            catch (SQLException ex1) {
                Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR INSTANCE " + job.getInstance_id(), ex1);
            }
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR INSTANCE " + job.getInstance_id(), ex);
        }

        finally {
            try {
                insertJobStatement.close();
            } catch (Exception e) {}
            try {
                updateInstanceStatement.close();
            } catch (Exception e) {}
            try {
                insertParamsStatement.close();
            } catch (Exception e) {}
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return updateInstanceResult;
    }

    /**
     * Creates a scheduled backup.
     * @param instance instance to enable backups on.
     * @param requester user requesting automatic backups.
     * @param startDate date to start backups
     * @param intervalHours hours between snapshots.
     * @return true if the operation was successful, false otherwise.
     */
    public boolean createScheduledBackup(Instance instance, String requester, java.util.Date startDate, int intervalHours, int admin) {
        Connection connection = null;
        CallableStatement createScheduleStatement = null;
        int createScheduleResult = 0;
        try {
            //Get connection
            connection = getConnection();
            
            //Create call create_scheduled_backup (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER,
            //                                      start_date_param IN DATE, interval_hours IN INTEGER)
            String createScheduleCall = "{ call create_scheduled_backup(?, ?, ?, ?, ?, ?, ?) }";
            createScheduleStatement = connection.prepareCall(createScheduleCall);
            //Set values
            createScheduleStatement.setString(1, instance.getOwner());
            createScheduleStatement.setString(2, instance.getDbName());
            createScheduleStatement.setString(3, instance.getDbType());
            createScheduleStatement.setString(4, requester);
            createScheduleStatement.setInt(5, admin);
            createScheduleStatement.setTimestamp(6, new java.sql.Timestamp(startDate.getTime()));
            createScheduleStatement.setInt(7, intervalHours);
            
            //Execute
            createScheduleResult = createScheduleStatement.executeUpdate();
        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR ENABLING BACKUPS FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex);
        }

        finally {
            try {
                createScheduleStatement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        if (createScheduleResult > 0) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.INFO, "ENABLE AUTOMATIC BACKUPS JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{requester, instance.getDbName()});
            return true;
        }
        else
            return false;
    }
    
    /**
     * Deletes a scheduled backup.
     * @param instance instance to delete the scheduled backup from.
     * @return true if the operation was successful, false otherwise.
     */
    public boolean deleteScheduledBackup(Instance instance, String username, int admin) {
        Connection connection = null;
        CallableStatement deleteScheduleStatement = null;
        int deleteScheduleResult = 0;
        try {
            //Get connection
            connection = getConnection();
            
            //Create call delete_scheduled_backup (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER)
            String deleteScheduleCall = "{ call delete_scheduled_backup(?, ?, ?, ?, ?) }";
            deleteScheduleStatement = connection.prepareCall(deleteScheduleCall);
            //Set values
            deleteScheduleStatement.setString(1, instance.getOwner());
            deleteScheduleStatement.setString(2, instance.getDbName());
            deleteScheduleStatement.setString(3, instance.getDbType());
            deleteScheduleStatement.setString(4, username);
            deleteScheduleStatement.setInt(5, admin);

            //Execute
            deleteScheduleResult = deleteScheduleStatement.executeUpdate();
            
            if (deleteScheduleResult != CallableStatement.EXECUTE_FAILED)
                 Logger.getLogger(JobDAO.class.getName()).log(Level.INFO, "DISABLE AUTOMATIC BACKUPS JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING SCHEDULED BACKUP FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex);
        }

        finally {
            try {
                deleteScheduleStatement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        if (deleteScheduleResult > 0)
            return true;
        else
            return false;
    }
    
    /**
     * Obtains the hours interval for the scheduled backups of a given instance.
     * @param instance DBOD instance to get the interval of.
     * @return interval, in hours, for the scheduled backups. 0 in case scheduled backups are not enabled.
     */
    public int getBackupInterval(Instance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        int interval = 0;
        try {
            //Get connection
            connection = getConnection();
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "SELECT repeat_interval"
                            + " FROM user_scheduler_jobs"
                            + " WHERE job_name = ?";
            statement = connection.prepareStatement(query);
            //Assign values to variables
            statement.setString(1, instance.getDbName() + "_BACKUP");
            //Execute query
            result = statement.executeQuery();

            //Parse the int to return
            if (result.next()) {
                String intervalStr = result.getString(1);
                interval = Integer.parseInt(intervalStr.substring(intervalStr.indexOf("INTERVAL=") + 9));
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING BACKUP INTERVAL FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex);
        } finally {
            try {
                result.close();
            } catch (Exception e) {}
            try {
                statement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return interval;
    }
    
    /**
     * Checks automatic backups start date.
     * @param instance DBOD instance to get the start date.
     * @return start date for backups for the given instance, null otherwise.
     */
    public java.util.Date getBackupStartDate(Instance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        java.util.Date date = null;
        try {
            //Get connection
            connection = getConnection();
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "SELECT start_date"
                            + " FROM user_scheduler_jobs"
                            + " WHERE job_name = ?";
            statement = connection.prepareStatement(query);
            //Assign values to variables
            statement.setString(1, instance.getDbName() + "_BACKUP");
            //Execute query
            result = statement.executeQuery();

            //Instantiate date object
            if (result.next()) {
                date = new java.util.Date(result.getTimestamp(1).getTime());
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING BACKUP START DATE FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex);
        } finally {
            try {
                result.close();
            } catch (Exception e) {}
            try {
                statement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return date;
    }
    
    /**
     * Creates scheduled backups to tape.
     * @param instance instance to enable backups to tape for.
     * @param username username requesting this job.
     * @param startDate date to start backups to tape.
     * @param admin 1 if user is admin, 0 otherwise.
     * @return true if the operation was successful, false otherwise.
     */
    public boolean createScheduledBackupToTape(Instance instance, String username, java.util.Date startDate, int admin) {
        Connection connection = null;
        CallableStatement createScheduleStatement = null;
        int createScheduleResult = 0;
        try {
            //Get connection
            connection = getConnection();
            
            //Create call create_backup_to_tape (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER,
            //                                      start_date_param IN DATE)
            String createScheduleCall = "{ call create_backup_to_tape(?, ?, ?, ?, ?, ?) }";
            createScheduleStatement = connection.prepareCall(createScheduleCall);
            //Set values
            createScheduleStatement.setString(1, instance.getOwner());
            createScheduleStatement.setString(2, instance.getDbName());
            createScheduleStatement.setString(3, instance.getDbType());
            createScheduleStatement.setString(4, username);
            createScheduleStatement.setInt(5, admin);
            createScheduleStatement.setTimestamp(6, new java.sql.Timestamp(startDate.getTime()));
            //Execute
            createScheduleResult = createScheduleStatement.executeUpdate();
        }
        catch (NamingException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR ENABLING BACKUPS TO TAPE FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex);
        }
        catch (SQLException ex) {
            try {
                //Rollback updates
                connection.rollback();
            }
            catch (SQLException ex1) {
                Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK ENABLING BACKUPS TO TAPE FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex1);
            }
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR ENABLING BACKUPS TO TAPE FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex);
        }

        finally {
            try {
                createScheduleStatement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        if (createScheduleResult > 0) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.INFO, "ENABLE AUTOMATIC BACKUPS TO TAPE JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
            return true;
        }
        else
            return false;
    }
    
    /**
     * Deletes a scheduled backup to tape.
     * @param instance instance to delete the scheduled backup from.
     * @return true if the operation was successful, false otherwise.
     */
    public boolean deleteScheduledBackupToTape(Instance instance, String username, int admin) {
        Connection connection = null;
        CallableStatement deleteScheduleStatement = null;
        int deleteScheduleResult = 0;
        try {
            //Get connection
            connection = getConnection();
            
            //Create call delete_backup_to_tape (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER)
            String deleteScheduleCall = "{ call delete_backup_to_tape(?, ?, ?, ?, ?) }";
            deleteScheduleStatement = connection.prepareCall(deleteScheduleCall);
            //Set values
            deleteScheduleStatement.setString(1, instance.getOwner());
            deleteScheduleStatement.setString(2, instance.getDbName());
            deleteScheduleStatement.setString(3, instance.getDbType());
            deleteScheduleStatement.setString(4, username);
            deleteScheduleStatement.setInt(5, admin);
            //Execute
            deleteScheduleResult = deleteScheduleStatement.executeUpdate();
            
            if (deleteScheduleResult != CallableStatement.EXECUTE_FAILED)
                 Logger.getLogger(JobDAO.class.getName()).log(Level.INFO, "DISABLE AUTOMATIC BACKUPS TO TAPE JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
        }
        catch (NamingException | SQLException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING SCHEDULED BACKUP TO TAPE FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex);
        }

        finally {
            try {
                deleteScheduleStatement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        if (deleteScheduleResult > 0)
            return true;
        else
            return false;
    }
    
    /**
     * Checks if backups to tape are enabled or disabled, and returns the start date.
     * @param instance DBOD instance to get the start date of.
     * @return start date for backups to tape for the given instance, null otherwise.
     */
    public java.util.Date getBackupToTapeStartDate(Instance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        java.util.Date date = null;
        try {
            //Get connection
            connection = getConnection();
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "SELECT start_date"
                            + " FROM user_scheduler_jobs"
                            + " WHERE job_name = ?";
            statement = connection.prepareStatement(query);
            //Assign values to variables
            statement.setString(1, instance.getDbName() + "_BACKUP_TO_TAPE");
            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            if (result.next()) {
                date = new java.util.Date(result.getTimestamp(1).getTime());
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING BACKUP TO TAPE ENABLED FOR USERNAME " + instance.getOwner() + " AND DB_NAME " + instance.getDbName(), ex);
        } finally {
            try {
                result.close();
            } catch (Exception e) {}
            try {
                statement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return date;
    }
}
