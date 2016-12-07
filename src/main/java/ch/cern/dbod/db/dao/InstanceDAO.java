/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.dao;

import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.InstanceChange;
import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.RestHelper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DAO for Instance entity.
 * @author Daniel Gomez Blanco
 * @author Jose Andres Cordero Benitez
 */
public class InstanceDAO {

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
     * Selects all the instances in the database. It takes the list of available
     * upgrades in order to decide if an instance needs upgrading or not.
     * @param upgrades list of available upgrades.
     * @return List of all the instances in the database.
     */
    public List<Instance> selectAll(List<Upgrade> upgrades) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<Instance> instances = new ArrayList<>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, host"
                            + " FROM dod_instances WHERE status = '1'"
                            + " ORDER BY db_name");
            statement = connection.prepareStatement(query.toString());

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                Instance instance = new Instance();
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
                instance.setHost(result.getString(17));
            //Check if instance needs upgrade
            if (upgrades != null) {
                    for (int i=0; i < upgrades.size(); i++) {
                        Upgrade upgrade = upgrades.get(i);
                        if (upgrade.getDbType().equals(instance.getDbType()) && upgrade.getCategory().equals(instance.getCategory())
                            && upgrade.getVersionFrom().equals(instance.getVersion()))
                            instance.setUpgradeTo(upgrade.getVersionTo());
                    }
                }
                instances.add(instance);
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR ADMIN",ex);
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
     * Selects all the instances to be destroyed. These are the instances with a
     * status of 0.
     * @return List of all the instances to be destroyed.
     */
    public List<Instance> selectToDestroy() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<Instance> instances = new ArrayList<>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, host"
                            + " FROM dod_instances WHERE status = '0'"
                            + " ORDER BY db_name");
            statement = connection.prepareStatement(query.toString());

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                Instance instance = new Instance();
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
                instance.setHost(result.getString(17));
                instances.add(instance);
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES TO DESTROY",ex);
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
     * It takes the list of available upgrades in order to decide if an instance needs upgrading or not.
     * @param username username to obtain instances.
     * @param egroups e-groups that the user belongs to.
     * @param upgrades upgrades available.
     * @return List of instances belonging to a username or to any of their e-groups.
     */
    public List<Instance> selectByUserNameAndEGroups(String username, String egroups, List<Upgrade> upgrades) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<Instance> instances = new ArrayList<>();
        try {
            //Get connection
            connection = getConnection();
            
            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, host"
                            + " FROM dod_instances WHERE status = '1' ");
            //Append egroups (if any)
            StringTokenizer tokenizer = new StringTokenizer("");
            if (egroups != null && !egroups.isEmpty()) {
                query.append("AND (username = ? ");
                tokenizer = new StringTokenizer(egroups.toLowerCase(), ";");
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
                Instance instance = new Instance();
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
                instance.setHost(result.getString(17));
                //Check if instance needs upgrade
                if (upgrades != null) {
                    for (int j=0; j < upgrades.size(); j++) {
                        Upgrade upgrade = upgrades.get(j);
                        if (upgrade.getDbType().equals(instance.getDbType()) && upgrade.getCategory().equals(instance.getCategory())
                                && upgrade.getVersionFrom().equals(instance.getVersion()))
                            instance.setUpgradeTo(upgrade.getVersionTo());
                    }
                }
                instances.add(instance);
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR USERNAME " + username ,ex);
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
     * Selects the instances in the database for the specified host. It takes
     * the list of available upgrades in order to decide if an instance needs upgrading or not.
     * @param host name of the host.
     * @param upgrades upgrades available.
     * @return List of the instances in the host.
     */
    public List<Instance> selectInstancesPerHost(String host, List<Upgrade> upgrades) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<Instance> instances = new ArrayList<>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, host"
                            + " FROM dod_instances WHERE status = '1' AND host = ?"
                            + " ORDER BY db_name");
            statement = connection.prepareStatement(query.toString());
            
            //Assign values to variables
            statement.setString(1, host);

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                Instance instance = new Instance();
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
                instance.setHost(result.getString(17));
                //Check if instance needs upgrade
                if (upgrades != null) {
                    for (int i=0; i < upgrades.size(); i++) {
                        Upgrade upgrade = upgrades.get(i);
                        if (upgrade.getDbType().equals(instance.getDbType()) && upgrade.getCategory().equals(instance.getCategory())
                                && upgrade.getVersionFrom().equals(instance.getVersion()))
                            instance.setUpgradeTo(upgrade.getVersionTo());
                    }
                }
               instances.add(instance);
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR ADMIN",ex);
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
     * Select a specific instance by its DB name. It takes the list of available
     * upgrades in order to decide if an instance needs upgrading or not.
     * @param dbName DB name of the instance.
     * @param upgrades upgrades available.
     * @return instance for the username and DB name specified.
     */
    public Instance selectByDbName(String dbName, List<Upgrade> upgrades) {
        Instance instance = null;
        try {
            instance = RestHelper.getObjectFromRestApi("api/v1/instance/" + dbName, Instance.class, "response");
            System.out.println("INSTANCE = " + instance);
            
            //Check if instance needs upgrade
            if (instance != null && upgrades != null) {
                for (int i=0; i < upgrades.size(); i++) {
                    Upgrade upgrade = upgrades.get(i);
                    if (upgrade.getDbType().equals(instance.getDbType()) && upgrade.getCategory().equals(instance.getCategory())
                            && upgrade.getVersionFrom().equals(instance.getVersion()))
                        instance.setUpgradeTo(upgrade.getVersionTo());
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCE FOR DB NAME " + dbName ,ex);
        }
        return instance;
    }

    /**
     * Inserts a new instance in the database. It also updates the slave attribute
     * in the master in case this instance has a master.
     * @param instance instance to be inserted.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int insert(Instance instance) {
        Connection connection = null;
        PreparedStatement instanceStatement = null;
        PreparedStatement masterStatement = null;
        int instanceResult;
        int masterResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Set autocommit to false to execute multiple queries and rollback in case something goes wrong
            connection.setAutoCommit(false);
            
            //Prepare query for the prepared statement (to avoid SQL injection)
            String instanceQuery = "INSERT INTO dod_instances (username, db_name, e_group, category, creation_date, expiry_date, db_type, db_size, no_connections, project, description, version, state, status, master, slave, host)"
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
            instanceStatement.setString(17, instance.getHost());
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
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } catch (SQLException ex) {
            try {
                //Rollback updates
                connection.rollback();
            }
            catch (SQLException ex1) {
                Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK INSERTING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex1);
            }
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
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
     * Deletes an instance from the database.
     * @param instance instance to be deleted.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int delete(Instance instance) {
        Connection connection = null;
        PreparedStatement deleteStatement = null;
        int deleteResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Create statement
            String deleteQuery = "DELETE FROM dod_instances WHERE db_name = ? AND username = ?";
            deleteStatement = connection.prepareStatement(deleteQuery);
            //Set values
            deleteStatement.setString(1, instance.getDbName());
            deleteStatement.setString(2, instance.getUsername());

            deleteResult = deleteStatement.executeUpdate();
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR DELETING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } finally {
            try {
                deleteStatement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return deleteResult;
    }
    
    /**
     * Updates an instance with new values.
     * @param oldInstance old values of the instance.
     * @param newInstance new values of the instance.
     * @param requester username requesting this, in order to log it.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int update(Instance oldInstance, Instance newInstance, String requester) {
        Connection connection = null;
        PreparedStatement updateStatement = null;
        PreparedStatement insertStatement = null;
        int updateResult = 0;
        try {
            //Get connection
            connection = getConnection();
            connection.setAutoCommit(false);

            //Prepare query for the prepared statement (to avoid SQL injection)
            String updateQuery = "UPDATE dod_instances SET e_group = ?, expiry_date = ?, project = ?, description = ?, category = ?, no_connections = ?, db_size = ?, state = ?, version = ?, master = ?, slave = ?, host = ? WHERE username = ? AND db_name = ?";
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
            updateStatement.setString(12, newInstance.getHost());
            updateStatement.setString(13, newInstance.getUsername());
            updateStatement.setString(14, newInstance.getDbName());
            //Execute query
            updateResult = updateStatement.executeUpdate();
            
            //If update was successful log the change
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
                    DateFormat dateFormatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
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
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR UPDATING INSTANCE FOR USERNAME " + oldInstance.getUsername() + " AND DB_NAME " + oldInstance.getDbName(), ex);
        } catch (SQLException ex) {
            updateResult = 0;
            try {
                connection.rollback();
            } catch (Exception e) {
                Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK INSTANCE UPDATE", ex);
            }
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR UPDATING INSTANCE FOR USERNAME " + oldInstance.getUsername() + " AND DB_NAME " + oldInstance.getDbName(), ex);
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
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SWAPPING MASTER/SLAVE", ex);
        } catch (SQLException ex) {
            try {
                connection.rollback();
            } catch (Exception e) {
                Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR ROLLING BACK MASTER/SLAVE SWAP", ex);
            }
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SWAPPING MASTER/SLAVE", ex);
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
     * @param instance DBOD instance to get the changes from.
     * @return list of changes for the specified instance.
     */
    public List<InstanceChange> selectInstanceChanges(Instance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<InstanceChange> changes = new ArrayList<>();
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
                InstanceChange change = new InstanceChange();
                change.setUsername(result.getString(1));
                change.setDbName(result.getString(2));
                change.setAttribute(result.getString(3));
                change.setChangeDate(new java.util.Date(result.getTimestamp(4).getTime()));
                change.setRequester(result.getString(5));
                change.setOldValue(result.getString(6));
                change.setNewValue(result.getString(7));
                changes.add(change);
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(JobDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING CHANGES FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
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
    
    /**
     * Rescues an instance and enables it back again. It changes the status from
     * 0 to 1.
     * @param instance instance to be deleted.
     * @return 1 if the operation was successful, 0 otherwise.
     */
    public int rescue(Instance instance) {
        Connection connection = null;
        PreparedStatement rescueStatement = null;
        int rescueResult = 0;
        try {
            //Get connection
            connection = getConnection();
            //Create statement
            String rescueQuery = "UPDATE dod_instances SET status = '1' WHERE db_name = ? AND username = ?";
            rescueStatement = connection.prepareStatement(rescueQuery);
            //Set values
            rescueStatement.setString(1, instance.getDbName());
            rescueStatement.setString(2, instance.getUsername());

            rescueResult = rescueStatement.executeUpdate();
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR RESCUING INSTANCE FOR USERNAME " + instance.getUsername() + " AND DB_NAME " + instance.getDbName(), ex);
        } finally {
            try {
                rescueStatement.close();
            } catch (Exception e) {}
            try {
                connection.close();
            } catch (Exception e) {}
        }
        return rescueResult;
    }
    
    /**
     * Checks if an instance is on FIM.
     * @param instance instance to check.
     * @return true if the instance is on FIM, false otherwise.
     */
    public boolean isInstanceOnFIM(Instance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT *"
                            + " FROM fim_ora_ma.dod_fim_objects WHERE db_name = ?");
            statement = connection.prepareStatement(query.toString());

            //Assign values to variables
            statement.setString(1, instance.getDbName());

            //Execute query
            result = statement.executeQuery();

            //If there is no result return false
            if (!result.next()) {
                return false;
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(InstanceDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING FIM OBJECT FOR DB NAME " + instance.getDbName() ,ex);
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
        
        //Return true in any other case
        //This is required, because an instance cannot be deleted if it is still
        //in FIM. This is just to check that there were no errors and the instance
        //is really not in FIM when deleting it permantenly.
        return true;
    }
}
