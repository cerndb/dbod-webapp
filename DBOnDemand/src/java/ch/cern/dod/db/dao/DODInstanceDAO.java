package ch.cern.dod.db.dao;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODInstanceChange;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.util.DODConstants;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
     * @param upgrades upgrades available.
     * @return List of all the instances in the database.
     */
    public List<DODInstance> selectAll(List<DODUpgrade> upgrades) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODInstance> instances = new ArrayList<DODInstance>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, shared_instance"
                            + " FROM dod_instances WHERE status = '1'"
                            + " ORDER BY db_name");
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
                instance.setVersion(result.getString(12));
                instance.setState(result.getString(13));
                instance.setStatus(result.getBoolean(14));
                instance.setMaster(result.getString(15));
                instance.setSlave(result.getString(16));
                instance.setSharedInstance(result.getString(17));
                //Check if instance needs upgrade
                if (upgrades != null) {
                    for (int i=0; i < upgrades.size(); i++) {
                        DODUpgrade upgrade = upgrades.get(i);
                        if (upgrade.getDbType().equals(instance.getDbType()) && upgrade.getCategory().equals(instance.getCategory())
                                && upgrade.getVersionFrom().equals(instance.getVersion()))
                            instance.setUpgradeTo(upgrade.getVersionTo());
                    }
                }
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
     * @param upgrades upgrades available.
     * @return List of instances belonging to a username or to any of their e-groups.
     */
    public List<DODInstance> selectByUserNameAndEGroups(String username, String egroups, List<DODUpgrade> upgrades) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODInstance> instances = new ArrayList<DODInstance>();
        try {
            //Get connection
            connection = getConnection();
            
            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, shared_instance"
                            + " FROM dod_instances WHERE status = '1' ");
            //Append egroups (if any)
            StringTokenizer tokenizer = new StringTokenizer("");
            if (egroups != null && !egroups.isEmpty()) {
                query.append("AND (username = ? ");
                tokenizer = new StringTokenizer(egroups, ";");
                int tokens = tokenizer.countTokens();
                if (tokens > 0) {
                    query.append("OR ( e_group IS NOT NULL AND e_group IN (");
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
                instance.setVersion(result.getString(12));
                instance.setState(result.getString(13));
                instance.setStatus(result.getBoolean(14));
                instance.setMaster(result.getString(15));
                instance.setSlave(result.getString(16));
                instance.setSharedInstance(result.getString(17));
                //Check if instance needs upgrade
                if (upgrades != null) {
                    for (int j=0; j < upgrades.size(); j++) {
                        DODUpgrade upgrade = upgrades.get(j);
                        if (upgrade.getDbType().equals(instance.getDbType()) && upgrade.getCategory().equals(instance.getCategory())
                                && upgrade.getVersionFrom().equals(instance.getVersion()))
                            instance.setUpgradeTo(upgrade.getVersionTo());
                    }
                }
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
     * Selects the instances in the database for the specified sharedInstance.
     * @param sharedInstance name of the shared instance.
     * @param upgrades upgrades available.
     * @return List of the instances in the shared instance.
     */
    public List<DODInstance> selectSharedInstances(String sharedInstance, List<DODUpgrade> upgrades) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODInstance> instances = new ArrayList<DODInstance>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, shared_instance"
                            + " FROM dod_instances WHERE status = '1' AND shared_instance = ?"
                            + " ORDER BY db_name");
            statement = connection.prepareStatement(query.toString());
            
            //Assign values to variables
            statement.setString(1, sharedInstance);

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
                instance.setVersion(result.getString(12));
                instance.setState(result.getString(13));
                instance.setStatus(result.getBoolean(14));
                instance.setMaster(result.getString(15));
                instance.setSlave(result.getString(16));
                instance.setSharedInstance(result.getString(17));
                //Check if instance needs upgrade
                if (upgrades != null) {
                    for (int i=0; i < upgrades.size(); i++) {
                        DODUpgrade upgrade = upgrades.get(i);
                        if (upgrade.getDbType().equals(instance.getDbType()) && upgrade.getCategory().equals(instance.getCategory())
                                && upgrade.getVersionFrom().equals(instance.getVersion()))
                            instance.setUpgradeTo(upgrade.getVersionTo());
                    }
                }
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
     * Select a specific instance by its DB name.
     * @param dbName DB name of the instance.
     * @param upgrades upgrades available.
     * @return DOD instance for the username and DB name specified.
     */
    public DODInstance selectByDbName(String dbName, List<DODUpgrade> upgrades) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        DODInstance instance = null;
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, shared_instance"
                            + " FROM dod_instances WHERE db_name = ? AND status = '1'");
            statement = connection.prepareStatement(query.toString());

            //Assign values to variables
            statement.setString(1, dbName);

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
                instance.setVersion(result.getString(12));
                instance.setState(result.getString(13));
                instance.setStatus(result.getBoolean(14));
                instance.setMaster(result.getString(15));
                instance.setSlave(result.getString(16));
                instance.setSharedInstance(result.getString(17));
                //Check if instance needs upgrade
                if (upgrades != null) {
                    for (int i=0; i < upgrades.size(); i++) {
                        DODUpgrade upgrade = upgrades.get(i);
                        if (upgrade.getDbType().equals(instance.getDbType()) && upgrade.getCategory().equals(instance.getCategory())
                                && upgrade.getVersionFrom().equals(instance.getVersion()))
                            instance.setUpgradeTo(upgrade.getVersionTo());
                    }
                }
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCE FOR DB NAME " + dbName ,ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCE FOR DB NAME " + dbName ,ex);
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
        PreparedStatement instanceStatement = null;
        PreparedStatement masterStatement = null;
        int instanceResult = 0;
        int masterResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Set autocommit to false to execute multiple queries and rollback in case something goes wrong
            connection.setAutoCommit(false);
            
            //Prepare query for the prepared statement (to avoid SQL injection)
            String instanceQuery = "INSERT INTO dod_instances (username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, shared_instance)"
                            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            instanceStatement = connection.prepareStatement(instanceQuery);
            //Assign values to variables
            instanceStatement.setString(1, instance.getUsername());
            instanceStatement.setString(2, instance.getDbName());
            instanceStatement.setString(3, instance.getEGroup());
            instanceStatement.setString(4, instance.getCategory());
            instanceStatement.setDate(5, new java.sql.Date(instance.getCreationDate().getTime()));
            if (instance.getExpiryDate() != null)
                instanceStatement.setDate(6, new java.sql.Date(instance.getExpiryDate().getTime()));
            else
                instanceStatement.setDate(6, null);
            instanceStatement.setString(7, instance.getDbType());
            instanceStatement.setInt(8, instance.getDbSize());
            instanceStatement.setInt(9, instance.getNoConnections());
            instanceStatement.setString(10, instance.getProject());
            instanceStatement.setString(11, instance.getDescription());
            instanceStatement.setString(12, instance.getVersion());
            instanceStatement.setString(13, instance.getState());
            instanceStatement.setBoolean(14, instance.getStatus());
            instanceStatement.setString(15, instance.getMaster());
            instanceStatement.setString(16, instance.getSlave());
            instanceStatement.setString(17, instance.getSharedInstance());
            //Execute query
            instanceResult = instanceStatement.executeUpdate();
            
            if (instance.getMaster() != null && !instance.getMaster().isEmpty()) {
                //Update master in case the insert was successful
                if (instanceResult > 0) {
                    //Prepare query for the prepared statement (to avoid SQL injection)
                    String masterQuery = "UPDATE dod_instances SET slave = ?, master = NULL "
                                            + "WHERE db_name = ?";
                    masterStatement = connection.prepareStatement(masterQuery);
                    //Set values
                    masterStatement.setString(1, instance.getDbName());
                    masterStatement.setString(2, instance.getMaster());
                    //Execute query
                    masterResult = masterStatement.executeUpdate();
                    
                    //Check result
                    if (masterResult <= 0) {
                        connection.rollback();
                        return 0;
                    }
                }
            }
            else {
                masterResult = 1;
            }
            
            //Commit queries
            connection.commit();
            
        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } catch (SQLException ex) {
            try {
                //Rollback updates
                connection.rollback();
            }
            catch (SQLException ex1) {
                Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK INSERTING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex1);
            }
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } finally {
            try {
                instanceStatement.close();
            } catch (Exception e) {}
            try {
                masterStatement.close();
            } catch (Exception e) {}
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return masterResult;
    }

    /**
     * Deletes an instance from the database. It does not delete it physically, but logically,
     * setting the status field to 0.
     * @param instance instance to be deleted.
     * @return 1 if the operation was successful, 0 otherwise.
     * @deprecated instances are destroyed via FIM now
     */
    public int delete(DODInstance instance) {
        Connection connection = null;
        CallableStatement destroyStatement = null;
        int deleteResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Create call destroy_instance(username IN VARCHAR2, db_name IN VARCHAR2)
            String destroyCall = "{ call destroy_instance(?, ?) }";
            destroyStatement = connection.prepareCall(destroyCall);
            //Set values
            destroyStatement.setString(1, instance.getUsername());
            destroyStatement.setString(2, instance.getDbName());

            deleteResult = destroyStatement.executeUpdate();
        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } finally {
            try {
                destroyStatement.close();
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
     * Updates an instance with new values.
     * @param instance instance to update.
     * @param 
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int update(DODInstance oldInstance, DODInstance newInstance, String requester) {
        Connection connection = null;
        PreparedStatement updateStatement = null;
        PreparedStatement insertStatement = null;
        int updateResult = 0;
        try {
            //Get connection
            connection = getConnection();
            connection.setAutoCommit(false);

            //Prepare query for the prepared statement (to avoid SQL injection)
            String updateQuery = "UPDATE dod_instances SET e_group = ?, expiry_date = ?, project = ?, description = ?, category = ?, no_connections = ?, db_size = ?, state = ?, version = ?, master = ?, slave = ?, shared_instance = ? WHERE username = ? AND db_name = ?";
            updateStatement = connection.prepareStatement(updateQuery);
            //Assign values to variables
            updateStatement.setString(1, newInstance.getEGroup());
            if (newInstance.getExpiryDate() != null)
                updateStatement.setDate(2, new java.sql.Date(newInstance.getExpiryDate().getTime()));
            else
                updateStatement.setDate(2, null);
            updateStatement.setString(3, newInstance.getProject());
            updateStatement.setString(4, newInstance.getDescription());
            updateStatement.setString(5, newInstance.getCategory());
            updateStatement.setInt(6, newInstance.getNoConnections());
            updateStatement.setInt(7, newInstance.getDbSize());
            updateStatement.setString(8, newInstance.getState());
            updateStatement.setString(9, newInstance.getVersion());
            updateStatement.setString(10, newInstance.getMaster());
            updateStatement.setString(11, newInstance.getSlave());
            updateStatement.setString(12, newInstance.getSharedInstance());
            updateStatement.setString(13, newInstance.getUsername());
            updateStatement.setString(14, newInstance.getDbName());
            //Execute query
            updateResult = updateStatement.executeUpdate();
            
            //If update was successful
            if (updateResult > 0) {
                String insertQuery = "INSERT INTO dod_instance_changes (username, db_name, attribute, change_date, requester, old_value, new_value) VALUES (?,?,?,?,?,?,?)";
                insertStatement = connection.prepareStatement(insertQuery);
                //Check for changes on fields editable by users
                if ((oldInstance.getEGroup() == null && newInstance.getEGroup() != null)
                        || (oldInstance.getEGroup() != null && !oldInstance.getEGroup().equals(newInstance.getEGroup()))) {
                    insertStatement.setString(1, newInstance.getUsername());
                    insertStatement.setString(2, newInstance.getDbName());
                    insertStatement.setString(3, "e-Group");
                    insertStatement.setTimestamp(4, new java.sql.Timestamp((new java.util.Date()).getTime()));
                    insertStatement.setString(5, requester);
                    insertStatement.setString(6, oldInstance.getEGroup());
                    insertStatement.setString(7, newInstance.getEGroup());
                    insertStatement.addBatch();
                }
                if ((oldInstance.getExpiryDate() == null && newInstance.getExpiryDate() != null)
                        || (oldInstance.getExpiryDate() != null && !oldInstance.getExpiryDate().equals(newInstance.getExpiryDate()))) {
                    DateFormat dateFormatter = new SimpleDateFormat(DODConstants.DATE_FORMAT);
                    insertStatement.setString(1, newInstance.getUsername());
                    insertStatement.setString(2, newInstance.getDbName());
                    insertStatement.setString(3, "Expiry Date");
                    insertStatement.setTimestamp(4, new java.sql.Timestamp((new java.util.Date()).getTime()));
                    insertStatement.setString(5, requester);
                    if (oldInstance.getExpiryDate() != null)
                        insertStatement.setString(6, dateFormatter.format(oldInstance.getExpiryDate()));
                    else
                        insertStatement.setString(6, null);
                    if (newInstance.getExpiryDate() != null)
                        insertStatement.setString(7, dateFormatter.format(newInstance.getExpiryDate()));
                    else
                        insertStatement.setString(7, null);
                    insertStatement.addBatch();
                }
                if ((oldInstance.getProject() == null && newInstance.getProject() != null)
                        || (oldInstance.getProject() != null && !oldInstance.getProject().equals(newInstance.getProject()))) {
                    insertStatement.setString(1, newInstance.getUsername());
                    insertStatement.setString(2, newInstance.getDbName());
                    insertStatement.setString(3, "Project");
                    insertStatement.setTimestamp(4, new java.sql.Timestamp((new java.util.Date()).getTime()));
                    insertStatement.setString(5, requester);
                    insertStatement.setString(6, oldInstance.getProject());
                    insertStatement.setString(7, newInstance.getProject());
                    insertStatement.addBatch();
                }
                if ((oldInstance.getDescription() == null && newInstance.getDescription() != null)
                        || (oldInstance.getDescription() != null && !oldInstance.getDescription().equals(newInstance.getDescription()))) {
                    insertStatement.setString(1, newInstance.getUsername());
                    insertStatement.setString(2, newInstance.getDbName());
                    insertStatement.setString(3, "Description");
                    insertStatement.setTimestamp(4, new java.sql.Timestamp((new java.util.Date()).getTime()));
                    insertStatement.setString(5, requester);
                    insertStatement.setString(6, oldInstance.getDescription());
                    insertStatement.setString(7, newInstance.getDescription());
                    insertStatement.addBatch();
                }
                int[] results = insertStatement.executeBatch();
                for (int i=0; i<results.length; i++){
                    if (results[i] == PreparedStatement.EXECUTE_FAILED) {
                        updateResult = 0;
                        connection.rollback();                        
                        break;
                    }
                }
            }
            
            //Commit transaction
            connection.commit();

        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR UPDATING INSTANCE FOR USERNAME " + oldInstance.getUsername() + " AND DB_NAME " + oldInstance.getDbName(), ex);
        } catch (SQLException ex) {
            updateResult = 0;
            try {
                connection.rollback();
            } catch (Exception e) {
                Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK INSTANCE UPDATE", ex);
            }
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR UPDATING INSTANCE FOR USERNAME " + oldInstance.getUsername() + " AND DB_NAME " + oldInstance.getDbName(), ex);
        } finally {
            try {
                updateStatement.close();
            } catch (Exception e) {}
            try {
                insertStatement.close();
            } catch (Exception e) {}
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return updateResult;
    }
    
    /**
     * Swaps master and slave in the database.
     * @param newMaster new master.
     * @param newSlave new slave.
     * @return number of updated instances if the operation was successful, 0 otherwise.
     */
    public int swapMasterSlave(String newMaster, String newSlave) {
        Connection connection = null;
        PreparedStatement statement = null;
        int result = 0;
        try {
            if (newMaster != null && newSlave != null) {
                //Get connection
                connection = getConnection();
                connection.setAutoCommit(false);

                //Prepare query for the prepared statement (to avoid SQL injection)
                String query = "UPDATE dod_instances SET master = ?, slave = ? WHERE db_name = ?";
                statement = connection.prepareStatement(query);
                
                //Add master to batch
                statement.setString(1, null);
                statement.setString(2, newSlave);
                statement.setString(3, newMaster);
                statement.addBatch();
                
                //Add slave to batch
                statement.setString(1, newMaster);
                statement.setString(2, null);
                statement.setString(3, newSlave);
                statement.addBatch();
                
                int[] results = statement.executeBatch();
                result = results.length;
                for (int i=0; i<results.length; i++){
                    if (results[i] == PreparedStatement.EXECUTE_FAILED) {
                        result = 0;
                        connection.rollback();                        
                        break;
                    }
                }
                connection.commit();
            }

        } catch (NamingException ex) {
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SWAPPING MASTER/SLAVE", ex);
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (Exception e) {
                Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK MASTER/SLAVE SWAP", ex);
            }
            Logger.getLogger(DODInstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SWAPPING MASTER/SLAVE", ex);
        } finally {
            try {
                statement.close();
            } catch (Exception e) {}
            try {
                connection.setAutoCommit(true);
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return result;
    }
    
    /**
     * Obtains the list of changes to attributes of an instance.
     * @param instance DOD instance to get the changes from.
     * @return list of changes for the specified instance.
     */
    public List<DODInstanceChange> selectInstanceChanges(DODInstance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODInstanceChange> changes = new ArrayList<DODInstanceChange>();
        try {
            //Get connection
            connection = getConnection();
            //Prepare query for the prepared statement (to avoid SQL injection)
            String query = "SELECT username, db_name, attribute, change_date, requester, old_value, new_value"
                            + " FROM dod_instance_changes WHERE username = ? AND db_name = ? ORDER BY change_date DESC";
            statement = connection.prepareStatement(query);
            //Assign values to variables
            statement.setString(1, instance.getUsername());
            statement.setString(2, instance.getDbName());
            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                DODInstanceChange change = new DODInstanceChange();
                change.setUsername(result.getString(1));
                change.setDbName(result.getString(2));
                change.setAttribute(result.getString(3));
                change.setChangeDate(new java.util.Date(result.getTimestamp(4).getTime()));
                change.setRequester(result.getString(5));
                change.setOldValue(result.getString(6));
                change.setNewValue(result.getString(7));
                changes.add(change);
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING CHANGES FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODJobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING CHANGES FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
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
        return changes;
    }
}
