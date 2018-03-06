/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */
 
package ch.cern.dbod.db.dao;

import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.util.CommonConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DAO for upgrade objects.
 * @author Daniel Gomez Blanco
 */
public class UpgradeDAO {
    
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
     * Selects all the upgrades available in the database.
     * @return List of all the upgrades in the database.
     */
    public Map<String, Upgrade> selectAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        HashMap<String, Upgrade> upgrades = new HashMap<>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT db_type, category, version_from, version_to"
                            + " FROM dod_upgrades ORDER BY db_type, version_from, version_to");
            statement = connection.prepareStatement(query.toString());

            //Execute query
            result = statement.executeQuery();

            //Instantiate upgrade objects
            while (result.next()) {
                Upgrade upgrade = new Upgrade();
                upgrade.setDbType(result.getString(1));
                upgrade.setCategory(result.getString(2));
                upgrade.setVersionFrom(result.getString(3));
                upgrade.setVersionTo(result.getString(4));
                String key = upgrade.getDbType() + "$" + upgrade.getCategory() + "$" + upgrade.getVersionFrom();
                upgrades.put(key, upgrade);
            }
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(UpgradeDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING INSTANCES FOR ADMIN",ex);
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
        return upgrades;
    }
    
    /**
     * Inserts a new upgrade in the database. An update is tried first, in case
     * there is already an upgrade for a version. This is done this way because
     * upgrades can only be from one version to the next, and we will always
     * force people to update to the next version.
     * 
     * Let's say there is an upgrade for MySQL from 5.5.15 to 5.5.17, and an
     * upgrade from 5.5.17 to 5.5.30. The usual flow will be to upgrade to
     * 5.5.17 and 5.5.30 later on. But we might provide a script to upgrade
     * from 5.5.15 to 5.5.30. Then the owner will have to upgrade to 5.5.30.
     * @param upgrade upgrade object
     * @return true if the operation was successful, false otherwise.
     */
    public boolean insert(Upgrade upgrade) {
        Connection connection = null;
        PreparedStatement insertStatement = null;
        PreparedStatement updateStatement = null;
        int result = 0;
        try {
            //Get connection
            connection = getConnection();
            
            //Try update statement first (if a new upgrade is available for a given version it has to be updated in the version_to field)
            //Prepare query for the prepared statement (to avoid SQL injection)
            String updateQuery = "UPDATE dod_upgrades SET version_to = ? WHERE db_type = ? AND category = ? AND version_from = ?";
            updateStatement = connection.prepareStatement(updateQuery);
            //Assign values to variables
            updateStatement.setString(1, upgrade.getVersionTo());
            updateStatement.setString(2, upgrade.getDbType());
            updateStatement.setString(3, upgrade.getCategory());
            updateStatement.setString(4, upgrade.getVersionFrom());
            
            //Execute query
            result = updateStatement.executeUpdate();
            
            //If nothing was updated
            if (result == 0) {
                //Prepare query for the prepared statement (to avoid SQL injection)
                String insertQuery = "INSERT INTO dod_upgrades (db_type, category, version_from, version_to)"
                                + " VALUES (?, ?, ?, ?)";
                insertStatement = connection.prepareStatement(insertQuery);
                //Assign values to variables
                insertStatement.setString(1, upgrade.getDbType());
                insertStatement.setString(2, upgrade.getCategory());
                insertStatement.setString(3, upgrade.getVersionFrom());
                insertStatement.setString(4, upgrade.getVersionTo());
                //Execute query
                result = insertStatement.executeUpdate();
            }
            
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(UpgradeDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING UPGRADE FOR TYPE " + upgrade.getDbType() + " AND CATEGORY " + upgrade.getCategory(), ex);
        } finally {
            try {
                updateStatement.close();
            } catch (Exception e) {
            }
            try {
                insertStatement.close();
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        if (result > 0)
            return true;
        else
            return false;
    }
    
    /**
     * Deletes an upgrade from the table of upgrades.
     * @param upgrade upgrade to delete
     * @return true if it was deleted, false otherwise
     */
    public boolean delete(Upgrade upgrade) {
        Connection connection = null;
        PreparedStatement deleteStatement = null;
        int result = 0;
        try {
            //Get connection
            connection = getConnection();
            
            //Try update statement first (if a new upgrade is available for a given version it has to be updated in the version_to field)
            //Prepare query for the prepared statement (to avoid SQL injection)
            String deleteQuery = "DELETE FROM dod_upgrades WHERE db_type = ? AND category = ? AND version_from = ? AND version_to = ?";
            deleteStatement = connection.prepareStatement(deleteQuery);
            //Assign values to variables
            deleteStatement.setString(1, upgrade.getDbType());
            deleteStatement.setString(2, upgrade.getCategory());
            deleteStatement.setString(3, upgrade.getVersionFrom());
            deleteStatement.setString(4, upgrade.getVersionTo());
            //Execute query
            result = deleteStatement.executeUpdate();
            
        } catch (NamingException | SQLException ex) {
            Logger.getLogger(UpgradeDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING UPGRADE FOR TYPE " + upgrade.getDbType() + " AND CATEGORY " + upgrade.getCategory(), ex);
        } finally {
            try {
                deleteStatement.close();
            } catch (Exception e) {
            }
            try {
                connection.close();
            } catch (Exception e) {
            }
        }
        if (result > 0)
            return true;
        else
            return false;
    } 
}
