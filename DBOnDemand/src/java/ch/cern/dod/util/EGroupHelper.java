package ch.cern.dod.util;

import ch.cern.dod.ws.egroups.*;
import ch.cern.dod.ws.egroups.cra.*;
import java.rmi.RemoteException;
import java.util.StringTokenizer;
import javax.xml.rpc.ServiceException;

/**
 * Helper to manage e-groups. It also provides a static method to check if a group is
 * in a list of groups. To manage e-groups, it makes use of CERN SOAP Web Services
 * using the Axis 1.4 library. More information about CERN SOAP Web Services can be found
 * here https://espace.cern.ch/e-groups-help/Web%20services/How%20to%20use%20SOAP%20Web%20Services%20to%20manage%20e-groups.aspx
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
     * @param user username to connect to web services
     * @param password password to connect to web services
     */
    public EGroupHelper(String user, String password) {
        this.wsUser = user;
        this.wsPassword = password;
    }

    /**
     * Checks if a group is in a list of groups.
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
     * Creates a new eGroup for the given user and instance with the given instance name.
     * @param eGroupName name for the e-group to be created.
     * @param instanceName name of the instance.
     * @param userCCID id number of the owner of this e-group.
     * @param name full name of the owner of this e-group.
     * @return List of groups for the specified user.
     * @throws ServiceException if there is an error executing the web service.
     * @throws RemoteException if there is an error connection to the server.
     */
    public boolean createEGroup(String eGroupName, String instanceName, long userCCID, String name) throws ServiceException, RemoteException {
        // Service locator
        CraEgroupsWebService_ServiceLocator serviceLocator = new CraEgroupsWebService_ServiceLocator();
        // Service stub
        CraEgroupsWebServiceBindingStub stub = (CraEgroupsWebServiceBindingStub) serviceLocator.getCraEgroupsWebService();
        //Create eGroup
        EgroupType egroup = new EgroupType();
        egroup.setName(eGroupName);
        egroup.setID(0);
        egroup.setType(EgroupTypeCode.StaticEgroup);
        egroup.setDescription("e-group for DBOnDemand instance " + instanceName);
        egroup.setUsage(UsageCode.EgroupsOnly);
        egroup.setTopic("DB On Demand");
        //User is owner
        UserType owner = new UserType();
        owner.setCCID(userCCID);
        owner.setType(UserTypeCode.Person);
        egroup.setOwner(owner);
        //User is member
        MemberType[] members = new MemberType[1];
        MemberType member = new MemberType();
        member.setID(String.valueOf(userCCID));
        member.setType(MemberTypeCode.Person);
        member.setName(name);
        members[0] = member;
        egroup.setMembers(members);
        //Group privacy and subscription
        egroup.setPrivacy(PrivacyType.Members);
        egroup.setSelfsubscription(SelfsubscriptionType.UsersWithAdminApproval);
        

        //Create request
        SynchronizeEgroupRequest req = new SynchronizeEgroupRequest(wsUser, wsPassword, egroup);
        //Get result
        SynchronizeEgroupResponse resp = (SynchronizeEgroupResponse) stub.synchronizeEgroup(req);
        //Return result
        return resp.isResult();
    }

    /**
     * Checks if an e-group already exisits.
     * @param egroup name of the e-group to check.
     * @return true if the e-group exists, false otherwise.
     * @throws ServiceException if there is an error executing the web service or if the e-group does not exist.
     * @throws RemoteException if there is an error connection to the server.
     */
    public boolean eGroupExists(String egroup) throws ServiceException, RemoteException {
        // Service locator
        CraEgroupsWebService_ServiceLocator serviceLocator = new CraEgroupsWebService_ServiceLocator();

        // Service stub
        CraEgroupsWebServiceBindingStub stub = (CraEgroupsWebServiceBindingStub) serviceLocator.getCraEgroupsWebService();

        // get e-group by name
        FindEgroupByNameRequest findEgroupByName = new FindEgroupByNameRequest(wsUser, wsPassword, egroup);
        FindEgroupByNameResponse resp = (FindEgroupByNameResponse) stub.findEgroupByName(findEgroupByName);
        EgroupType group = (EgroupType) resp.getResult();
        if (group.getID() > 0)
            return true;
        else
            return false;
    }
}
