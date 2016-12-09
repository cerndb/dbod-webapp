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
import ch.cern.dbod.util.CommonConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
public class ActivityDAO {

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
     * Inserts a new user activity in the database.
     * @param user Name of the user who executes the action.
     * @param instance Related instance (if any).
     * @param page Name of the view (if any).
     * @param log Action executed.
     */
    public void insert(String user, Instance instance, String page, String log) {
        Connection connection = null;
        PreparedStatement activityStatement = null;
        PreparedStatement masterStatement = null;

        try {
            //Get connection
            connection = getConnection();
            //Set autocommit to false to execute multiple queries and rollback in case something goes wrong
            connection.setAutoCommit(false);
            
            //Prepare query for the prepared statement (to avoid SQL injection)
            String activityQuery = "INSERT INTO activity_log (username, instance, page, log)"
                            + " VALUES (?, ?, ?, ?)";
            activityStatement = connection.prepareStatement(activityQuery);
            //Assign values to variables
            activityStatement.setString(1, user);
            activityStatement.setString(2, instance == null ? null : instance.getDbName());
            activityStatement.setString(3, page);
            activityStatement.setString(4, log);
            
            //Execute query
            activityStatement.executeUpdate();
            
            //Commit queries
            connection.commit();
            
        } catch (Exception ex) {
            Logger.getLogger(ActivityDAO.class.getName()).log(Level.SEVERE, "ERROR INSERTING ACTIVITY LOG FOR USERNAME " + user + " AND INSTANCE " + instance, ex);
        } finally {
            try {
                activityStatement.close();
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
    }
}
