package ch.cern.dbod.util;

import ch.cern.dbod.ws.authentication.Authentication;
import ch.cern.dbod.ws.authentication.AuthenticationSoap;
import ch.cern.dbod.ws.authentication.UserInfo;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.BindingProvider;

/**
 * Helper to obtain information and manage user accounts. It uses the SOAP
 * API documented here https://espace.cern.ch/authentication/CERN%20Authentication%20Help/SOAP%20WebServices.aspx
 * @author Daniel Gomez Blanco
 */
public class AuthenticationHelper {

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
    public AuthenticationHelper(String user, String password) {
        this.wsUser = user;
        this.wsPassword = password;
    }

    /**
     * Gets the connected user information.
     * @param username user to obtain the information from.
     * @return information from user passed as parameter.
     * @return user information
     */
    public UserInfo getUserInfo(String username) {
        try {
            Authentication service = new Authentication();
            AuthenticationSoap port = service.getAuthenticationSoap();
            ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, wsUser);
            ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, wsPassword);
            UserInfo info = port.getUserInfoFromLogin(username);
            return info;
        }
        catch (Exception ex) {
            Logger.getLogger(AuthenticationHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING USER " + username, ex);
        }
        return null;
    }
}
