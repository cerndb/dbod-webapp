package ch.cern.dod.util;

import ch.cern.dod.ws.egroups.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.handler.MessageContext;

/**
 * Helper to manage e-groups. It also provides a static method to check if a
 * group is in a list of groups. To manage e-groups, it makes use of CERN SOAP
 * Web Services using the Axis 1.4 library. More information about CERN SOAP Web
 * Services can be found here
 * https://espace.cern.ch/e-groups-help
 *
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class EGroupHelper {

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
     *
     * @param user username to connect to web services
     * @param password password to connect to web services
     */
    public EGroupHelper(String user, String password) {
        this.wsUser = user;
        this.wsPassword = password;
    }

    /**
     * Checks if a group is in a list of groups.
     *
     * @param group group to check
     * @param groups groups to check, separated by ";"
     * @return true if the group is in the list, false otherwise
     */
    public static boolean groupInList(String group, String groups) {
        if (group != null && groups != null) {
            StringTokenizer tokenizer = new StringTokenizer(groups, ";");
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                if (token.equals(group)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Creates a new eGroup for the given user and instance with the given
     * instance name.
     *
     * @param eGroupName name for the e-group to be created.
     * @param instanceName name of the instance.
     * @param userCCID id number of the owner of this e-group.
     * @param name full name of the owner of this e-group.
     * @return List of groups for the specified user.
     */
    public boolean createEGroup(String eGroupName, String instanceName, long userCCID, String name) {
        try {
            // Service locator
            EgroupsWebService webService = new EgroupsWebService();
            // Service stub
            EgroupsService port = webService.getEgroupsServiceSoap11();
            //Set username and password
            BindingProvider bp = (BindingProvider) port;
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, wsUser);
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, wsPassword);
            //Create eGroup
            EgroupType egroup = new EgroupType();
            egroup.setName(eGroupName);
            egroup.setType(EgroupTypeCode.STATIC_EGROUP);
            egroup.setDescription("e-group for DB On Demand instance " + instanceName);
            egroup.setUsage(UsageCode.EGROUPS_ONLY);
            egroup.setTopic("DB On Demand");
            //User is owner
            UserType owner = new UserType();
            owner.setPersonId(userCCID);
            egroup.setOwner(owner);
            //User is member
            MemberType member = new MemberType();
            member.setID(String.valueOf(userCCID));
            member.setType(MemberTypeCode.PERSON);
            member.setName(name);
            MembersType members = new MembersType();
            members.getMembers().add(member);
            egroup.setMembers(members);
            //Group privacy and subscription
            egroup.setPrivacy(PrivacyType.MEMBERS);
            egroup.setSelfsubscription(SelfsubscriptionType.USERS_WITH_ADMIN_APPROVAL);

            //Create request
            SynchronizeEgroupRequest req = new SynchronizeEgroupRequest();
            req.setEgroup(egroup);
            //Get result
            SynchronizeEgroupResponse resp = port.synchronizeEgroup(req);
            
            //Check errors
            if (resp.getError() != null) {
                Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR CREATING EGROUP {0}: {1}", new Object[]{eGroupName, resp.getError().getMessage()});
                
                return false;
            }
            //Check warnings
            if (resp.getWarnings() != null) {
                List<ErrorType> warnings = resp.getWarnings().getWarnings();
                if (warnings != null) {
                    ListIterator<ErrorType> iter = warnings.listIterator();
                    while (iter.hasNext()) {
                        Logger.getLogger(EGroupHelper.class.getName()).log(Level.WARNING, "WARNING CREATING EGROUP {0}: {1}", new Object[]{eGroupName, iter.next().getMessage()});
                    }
                }
            }
            
            return true;

        } catch (Exception ex) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR CREATING EGROUP " + eGroupName, ex);
        }
        return false;
    }

    /**
     * Checks if an e-group already exists.
     *
     * @param egroup name of the e-group to check.
     * @return true if the e-group exists, false otherwise.
     */
    public boolean eGroupExists(String egroup) {
        try {            
            // Service locator
            EgroupsWebService webSservice = new EgroupsWebService();
            // Service stub
            EgroupsService port = webSservice.getEgroupsServiceSoap11();
            
            //Set username and password
            BindingProvider bp = (BindingProvider) port;
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, wsUser);
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, wsPassword);
            
            // get e-group by name
            FindEgroupByNameRequest req = new FindEgroupByNameRequest();
            req.setName(egroup);
            FindEgroupByNameResponse resp = port.findEgroupByName(req);
            EgroupType group = (EgroupType) resp.getResult();
            
            //Check warnings (we do not check errors because if the group does not exist an error is returned)
            if (resp.getWarnings() != null) {
                List<ErrorType> warnings = resp.getWarnings().getWarnings();
                if (warnings != null) {
                    ListIterator<ErrorType> iter = warnings.listIterator();
                    while (iter.hasNext()) {
                        Logger.getLogger(EGroupHelper.class.getName()).log(Level.WARNING, "WARNING FINDING EGROUP {0}:\n{1}\n", new Object[]{egroup, iter.next().getMessage()});
                    }
                }
            }
            
            return group != null && group.getID() > 0;
        } catch (Exception ex) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING EGROUP " + egroup, ex);
        }
        return false;
    }
}
