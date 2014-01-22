package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ws.DODWebService;
import ch.cern.dod.ws.DODWebServicePortType;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;

/**
 * Helper to manage snapshots using web services.
 * @author Daniel Gomez Blanco
 * @version 22/11/2011
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
     * Gets a configuration file from a specific instance.
     * @param instance instance to get the snapshots of.
     * @return configuration file.
     */
    public AMedia getMySQLConfigFile(DODInstance instance) {
        AMedia file = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            String content = port.getMySQLConfigFile(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            if (content != null) {
                file = new AMedia(Labels.getLabel(DODConstants.LABEL_CONFIG + DODConstants.CONFIG_FILE_MY_CNF), null, "text/plain", content);
            }
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING FILE ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return file;
    }
    
    /**
     * Gets a configuration file from a specific instance.
     * @param instance instance to get the snapshots of.
     * @param type type of config file to get.
     * @return configuration file.
     */
    public AMedia getPGConfigFile(DODInstance instance, String type) {
        AMedia file = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            String content = port.getPGConfigFile(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), type);
            if (content != null) {
                file = new AMedia(Labels.getLabel(DODConstants.LABEL_CONFIG + type), null, "text/plain", content);
            }
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING FILE ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return file;
    }
    
    /**
     * Gets the slow logs for a specific instance.
     * @param instance instance to get the slow logs of.
     * @return array of file names corresponding to the slow logs.
     */
    public String[] getSlowLogs(DODInstance instance) {
        String[] slowLogs = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            String slowLogsString = port.getSlowLogs(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
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
    public String[] getOracleLogs(DODInstance instance) {
        String[] logs = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            String logsString = port.getOracleLogs(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
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
    public String[] getOraLogs(DODInstance instance) {
        String[] logs = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            String logsString = port.getOraLogs(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
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
    public String[] getPGLogs(DODInstance instance) {
        String[] logs = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            String logsString = port.getPGLogs(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
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
     * @param file path of the file to get.
     * @return URL to a server.
     */
    public String getServedFileURL(DODInstance instance, String filePath) {
        String url = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            url = port.serveFile(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), filePath);
        } catch (Exception ex) {
            Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR SERVING FILE ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return url;
    }
    
    /**
     * Connects to a URL to download a file specified by filePath on the given instance.
     * @param urlString URL to download file from.
     * @param filePath file path used to name the file.
     * @param instance instance where this file resides.
     * @return a file to be downloaded by the user.
     */
    public AMedia getHTTPFile(String urlString, String filePath, DODInstance instance) {
        AMedia file = null;
        try {
           URL url = new URL(urlString);
           file = new AMedia(filePath.substring(filePath.lastIndexOf("/") + 1), null, "text/plain", url, "UTF-8");
        } catch (MalformedURLException | FileNotFoundException ex) {
           Logger.getLogger(FileHelper.class.getName()).log(Level.SEVERE, "ERROR SERVING FILE ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return file;
    }
}
