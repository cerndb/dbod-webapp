package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ws.DODWebServiceLocator;
import ch.cern.dod.ws.DODWebServiceSoapBindingStub;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;
import org.apache.axis.types.URI;
import org.zkoss.util.media.AMedia;

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
     * @param file path of the file to get.
     * @return configuration file.
     */
    public AMedia getFile(DODInstance instance, String filePath) {
        AMedia file = null;
        try {
            DODWebServiceLocator locator = new DODWebServiceLocator();
            DODWebServiceSoapBindingStub stub = (DODWebServiceSoapBindingStub) locator.getDODWebServicePort();
            stub.setUsername(wsUser);
            stub.setPassword(wsPassword);
            String content = stub.getFile(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), filePath);
            if (content != null)
                file = new AMedia(filePath.substring(filePath.lastIndexOf("/") + 1), null, "text/plain", content);
        } catch (RemoteException ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (ServiceException ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return file;
    }
    
    /**
     * Gets the slow logs for a specific instance.
     * @param instance instance to get the slow logs of.
     * @return array of file names corresponding to the slowlong.
     * @throws ServiceException if there is an error executing the web service.
     * @throws RemoteException if there is an error connection to the server.
     */
    public String[] getSlowLogs(DODInstance instance) {
        String[] slowLogs = null;
        try {
            DODWebServiceLocator locator = new DODWebServiceLocator();
            DODWebServiceSoapBindingStub stub = (DODWebServiceSoapBindingStub) locator.getDODWebServicePort();
            stub.setUsername(wsUser);
            stub.setPassword(wsPassword);
            String slowLogsString = stub.getSlowLogs(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            slowLogs = slowLogsString.split(":");
        } catch (RemoteException ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (ServiceException ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return slowLogs;
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
            DODWebServiceLocator locator = new DODWebServiceLocator();
            DODWebServiceSoapBindingStub stub = (DODWebServiceSoapBindingStub) locator.getDODWebServicePort();
            stub.setUsername(wsUser);
            stub.setPassword(wsPassword);
            URI uri = stub.serveFile(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), filePath);
            if (uri != null)
                url = uri.toString();
        } catch (RemoteException ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (ServiceException ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return url;
    }
}
