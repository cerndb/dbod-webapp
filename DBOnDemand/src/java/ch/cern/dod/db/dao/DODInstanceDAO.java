package ch.cern.dod.db.dao;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.util.DODConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DAO for DODInstance entity.
 * @author Daniel Gomez Blanco
 * @version 23/11/2011
 */
public class DODInstanceDAO {

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
     * Selects all the instances in the database.
     * @return List of all the instances in the database.
     */
    public List<DODInstance> selectAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODInstance> instances = new ArrayList<DODInstance>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, state, status"
                            + " FROM dod_instances WHERE status = '1'");
            query.append("ORDER BY db_name");
            statement = connection.prepareStatement(query.toString());

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                DODInstance instance = new DODInstance();
                instance.setUsername(result.getString(1));
                instance.setDbName(result.getString(2));
                instance.setEGroup(result.getString(3));
                instance.setCategory(result.getString(4));
                instance.setCreationDate(new java.util.Date(result.getDate(5).getTime()));
                if (result.getDate(6) != null)
                    instance.setExpiryDate(new java.util.Date(result.getDate(6).getTime()));
                instance.setDbType(result.getString(7));
                instance.setDbSize(result.getInt(8));
                instance.setNoConnections(result.getInt(9));
                instance.setProject(result.getString(10));
                instance.setDescription(result.getString(11));
                instance.setState(result.getString(12));
                instance.setStatus(result.getBoolean(13));
                instances.add(instance);
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR ADMIN",ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR ADMIN",ex);
        } finally {
            try {
                result.close();
            } catch (Exception e) {
            }
            try {
                statement.close();
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        return instances;
    }

    /**
     * Select the instances belonging to a username or to any of their e-groups.
     * @param username username to obtain instances.
     * @param egroups e-groups that the user belongs to.
     * @return List of instances belonging to a username or to any of their e-groups.
     */
    public List<DODInstance> selectByUserNameAndEGroups(String username, String egroups) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODInstance> instances = new ArrayList<DODInstance>();
        try {
            //Get connection
            connection = getConnection();
            
            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, state, status"
                            + " FROM dod_instances WHERE status = '1' ");
            //Append egroups (if any)
            StringTokenizer tokenizer = new StringTokenizer("");
            if (egroups != null && !egroups.isEmpty()) {
                query.append("AND (username = ? ");
                tokenizer = new StringTokenizer(egroups, ";");
                int tokens = tokenizer.countTokens();
                if (tokens > 0) {
                    query.append("OR ( e_group <> '' AND e_group IN (");
                    for (int i=0; i<tokens-1; i++) {
                        query.append("?, ");
                    }
                    query.append("?))) ");
                }
            }
            else {
                query.append("AND username = ? ");
            }
            query.append("ORDER BY db_name");
            statement = connection.prepareStatement(query.toString());

            //Assign values to variables
            statement.setString(1, username);
            int i = 2;
            while (tokenizer.hasMoreTokens()) {
                statement.setString(i, tokenizer.nextToken());
                i++;
            }
            
            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                DODInstance instance = new DODInstance();
                instance.setUsername(result.getString(1));
                instance.setDbName(result.getString(2));
                instance.setEGroup(result.getString(3));
                instance.setCategory(result.getString(4));
                instance.setCreationDate(new java.util.Date(result.getDate(5).getTime()));
                if (result.getDate(6) != null)
                    instance.setExpiryDate(new java.util.Date(result.getDate(6).getTime()));
                instance.setDbType(result.getString(7));
                instance.setDbSize(result.getInt(8));
                instance.setNoConnections(result.getInt(9));
                instance.setProject(result.getString(10));
                instance.setDescription(result.getString(11));
                instance.setState(result.getString(12));
                instance.setStatus(result.getBoolean(13));
                instances.add(instance);
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR USERNAME " + username ,ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR USERNAME " + username ,ex);
        } finally {
            try {
                result.close();
            } catch (Exception e) {
            }
            try {
                statement.close();
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        return instances;
    }

    /**
     * Select a specific instance by the primary key.
     * @param username Username creator of the instance.
     * @param dbName DB name of the instance.
     * @return DOD instance for the username and DB name specified.
     */
    public DODInstance selectById(String username, String dbName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        DODInstance instance = null;
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, state, status"
                            + " FROM dod_instances WHERE username = ? AND db_name = ?");
            statement = connection.prepareStatement(query.toString());

            //Assign values to variables
            statement.setString(1, username);
            statement.setString(2, dbName);

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance object
            if (result.next()) {
                instance = new DODInstance();
                instance.setUsername(result.getString(1));
                instance.setDbName(result.getString(2));
                instance.setEGroup(result.getString(3));
                instance.setCategory(result.getString(4));
                instance.setCreationDate(new java.util.Date(result.getDate(5).getTime()));
                if (result.getDate(6) != null)
                    instance.setExpiryDate(new java.util.Date(result.getDate(6).getTime()));
                instance.setDbType(result.getString(7));
                instance.setDbSize(result.getInt(8));
                instance.setNoConnections(result.getInt(9));
                instance.setProject(result.getString(10));
                instance.setDescription(result.getString(11));
                instance.setState(result.getString(12));
                instance.setStatus(result.getBoolean(13));
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR USERNAME " + username ,ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR USERNAME " + username ,ex);
        } finally {
            try {
                result.close();
            } catch (Exception e) {
            }
            try {
                statement.close();
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        return instance;
    }

    /**
     * Inserts a new instance in the database.
     * @param instance instance to be inserted.
     * @return 1 if the operation was succesful, 0 otherwise.
     */
    public int insert(DODInstance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        int result = 0;
        try {
            //Get connection
            connection = getConnection();
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "INSERT INTO dod_instances (username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, state, status)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            //Assign values to variables
            statement.setString(1, instance.getUsername());
            statement.setString(2, instance.getDbName());
            statement.setString(3, instance.getEGroup());
            statement.setString(4, instance.getCategory());
            statement.setDate(5, new java.sql.Date(instance.getCreationDate().getTime()));
            if (instance.getExpiryDate() != null)
                statement.setDate(6, new java.sql.Date(instance.getExpiryDate().getTime()));
            else
                statement.setDate(6, null);
            statement.setString(7, instance.getDbType());
            statement.setInt(8, instance.getDbSize());
            statement.setInt(9, instance.getNoConnections());
            statement.setString(10, instance.getProject());
            statement.setString(11, instance.getDescription());
            statement.setString(12, instance.getState());
            statement.setBoolean(13, instance.getStatus());
            //Execute query
            result = statement.executeUpdate();
            
        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
            if (ex.getErrorCode() == 1)
                return -1;
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        return result;
    }

    /**
     * Deletes an instance from the database. It does not delete it physically, but logically,
     * setting the status field to 0. It also creates a job to log the user that deleted the isntance.
     * @param instance instance to be deleted.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int delete(DODInstance instance, String requester, int admin) {
        Connection connection = null;
        PreparedStatement deleteStatement = null;
        PreparedStatement insertJobStatement = null;
        int deleteResult = 0;
        int insertJobResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Set autocommit to false to execute multiple queries and rollback in case something goes wrong
            connection.setAutoCommit(false);
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "UPDATE dod_instances SET status = '0' WHERE username = ? AND db_name = ?";
            deleteStatement = connection.prepareStatement(query);
            //Assign values to variables
            deleteStatement.setString(1, instance.getUsername());
            deleteStatement.setString(2, instance.getDbName());
            //Execute query
            deleteResult = deleteStatement.executeUpdate();
            
            //If the operation was OK log it as a job
            if (deleteResult != PreparedStatement.EXECUTE_FAILED) {
                //Prepare query for the prepared statement (to avoid SQL injection)
                String insertQuery = "INSERT INTO dod_jobs (username, db_name, command_name, type, creation_date, requester, admin_action, state, log)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                insertJobStatement = connection.prepareStatement(insertQuery);
                //Assign values to variables
                insertJobStatement.setString(1, instance.getUsername());
                insertJobStatement.setString(2, instance.getDbName());
                insertJobStatement.setString(3, DODConstants.JOB_DESTROY);
                insertJobStatement.setString(4, instance.getDbType());
                java.util.Date now = new java.util.Date();
                insertJobStatement.setTimestamp(5, new java.sql.Timestamp(now.getTime()));
                insertJobStatement.setString(6, requester);
                insertJobStatement.setInt(7, admin);
                insertJobStatement.setString(8, DODConstants.JOB_STATE_FINISHED_OK);
                insertJobStatement.setString(9, "Instance successfully removed!");
                
                //Execute query
                insertJobResult = insertJobStatement.executeUpdate();
                
                //Log the result of operation is successful
                if (insertJobResult != PreparedStatement.EXECUTE_FAILED) {
                    Logger.getLogger(DODJobDAO.class.getName()).log(Level.INFO, "INSTANCE {0} SUCCESSFULLY REMOVED BY USER {1}!", new Object[]{instance.getDbName(), requester});
                
                    //Commit queries
                    connection.commit();
                }
                else {
                    connection.rollback();
                    return 0;
                }
            }
                

        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } catch (SQLException ex) {
            try {
                //Rollback updates
                connection.rollback();
            }
            catch (SQLException ex1) {
                Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK INSTANCE DELETION FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex1);
            }
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } finally {
            try {
                deleteStatement.close();
            } catch (Exception e) {
            }
            try {
                insertJobStatement.close();
            } catch (Exception e) {
            }
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        return deleteResult;
    }

    /**
     * Updates an instance with new values for e-group, expiry date, project or description.
     * @param instance instance to be updated.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int update(DODInstance oldInstance, DODInstance newInstance) {
        Connection connection = null;
        PreparedStatement statement = null;
        int result = 0;
        try {
            //Get connection
            connection = getConnection();
            
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "UPDATE dod_instances SET e_group = ?, expiry_date = ?, project = ?, description = ? WHERE username = ? AND db_name = ?";
            statement = connection.prepareStatement(query);
            //Assign values to variables
            statement.setString(1, newInstance.getEGroup());
            if (newInstance.getExpiryDate() != null)
                statement.setDate(2, new java.sql.Date(newInstance.getExpiryDate().getTime()));
            else
                statement.setDate(2, null);
            statement.setString(3, newInstance.getProject());
            statement.setString(4, newInstance.getDescription());
            statement.setString(5, oldInstance.getUsername());
            statement.setString(6, oldInstance.getDbName());
            //Execute query
            result = statement.executeUpdate();

        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR UPDATING INSTANCE FOR USERNAME " + oldInstance.getUsername() + " AND DB_NAME " + oldInstance.getDbName(), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR UPDATING INSTANCE FOR USERNAME " + oldInstance.getUsername() + " AND DB_NAME " + oldInstance.getDbName(), ex);
        } finally {
            try {
                statement.close();
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        return result;
    }
}
