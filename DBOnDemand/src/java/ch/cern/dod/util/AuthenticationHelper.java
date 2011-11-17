package ch.cern.dod.util;

import ch.cern.dod.ws.authentication.AuthenticationLocator;
import ch.cern.dod.ws.authentication.AuthenticationSoapStub;
import ch.cern.dod.ws.authentication.UserInfo;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

/**
 * Helper to obtain information and manage user accounts.
 * @author Daniel Gomez Blanco
 * @version 14/11/2011
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

    public UserInfo getUserInfo(String username) throws ServiceException, RemoteException {
        AuthenticationLocator locator = new AuthenticationLocator();
        AuthenticationSoapStub service = (AuthenticationSoapStub) locator.getAuthenticationSoap();
        service.setUsername(wsUser);
        service.setPassword(wsPassword);
        UserInfo info = service.getUserInfoFromLogin(username);
        return info;
    }
}
