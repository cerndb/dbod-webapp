package ch.cern.dod.db.dao;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODMetric;
import ch.cern.dod.util.DODConstants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DAO for monitoring.
 * @author Daniel Gomez Blanco
 */
public class DODMonitoringDAO {
    
    /**
     * Obtains a new connection from the pool.
     * @return a connection to the database.
     * @throws NamingException if the context cannot be found.
     * @throws SQLException if the datasource cannot be created.
     */
    private Connection getConnection() throws NamingException, SQLException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup(DODConstants.ENVIRONMENT_CONTEXT);
        DataSource dataSource = (DataSource) envContext.lookup(DODConstants.DATA_SOURCE_MONITORING);
        return dataSource.getConnection();
    }
    
    /**
     * Selects the available metrics for an instance.
     * @param instance instance to obtain metrics from
     * @return List of metrics.
     */
    public List<DODMetric> selectAvailableMetrics(DODInstance instance) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        ArrayList<DODMetric> metrics = new ArrayList<DODMetric>();
        try {
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            if (instance.getDbType().equals(DODConstants.DB_TYPE_MYSQL)) {
                query.append("SELECT target_type, parameter_code, parameter_name, unit"
                                + " FROM pdb_monitoring.target_params_defs"
                                + " WHERE target_type = ? OR target_type = ?"
                                + " ORDER BY target_type, parameter_name");
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, DODConstants.MONITORING_TYPE_MYSQL);
                statement.setString(2, DODConstants.MONITORING_TYPE_NODE);
            }
            else if (instance.getDbType().equals(DODConstants.DB_TYPE_ORACLE)) {
                query.append("SELECT ?, metric_id, metric_name, metric_unit"
                                + " FROM pdb_monitoring.rmon_metrics"
                                + " ORDER BY metric_name");
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, DODConstants.MONITORING_TYPE_ORACLE);
            }

            //Execute query
            result = statement.executeQuery();

            //Instantiate instance objects
            while (result.next()) {
                DODMetric metric = new DODMetric();
                metric.setType(result.getString(1));
                metric.setCode(result.getString(2));
                metric.setName(result.getString(3));
                metric.setUnit(result.getString(4));
                metrics.add(metric);
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODMonitoringDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING AVAILABLE METRICS",ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODMonitoringDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING AVAILABLE METRICS",ex);
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
        return metrics;
    }
    
    /**
     * Selects the data for a specific metric.
     * @param instance instance to obtain metric from
     * @param metric metric to obtain
     * @param days number of days for the metric
     * @return JSON string with the DataTable representation of the metric.
     */
    public String selectJSONData(DODInstance instance, String host, DODMetric metric, int days) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        StringBuilder json = new StringBuilder();
        try {
            //Adjust beginning
            Date now = new Date();
            Timestamp start = new Timestamp(now.getTime() - (days * 86400000));
            //Get connection
            connection = getConnection();

            //Prepare query for the prepared statement (to avoid SQL injection)
            StringBuilder query = new StringBuilder();
            //If the metric is MySQL
            if (metric.getType().equals(DODConstants.MONITORING_TYPE_MYSQL)) {
                query.append("SELECT p.valid_from, p.valid_to, p.value"
                                + " FROM pdb_monitoring.targets t, pdb_monitoring.target_params p"
                                + " WHERE t.target_name = ?"
                                + " AND p.target_id = t.target_id"
                                + " AND p.target_type = ?"
                                + " AND p.parameter_code= ?"
                                + " AND (p.valid_to >= ? OR p.valid_to is NULL)"
                                + " ORDER BY p.valid_from");
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
                statement.setString(2, DODConstants.MONITORING_TYPE_MYSQL);
                statement.setString(3, metric.getCode());
                statement.setTimestamp(4, start);
            }
            else if (metric.getType().equals(DODConstants.MONITORING_TYPE_NODE)) {
                query.append("SELECT p.valid_from, p.valid_to, p.value"
                                + " FROM pdb_monitoring.targets t, pdb_monitoring.target_params p"
                                + " WHERE t.target_name = ?"
                                + " AND p.target_id = t.target_id"
                                + " AND p.target_type = ?"
                                + " AND p.parameter_code= ?"
                                + " AND (p.valid_to >= ? OR p.valid_to IS NULL)"
                                + " ORDER BY p.valid_from");
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, host);
                statement.setString(2, DODConstants.MONITORING_TYPE_NODE);
                statement.setString(3, metric.getCode());
                statement.setTimestamp(4, start);
            }
            else if (metric.getType().equals(DODConstants.MONITORING_TYPE_ORACLE)) {
                query.append("SELECT begin_time, end_time, average"
                                + " FROM pdb_monitoring.rmon_data"
                                + " WHERE cluster_name = ?"
                                + " AND metric_id = ?"
                                + " AND (end_time >= ? OR end_time IS NULL)"
                                + " ORDER BY begin_time");
                statement = connection.prepareStatement(query.toString());
                statement.setString(1, instance.getDbName().toUpperCase());
                statement.setString(2, metric.getCode());
                statement.setTimestamp(3, start);
            }

            //Execute query
            result = statement.executeQuery();

            //Build JSON object
            if (result.next()) {
                //If there is no end date use now
                Timestamp end = result.getTimestamp(2);
                if (end == null) {
                    end = new Timestamp(now.getTime());
                }
                
                //Store value to build delta
                Float value1 = Float.parseFloat(result.getString(3));
                
                //Initialise object
                json.append("{cols: [{id: 'date', label: 'Date', type: 'datetime'}, {id: 'value', label: 'Cumulative', type: 'number'}, {id: 'value', label: 'Delta', type: 'number'}], rows: [");
                
                //Start date (with delta 0)
                json.append("{c: [{v: new Date(");
                json.append(start.getTime());
                json.append(")}, {v: ");
                json.append(value1);
                json.append("}, {v: 0}]}");
                
                //End date (no delta)
                json.append(", {c: [{v: new Date(");
                json.append(end.getTime());
                json.append(")}, {v: ");
                json.append(value1);
                json.append("}]}");
                
                //If the next point is after more than 7 minutes add delta 0 (not for Oracle)
                if (!instance.getDbType().equals(DODConstants.DB_TYPE_ORACLE)
                        && start.getTime() + 420000 < end.getTime()) {
                    json.append(", {c: [{v: new Date(");
                    json.append(end.getTime() - 360000);
                    json.append(")}, {}, {v:0}]}");
                }

                //Fetch rest of rows
                while (result.next()) {
                    //If there is no end date use now
                    end = result.getTimestamp(2);
                    if (end == null) {
                        end = new Timestamp(now.getTime());
                    }
                    
                    //Store new value to build delta
                    Float value2 = Float.parseFloat(result.getString(3));
                    
                    //Start date
                    json.append(", {c: [{v: new Date(");
                    json.append(result.getTimestamp(1).getTime());
                    json.append(")}, {v: ");
                    json.append(value2);
                    json.append("}, {v: ");
                    json.append(value2 - value1);
                    json.append("}]}");
                    
                    //End date (no delta)
                    json.append(", {c: [{v: new Date(");
                    json.append(end.getTime());
                    json.append(")}, {v: ");
                    json.append(value2);
                    json.append("}]}");
                    
                    //If the next point is after more than 7 minutes add delta 0
                    if (!instance.getDbType().equals(DODConstants.DB_TYPE_ORACLE)
                            && result.getTimestamp(1).getTime() + 420000 < end.getTime()) {
                        json.append(", {c: [{v: new Date(");
                        json.append(result.getTimestamp(1).getTime() + 360000);
                        json.append(")}, {}, {v:0}]}");
                        json.append(", {c: [{v: new Date(");
                        json.append(end.getTime() - 360000);
                        json.append(")}, {}, {v:0}]}");
                    }
                    
                    //Resest first value
                    value1 = value2;
                }
                
                //Add delta 0 for the end point
                json.append(", {c: [{v: new Date(");
                json.append(end.getTime());
                json.append(")}, {}, {v: 0}]}");
                
                //Close object
                json.append("]}");
            }
        } catch (NamingException ex) {
            Logger.getLogger(DODMonitoringDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING METRIC DATA",ex);
        } catch (SQLException ex) {
            Logger.getLogger(DODMonitoringDAO.class.getName()).log(Level.SEVERE, "ERROR SELECTING METRIC DATA",ex);
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
        if (json.length() > 0)
            return json.toString();
        else
            return "null";
    }
}