package ch.cern.dod.util;

import ch.cern.dod.ws.authentication.UserInfo;
import ch.cern.dod.ws.egroups.*;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.ws.BindingProvider;

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
     * Port to send requests to
     */
    private EgroupsService port;

    /**
     * Constructor for this class.
     *
     * @param user username to connect to web services
     * @param password password to connect to web services
     */
    public EGroupHelper(String user, String password) {
        this.wsUser = user;
        this.wsPassword = password;
        
        // Service locator
        EgroupsWebService webService = new EgroupsWebService();
        // Service stub
        this.port = webService.getEgroupsServiceSoap11();
        //Set username and password
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, wsUser);
        bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, wsPassword);
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
     * @param eGroupName name for the e-group to be created.
     * @param instanceName name of the instance.
     * @param userCCID id number of the owner of this e-group.
     * @param ownerIsMember indicates if the owner should be added as member or not.
     * @return true if the creation was successful.
     */
    public boolean createEGroup(String eGroupName, String instanceName, long userCCID, boolean ownerIsMember) {
        try {
            //Create eGroup
            EgroupType egroup = new EgroupType();
            egroup.setName(eGroupName);
            egroup.setType(EgroupTypeCode.STATIC_EGROUP);
            egroup.setDescription("e-group for DB On Demand instance " + instanceName);
            egroup.setUsage(UsageCode.SECURITY_MAILING);
            egroup.setTopic("DB On Demand");
            
            //User is owner
            UserType owner = new UserType();
            owner.setPersonId(userCCID);
            egroup.setOwner(owner);
            
            //Add members
            if (ownerIsMember) {
                //User is member
                MemberType member = new MemberType();
                member.setID(userCCID);
                member.setType(MemberTypeCode.PERSON);
                egroup.getMembers().add(member);
            }
            
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
            if (resp.getWarnings() != null && resp.getWarnings().size() > 0) {
                ListIterator<ErrorType> iter = resp.getWarnings().listIterator();
                while (iter.hasNext()) {
                    Logger.getLogger(EGroupHelper.class.getName()).log(Level.WARNING, "WARNING CREATING EGROUP {0}: {1}", new Object[]{eGroupName, iter.next().getMessage()});
                }
                return false;
            }
                        
            return true;

        } catch (Exception ex) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR CREATING EGROUP " + eGroupName, ex);
        }
        return false;
    }
    
    /**
     * Deletes an e-group
     * @param egroup e-group to delete
     * @return true if the operation is successful
     */
    public boolean deleteEgroup(String egroup) {
        try {
            //Create request
            DeleteEgroupRequest req = new DeleteEgroupRequest();
            req.setEgroupName(egroup);
            //Get result
            DeleteEgroupResponse resp = port.deleteEgroup(req);
            
            //Check errors
            if (resp.getError() != null) {
                Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR DELETING EGROUP {0}: {1}", new Object[]{egroup, resp.getError().getMessage()});
                return false;
            }
            
            return true;

        } catch (Exception ex) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR DELETING EGROUP " + egroup, ex);
        }
        return false;
    }
    
    /**
     * Checks if an e-group exists
     * @param egroup name of the egroup
     * @return true if exits, false otherwise
     */
    public boolean eGroupExists (String egroup) {
        return findEgroup(egroup) != null;
    }
    
    /**
     * Checks if an e-group has the usage code EGROUPS_ONLY
     * @param egroup name of the egroup
     * @return true if the egroup is EGROUPS_ONLY, false otherwise
     */
    public boolean isEgroupsOnly (String egroup) {
        EgroupType e = findEgroup(egroup);
        return e != null && UsageCode.EGROUPS_ONLY.equals(e.getUsage());
    }
        
  
    /**
     * Finds an e-group by name.
     *
     * @param egroup name of the e-group to check.
     * @return the group object.
     */
    private EgroupType findEgroup(String egroup) {
        try {                        
            // get e-group by name
            FindEgroupByNameRequest req = new FindEgroupByNameRequest();
            req.setName(egroup);
            FindEgroupByNameResponse resp = port.findEgroupByName(req);
            EgroupType group = (EgroupType) resp.getResult();
            
            //Check warnings (we do not check errors because if the group does not exist an error is returned)
            if (resp.getWarnings() != null && resp.getWarnings().size() > 0) {
                ListIterator<ErrorType> iter = resp.getWarnings().listIterator();
                while (iter.hasNext()) {
                    Logger.getLogger(EGroupHelper.class.getName()).log(Level.WARNING, "WARNING FINDING EGROUP {0}: {1}", new Object[]{egroup, iter.next().getMessage()});
                }
            }
            
            return group;
        } catch (Exception ex) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING EGROUP " + egroup, ex);
        }
        return null;
    }
    
    /**
     * Adds an e-group as member of another e-group
     * @param ownerEgroup e-group to add the other e-group to
     * @param memberEgroup e-goup to be added
     * @return true if successful
     */
    private boolean addEgroupAsMember (String ownerEgroup, String memberEgroup) {
        try {
            //Get member eGroup
            EgroupType member = findEgroup(memberEgroup);

            if (member != null) {
                //Add member
                MemberType m = new MemberType();
                m.setID(member.getID());
                m.setType(MemberTypeCode.fromValue(member.getType().value()));
                //Create request
                AddEgroupMembersRequest req = new AddEgroupMembersRequest();
                req.setEgroupName(ownerEgroup);
                req.getMembers().add(m);
                req.setOverwriteMembers(false);
                //Get result
                AddEgroupMembersResponse resp = port.addEgroupMembers(req);

                //Check errors
                if (resp.getError() != null) {
                    Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR ADDING EGROUP {0} TO EGROUP {1}: {2}", new Object[]{memberEgroup, ownerEgroup, resp.getError().getMessage()});
                    return false;
                }
                //Check warnings
                if (resp.getWarnings() != null && resp.getWarnings().size() > 0) {
                    ListIterator<ErrorType> iter = resp.getWarnings().listIterator();
                    while (iter.hasNext()) {
                        Logger.getLogger(EGroupHelper.class.getName()).log(Level.WARNING, "WARNING ADDING EGROUP {0} TO EGROUP {1}: {2}", new Object[]{memberEgroup, ownerEgroup, iter.next().getMessage()});
                    }
                    return false;
                }

                return true;
            }
            else return false;

        } catch (Exception ex) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR ADDING EGROUP " + memberEgroup + " TO EGROUP " + ownerEgroup, ex);
        }
        return false;
    }
    
    /**
     * Remove an e-group as member of another e-group
     * @param ownerEgroup e-group to remove the other e-group from
     * @param memberEgroup e-goup to be removed
     * @return true if successful
     */
    private boolean removeEgroupAsMember (String ownerEgroup, String memberEgroup) {
        try {
            //Get owner and member eGroups
            EgroupType member = findEgroup(memberEgroup);

            if (member != null) {
                //Remove member
                MemberType m = new MemberType();
                m.setID(member.getID());
                m.setType(MemberTypeCode.fromValue(member.getType().value()));

                //Create request
                RemoveEgroupMembersRequest req = new RemoveEgroupMembersRequest();
                req.setEgroupName(ownerEgroup);
                req.getMembers().add(m);
                //Get result
                RemoveEgroupMembersResponse resp = port.removeEgroupMembers(req);

                //Check errors
                if (resp.getError() != null) {
                    Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR REMOVING EGROUP {0} FROM EGROUP {1}: {2}", new Object[]{memberEgroup, ownerEgroup, resp.getError().getMessage()});
                    return false;
                }
                //Check warnings
                if (resp.getWarnings() != null && resp.getWarnings().size() > 0) {
                    ListIterator<ErrorType> iter = resp.getWarnings().listIterator();
                    while (iter.hasNext()) {
                        Logger.getLogger(EGroupHelper.class.getName()).log(Level.WARNING, "WARNING REMOVING EGROUP {0} FROM EGROUP {1}: {2}", new Object[]{memberEgroup, ownerEgroup, iter.next().getMessage()});
                    }
                    return false;
                }

                return true;
            }
            else return false;

        } catch (Exception ex) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "ERROR REMOVING EGROUP " + memberEgroup + " FROM EGROUP " + ownerEgroup, ex);
        }
        return false;
    }
    
    /**
     * Creates an e-group to be assigned to a role on OEM, and adds the given e-group to it.
     * It also adds the created e-group to the groups managed by OEM.
     * @param instanceName name of the instance to add
     * @param egroup name of the e-group given by the user
     * @return true if the operation is successful
     */
    public boolean addEgroupToOEM(String instanceName, String egroup) {
        //Get dodws CCID (curently same as dgomezbl but someone will inherit it)
        AuthenticationHelper authenticationHelper = new AuthenticationHelper(wsUser, wsPassword);
        if (authenticationHelper == null) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "COULD NOT GET AUTHENTICATION HELPER");
            return false;
        }
        UserInfo info = authenticationHelper.getUserInfo("dodws");
        if (info == null) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "COULD NOT GET DODWS USER INFO");
            return false;
        }
        if (info.getCcid() <= 0) {
            Logger.getLogger(EGroupHelper.class.getName()).log(Level.SEVERE, "COULD NOT GET DODWS CCID");
            return false;
        }
        return createEGroup(DODConstants.OEM_PDB_EGROUP_PREFIX + instanceName, instanceName, info.getCcid(), false)
                            && addEgroupAsMember(DODConstants.OEM_PDB_EGROUP_PREFIX + instanceName, egroup)
                            && addEgroupAsMember(DODConstants.OEM_EGROUP, DODConstants.OEM_PDB_EGROUP_PREFIX + instanceName);
    }
    
    /**
     * Removes the e-group associated to a role from the master e-group and deletes it.
     * @param instanceName name of the instance to remove
     * @return true if the operation is successful
     */
    public boolean removeEgroupFromOEM(String instanceName) {
        return removeEgroupAsMember(DODConstants.OEM_EGROUP, DODConstants.OEM_PDB_EGROUP_PREFIX + instanceName)
                && deleteEgroup(DODConstants.OEM_PDB_EGROUP_PREFIX + instanceName);
    }
    
    /**
     * Changes the e-group associated with an instance in OEM
     * @param instanceName name of the instance to change
     * @param oldEgroup old e-group
     * @param newEgroup new e-group
     * @return 
     */
    public boolean changeEgroupInOEM(String instanceName, String oldEgroup, String newEgroup) {
        return removeEgroupAsMember(DODConstants.OEM_PDB_EGROUP_PREFIX + instanceName, oldEgroup)
                && addEgroupAsMember(DODConstants.OEM_PDB_EGROUP_PREFIX + instanceName, newEgroup);
    }
}
