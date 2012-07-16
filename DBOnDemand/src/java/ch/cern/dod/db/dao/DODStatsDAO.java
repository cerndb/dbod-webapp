package ch.cern.dod.db.dao;

import ch.cern.dod.db.entity.DODCommandStat;
import ch.cern.dod.db.entity.DODJobStat;
import ch.cern.dod.util.DODConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DAO for stat objects.
 * @author Daniel Gomez Blanco
 */
public class DODStatsDAO {
    
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
     * Selects the command stats available in the database.
     * @return List of command stats.
     */
    public List<DODCommandStat> selectCommandStats() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODCommandStat> commandStats = new ArrayList<DODCommandStat>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT command_name, count, mean_duration"
                            + " FROM command_stats ORDER BY command_name");
            statement = connection.prepareStatement(query.toString());

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                DODCommandStat commandStat = new DODCommandStat();
                commandStat.setCommandName(result.getString(1));
                commandStat.setCount(result.getInt(2));
                commandStat.setMeanDuration(result.getFloat(3));
                commandStats.add(commandStat);
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODStatsDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING COMMAND STATS",ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODStatsDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING COMMAND STATS",ex);
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
        return commandStats;
    }
    
    /**
     * Selects the job stats available in the database.
     * @return List of job stats.
     */
    public List<DODJobStat> selectJobStats() {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODJobStat> jobStats = new ArrayList<DODJobStat>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            query.append("SELECT db_name, command_name, count, mean_duration"
                            + " FROM job_stats ORDER BY db_name, command_name");
            statement = connection.prepareStatement(query.toString());

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                DODJobStat jobStat = new DODJobStat();
                jobStat.setDbName(result.getString(1));
                jobStat.setCommandName(result.getString(2));
                jobStat.setCount(result.getInt(3));
                jobStat.setMeanDuration(result.getFloat(4));
                jobStats.add(jobStat);
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODStatsDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING JOB STATS",ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODStatsDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING JOB STATS",ex);
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
        return jobStats;
    }
}
