/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.util;

import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.ws.DBODWebService;
import ch.cern.dbod.ws.DBODWebServicePortType;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;

/**
 * Helper to manage snapshots using web services. This uses the SOAP web services
 * implemented in the syscontrol project
 * @author Daniel Gomez Blanco
 */
public class FileHelper {

    /**
     * Username to connect to web services.
     */
    private String wsUser;
    /**
     * Password to connect to web services.
     */
    private String wsPassword;

    /**
     * Constructor for this class.
     * @param user username to connect to web services
     * @param password password to connect to web services
     */
    public FileHelper(String user, String password) {
        this.wsUser = user;
        this.wsPassword = password;
    }

    /**
     * Gets a configuration file from a specific MySQL instance.
     * @param instance instance to get the snapshots of.
     * @return configuration file.
     */
    public AMedia getMySQLConfigFile(Instance instance) {
        AMedia file = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            String content = port.getMySQLConfigFile(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            if (content != null) {
                file = new AMedia(Labels.getLabel(CommonConstants.LABEL_CONFIG + CommonConstants.CONFIG_FILE_MY_CNF), null, "text/plain", content);
            }
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING FILE ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return file;
    }
    
    /**
     * Gets a configuration file from a specific PostgreSQL instance.
     * @param instance instance to get the snapshots of.
     * @param type type of config file to get.
     * @return configuration file.
     */
    public AMedia getPGConfigFile(Instance instance, String type) {
        AMedia file = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            String content = port.getPGConfigFile(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), type);
            if (content != null) {
                file = new AMedia(Labels.getLabel(CommonConstants.LABEL_CONFIG + type), null, "text/plain", content);
            }
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING FILE ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return file;
    }
    
    /**
     * Gets the slow logs for a specific MySQL instance.
     * @param instance instance to get the slow logs of.
     * @return array of file names corresponding to the slow logs.
     */
    public String[] getSlowLogs(Instance instance) {
        String[] slowLogs = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            String slowLogsString = port.getSlowLogs(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            if (slowLogsString != null && !slowLogsString.isEmpty()) {
                slowLogs = slowLogsString.split(":");
            }
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING SLOW LOGS ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return slowLogs;
    }
    
    /**
     * Gets the logs for a specific Oracle instance.
     * @param instance instance to get the logs of.
     * @return array of file names corresponding to the log files.
     */
    public String[] getOracleLogs(Instance instance) {
        String[] logs = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            String logsString = port.getOracleLogs(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            if (logsString != null && !logsString.isEmpty()) {
                logs = logsString.split(":");
            }
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING LOGS ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return logs;
    }
    
    /**
     * Gets the logs for a specific Oracle 12c instance.
     * @param instance instance to get the logs of.
     * @return array of file names corresponding to the log files.
     */
    public String[] getOraLogs(Instance instance) {
        String[] logs = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            String logsString = port.getOraLogs(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            if (logsString != null && !logsString.isEmpty()) {
                logs = logsString.split(":");
            }
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING LOGS ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return logs;
    }
    
    /**
     * Gets the logs for a specific PostgreSQL instance.
     * @param instance instance to get the logs of.
     * @return array of file names corresponding to the log files.
     */
    public String[] getPGLogs(Instance instance) {
        String[] logs = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            String logsString = port.getPGLogs(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            if (logsString != null && !logsString.isEmpty()) {
                logs = logsString.split(":");
            }
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING LOGS ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return logs;
    }
    
    /**
     * Gets a URL to a served file from a specific instance.
     * @param instance instance to get the snapshots of.
     * @param filePath path of the file to get.
     * @return URL to a server.
     */
    public String getServedFileURL(Instance instance, String filePath) {
        String url = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            url = port.serveFile(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), filePath);
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR SERVING FILE ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return url;
    }
    
    /**
     * Connects to a URL to download a file specified by filePath on the given instance.
     * This method is created to connect to the DB hosts and get a specific file,
     * and then serve it to the user, instead of providing the use the download
     * address (less security and can't be accessed outside CERN)
     * @param urlString URL to download file from.
     * @param filePath file path used to name the file.
     * @param instance instance where this file resides.
     * @return a file to be downloaded by the user.
     */
    public AMedia getHTTPFile(String urlString, String filePath, Instance instance) {
        AMedia file = null;
        try {
           URL url = new URL(urlString);
           file = new AMedia(filePath.substring(filePath.lastIndexOf('/') + 1), null, "application/octet-stream", url, null);
        } catch (MalformedURLException | FileNotFoundException ex) {
           Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR SERVING FILE ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return file;
    }
}
