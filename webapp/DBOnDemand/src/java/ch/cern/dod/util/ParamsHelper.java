package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ws.DODWebService;
import ch.cern.dod.ws.DODWebServicePortType;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     */
    public String getParam(DODInstance instance, String param) {
        String paramValue = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            paramValue = port.getParam(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), param);
        } catch (Exception ex) {
            Logger.getLogger(ParamsHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING PARAM ON INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return paramValue;
    }
    
    /**
     * Gets th host of an instance.
     * @param instance instance to get the host of.
     * @return host value.
     */
    public String getHost(DODInstance instance) {
        String host = null;
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            host = port.getHost(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
        } catch (Exception ex) {
            Logger.getLogger(ParamsHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING HOST OF INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return host;
    }
}
