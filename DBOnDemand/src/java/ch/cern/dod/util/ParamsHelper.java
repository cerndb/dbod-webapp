package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ws.DODWebServiceLocator;
import ch.cern.dod.ws.DODWebServiceSoapBindingStub;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.rpc.ServiceException;

/**
 * Helper to get params from instances.
 * @author Daniel Gomez Blanco
 * @version 17/10/2011
 */
public class ParamsHelper {

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
    public ParamsHelper(String user, String password) {
        this.wsUser = user;
        this.wsPassword = password;
    }

    /**
     * Gets a param from the instance.
     * @param instance instance to get the snapshots of.
     * @param param param to get.
     * @return param value.
     * @throws ServiceException if there is an error executing the web service.
     * @throws RemoteException if there is an error connection to the server.
     */
    public String getParam(DODInstance instance, String param) {
        String paramValue = null;
        try {
            DODWebServiceLocator locator = new DODWebServiceLocator();
            DODWebServiceSoapBindingStub stub = (DODWebServiceSoapBindingStub) locator.getDODWebServicePort();
            stub.setUsername(wsUser);
            stub.setPassword(wsPassword);
            paramValue = stub.getParam(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), param);
        } catch (RemoteException ex) {
            Logger.getLogger(ParamsHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (ServiceException ex) {
            Logger.getLogger(ParamsHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        }
        return paramValue;
    }
}
