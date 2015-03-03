package ch.cern.dbod.util;

import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.ws.DBODWebService;
import ch.cern.dbod.ws.DBODWebServicePortType;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper to get params from instances. It uses the SOAP web services
 * implemented in the Syscontrol project
 * @author Daniel Gomez Blanco
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
    public String getParam(Instance instance, String param) {
        String paramValue = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            paramValue = port.getParam(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName(), param);
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
    public String getHost(Instance instance) {
        String host = null;
        try {
            DBODWebService service = new DBODWebService();
            DBODWebServicePortType port = service.getDBODWebServicePort();
            host = port.getHost(CommonConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
        } catch (Exception ex) {
            Logger.getLogger(ParamsHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING HOST OF INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return host;
    }
}
