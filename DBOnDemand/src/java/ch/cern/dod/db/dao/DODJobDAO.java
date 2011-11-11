package ch.cern.dod.db.dao;

import ch.cern.dod.db.entity.DODCommandParam;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODJob;
import ch.cern.dod.util.DODConstants;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Clob;
import java.util.Calendar;

/**
 * DAO for DODJob entity.
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class DODJobDAO {

    /**
     * Date formatter date and time
     */
    DateFormat dateTimeFormatter = new SimpleDateFormat(DODConstants.DATE_TIME_FORMAT);
    
    /**
     * Obtains a new connection from the pool.
     * @return a connection to the database.
     * @throws NamingException if the context cannot be found.
     * @throws SQLException if the datasource cannot be created.
     */
    private Connection getConnection() throws NamingException, SQLException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup(DODConstants.ENVIRONMENT_CONTEXT);
        DataSource dataSource = (DataSource) envContext.lookup(DODConstants.DATA_SOURCE);
        return dataSource.getConnection();
    }

    /**
     * Obtains the list of jobs for a specific instance.
     * @param instance DOD instance to get the jobs from.
     * @return list of jobs for the specified instance.
     */
    public List<DODJob> selectByInstance(DODInstance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODJob> jobs = new ArrayList<DODJob>();
        try {
            //Get connection
            connection = getConnection();
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "SELECT username, db_name, command_name, type, creation_date, completion_date, requester, state, admin_action, result"
                            + " FROM dod_jobs WHERE username = ? AND db_name = ? ORDER BY creation_date DESC";
            statement = connection.prepareStatement(query);
            //Assign values to variables
            statement.setString(1, instance.getUsername());
            statement.setString(2, instance.getDbName());
            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                DODJob job = new DODJob();
                job.setUsername(result.getString(1));
                job.setDbName(result.getString(2));
                job.setCommandName(result.getString(3));
                job.setType(result.getString(4));
                job.setCreationDate(new java.util.Date(result.getTimestamp(5).getTime()));
                if (result.getTimestamp(6) != null)
                    job.setCompletionDate(new java.util.Date(result.getTimestamp(6).getTime()));
                job.setRequester(result.getString(7));
                job.setState(result.getString(8));
                job.setAdminAction(result.getInt(9));
                job.setResult(result.getString(10));
                jobs.add(job);
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING JOB FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING JOB FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
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
        return jobs;
    }

    /**
     * Selects a log for a specific job.
     * @param job job to obtain the log from.
     * @return String containing the job.
     */
    public String selectLogByJob(DODJob job) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        String log = "";
        try {
            //Get connection
            connection = getConnection();
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "SELECT log FROM dod_jobs"
                            + " WHERE username = ? AND db_name = ? AND command_name = ? AND type = ? AND creation_date = ? ORDER BY creation_date";
            statement = connection.prepareStatement(query);
            //Assign values to variables
            statement.setString(1, job.getUsername());
            statement.setString(2, job.getDbName());
            statement.setString(3, job.getCommandName());
            statement.setString(4, job.getType());
            statement.setTimestamp(5,  new java.sql.Timestamp(job.getCreationDate().getTime()));

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            if (result.next()) {
                Clob logClob = (Clob) result.getClob(1);
                if (logClob != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(logClob.getAsciiStream()));
                    String line = null;
                    StringBuilder buffer = new StringBuilder();
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                        buffer.append("\n");
                    }
                    log = buffer.toString();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING LOG FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
        } catch (NamingException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING LOG FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING LOG FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
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
        return log;
    }
    
    /**
     * Inserts a job in the database. It is a single insert, it does not update the instance or insert any parameters.
     * It simple inserts the job for logging purposes. That is why the job is always in state FINISHED.
     * @param job job to be inserted.
     * @param log log for the job.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int insert(DODJob job, String log) {
        Connection connection = null;
        PreparedStatement insertJobStatement = null;
        int insertJobResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Prepare query for the prepared statement (to avoid SQL injection)
            String insertQuery = "INSERT INTO dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state, log)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            insertJobStatement = connection.prepareStatement(insertQuery);
            //Assign values to variables
            insertJobStatement.setString(1, job.getUsername());
            insertJobStatement.setString(2, job.getDbName());
            insertJobStatement.setString(3, job.getCommandName());
            insertJobStatement.setString(4, job.getType());
            insertJobStatement.setTimestamp(5, new java.sql.Timestamp(job.getCreationDate().getTime()));
            insertJobStatement.setString(6, job.getRequester());
            insertJobStatement.setInt(7, job.getAdminAction());
            insertJobStatement.setString(8, job.getState());
            insertJobStatement.setString(9, log);

            //Execute query
            insertJobResult = insertJobStatement.executeUpdate();
            
            //Commit queries
            connection.commit();
        }
        catch (NamingException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
        }
        catch (SQLException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
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
     * Inserts a job in the database.
     * @param job job to be inserted.
     * @param params params to be inserted with the job.
     * @return 1 if the operation was succesful, 0 otherwise.
     */
    public int insert(DODJob job, List<DODCommandParam> params) {
        Connection connection = null;
        PreparedStatement insertJobStatement = null;
        PreparedStatement insertParamsStatement = null;
        PreparedStatement updateInstanceStatement = null;
        int insertJobResult = 0;
        int insertParamsResult = 0;
        int updateInstanceResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Set autocommit to false to execute multiple queries and rollback in case something goes wrong
            connection.setAutoCommit(false);
            //Prepare query for the prepared statement (to avoid SQL injection)
            String insertQuery = "INSERT INTO dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            insertJobStatement = connection.prepareStatement(insertQuery);
            //Assign values to variables
            insertJobStatement.setString(1, job.getUsername());
            insertJobStatement.setString(2, job.getDbName());
            insertJobStatement.setString(3, job.getCommandName());
            insertJobStatement.setString(4, job.getType());
            insertJobStatement.setTimestamp(5, new java.sql.Timestamp(job.getCreationDate().getTime()));
            insertJobStatement.setString(6, job.getRequester());
            insertJobStatement.setInt(7, job.getAdminAction());
            insertJobStatement.setString(8, job.getState());

            //Execute query
            insertJobResult = insertJobStatement.executeUpdate();

            //If the insert operation was successful, insert params (if any) and update intance
            if (insertJobResult != PreparedStatement.EXECUTE_FAILED) {
                if (params != null && !params.isEmpty()) {
                    //Prepare query for the prepared statement (to avoid SQL injection)
                    String paramsQuery = "INSERT INTO dod_command_params (username, db_name, command_name, type, creation_date, name, value) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    insertParamsStatement = connection.prepareStatement(paramsQuery);
                    //If there are parameters
                    for (int i=0; i<params.size(); i++) {
                        DODCommandParam commandParam = params.get(i);
                        //Assign values to variables
                        insertParamsStatement.setString(1, commandParam.getUsername());
                        insertParamsStatement.setString(2, commandParam.getDbName());
                        insertParamsStatement.setString(3, commandParam.getCommandName());
                        insertParamsStatement.setString(4, commandParam.getType());
                        insertParamsStatement.setTimestamp(5, new java.sql.Timestamp(commandParam.getCreationDate().getTime()));
                        insertParamsStatement.setString(6, commandParam.getName());
                        insertParamsStatement.setString(7, commandParam.getValue());
                        insertParamsStatement.addBatch();
                    }
                    int[] results = insertParamsStatement.executeBatch();
                    insertParamsResult = results.length;
                    for (int i=0; i<results.length; i++){
                        if (results[i] == PreparedStatement.EXECUTE_FAILED) {
                            insertParamsResult = 0;
                            break;
                        }
                    }
                }
                else
                    insertParamsResult = 1;

                //Only update instance if the operation was succesful
                if (insertParamsResult != PreparedStatement.EXECUTE_FAILED) {
                    //Prepare query for the prepared statement (to avoid SQL injection)
                    String updateQuery = "UPDATE dod_instances SET state = '" + DODConstants.INSTANCE_STATE_JOB_PENDING + "' WHERE username = ? AND db_name = ?";
                    updateInstanceStatement = connection.prepareStatement(updateQuery);
                    //Assign values to variables
                    updateInstanceStatement.setString(1, job.getUsername());
                    updateInstanceStatement.setString(2, job.getDbName());
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
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
        }
        catch (SQLException ex) {
            try {
                //Rollback updates
                connection.rollback();
            }
            catch (SQLException ex1) {
                Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex1);
            }
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
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
     * Inserts a job in the database and creates a scheduled backup if necessary.
     * @param job job to be inserted.
     * @param interval_hours hours between snapshots.
     * @param params params to be inserted with the job.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int insertAndCreateScheduledBackup(DODJob job, int intervalHours, List<DODCommandParam> params) {
        Connection connection = null;
        PreparedStatement insertJobStatement = null;
        PreparedStatement insertParamsStatement = null;
        CallableStatement createScheduleStatement = null;
        PreparedStatement updateInstanceStatement = null;
        int insertJobResult = 0;
        int insertParamsResult = 0;
        int createScheduleResult = 0;
        int updateInstanceResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Set autocommit to false to execute multiple queries and rollback in case something goes wrong
                connection.setAutoCommit(false);
            
            //Only execute backup if the interval is greater than 0
            if (intervalHours > 0) {
                //Create call create_scheduled_backup (username IN VARCHAR2, db_name IN VARCHAR2, type IN VARCHAR2, requester IN VARCHAR2, admin_action IN INTEGER,
                //                                      start_date_param IN DATE, interval_hours IN INTEGER)
                String createScheduleCall = "{ call create_scheduled_backup(?, ?, ?, ?, ?, ?, ?) }";
                createScheduleStatement = connection.prepareCall(createScheduleCall);
                //Set values
                createScheduleStatement.setString(1, job.getUsername());
                createScheduleStatement.setString(2, job.getDbName());
                createScheduleStatement.setString(3, job.getType());
                createScheduleStatement.setString(4, job.getRequester());
                createScheduleStatement.setInt(5, job.getAdminAction());
                //Create Calendar to set the start date to the creationDate + intervalHours
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(job.getCreationDate());
                startDate.add(Calendar.HOUR, intervalHours);
                createScheduleStatement.setTimestamp(6, new java.sql.Timestamp(startDate.getTime().getTime()));
                createScheduleStatement.setInt(7, intervalHours);

                createScheduleResult = createScheduleStatement.executeUpdate();
            }
            else {
                createScheduleResult = 1;
            }
            
            //Only create job if the operation was succesful
            if (createScheduleResult != CallableStatement.EXECUTE_FAILED) {
                
                //Prepare query for the prepared statement (to avoid SQL injection)
                String insertQuery = "INSERT INTO dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state)"
                                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                insertJobStatement = connection.prepareStatement(insertQuery);
                //Assign values to variables
                insertJobStatement.setString(1, job.getUsername());
                insertJobStatement.setString(2, job.getDbName());
                insertJobStatement.setString(3, job.getCommandName());
                insertJobStatement.setString(4, job.getType());
                insertJobStatement.setTimestamp(5, new java.sql.Timestamp(job.getCreationDate().getTime()));
                insertJobStatement.setString(6, job.getRequester());
                insertJobStatement.setInt(7, job.getAdminAction());
                insertJobStatement.setString(8, job.getState());

                //Execute query
                insertJobResult = insertJobStatement.executeUpdate();

                //If the insert operation was successful, insert params (if any) and update intance
                if (insertJobResult != PreparedStatement.EXECUTE_FAILED) {
                    //Insert params
                    if (params != null && !params.isEmpty()) {
                        //Prepare query for the prepared statement (to avoid SQL injection)
                        String paramsQuery = "INSERT INTO dod_command_params (username, db_name, command_name, type, creation_date, name, value) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        insertParamsStatement = connection.prepareStatement(paramsQuery);
                        //If there are parameters
                        for (int i=0; i<params.size(); i++) {
                            DODCommandParam commandParam = params.get(i);
                            //Assign values to variables
                            insertParamsStatement.setString(1, commandParam.getUsername());
                            insertParamsStatement.setString(2, commandParam.getDbName());
                            insertParamsStatement.setString(3, commandParam.getCommandName());
                            insertParamsStatement.setString(4, commandParam.getType());
                            insertParamsStatement.setTimestamp(5, new java.sql.Timestamp(commandParam.getCreationDate().getTime()));
                            insertParamsStatement.setString(6, commandParam.getName());
                            insertParamsStatement.setString(7, commandParam.getValue());
                            insertParamsStatement.addBatch();
                        }
                        int[] results = insertParamsStatement.executeBatch();
                        insertParamsResult = results.length;
                        for (int i=0; i<results.length; i++){
                            if (results[i] == PreparedStatement.EXECUTE_FAILED) {
                                insertParamsResult = 0;
                                break;
                            }
                        }
                    }
                    else
                        insertParamsResult = 1;

                    //If the operation was succesful create the scheduled backup
                    if (insertParamsResult != PreparedStatement.EXECUTE_FAILED) {
                        //Prepare query for the prepared statement (to avoid SQL injection)
                        String updateQuery = "UPDATE dod_instances SET state = '" + DODConstants.INSTANCE_STATE_JOB_PENDING + "' WHERE username = ? AND db_name = ?";
                        updateInstanceStatement = connection.prepareStatement(updateQuery);
                        //Assign values to variables
                        updateInstanceStatement.setString(1, job.getUsername());
                        updateInstanceStatement.setString(2, job.getDbName());
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
        }
        catch (NamingException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
        }
        catch (SQLException ex) {
            try {
                //Rollback updates
                connection.rollback();
            }
            catch (SQLException ex1) {
                Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK JOB FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex1);
            }
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING JOB FOR USERNAME " + job.getUsername() + " AND DB_NAME " + job.getDbName(), ex);
        }

        finally {
            try {
                insertJobStatement.close();
            } catch (Exception e) {}
            try {
                insertParamsStatement.close();
            } catch (Exception e) {}
            try {
                createScheduleStatement.close();
            } catch (Exception e) {}
            try {
                updateInstanceStatement.close();
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
     * Deletes a scheduled backup.
     * @param instance instance to delete the scheduled backup from.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int deleteScheduledBackup(DODInstance instance, String username, int admin) {
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
            deleteScheduleStatement.setString(1, instance.getUsername());
            deleteScheduleStatement.setString(2, instance.getDbName());
            deleteScheduleStatement.setString(3, instance.getDbType());
            deleteScheduleStatement.setString(4, username);
            deleteScheduleStatement.setInt(5, admin);

            deleteScheduleResult = deleteScheduleStatement.executeUpdate();
            
            if (deleteScheduleResult != CallableStatement.EXECUTE_FAILED)
                 Logger.getLogger(DODJobDAO.class.getName()).log(Level.INFO, "DISABLE AUTOMATIC BACKUPS JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
        }
        catch (NamingException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING SCHEDULED BACKUP FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        }
        catch (SQLException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING SCHEDULED BACKUP FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        }

        finally {
            try {
                deleteScheduleStatement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return deleteScheduleResult;
    }
}
