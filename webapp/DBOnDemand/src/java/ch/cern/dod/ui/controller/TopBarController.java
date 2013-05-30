package ch.cern.dod.ui.controller;

import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.Div;

/**
 * Controller for top bar's actions.
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class TopBarController extends Div implements BeforeCompose{
    
    /**
     * e-groups this user belongs to
     */
    private String eGroups;
    /**
     * Indicates if the user is admin
     */
    private boolean admin;
    /**
     * Userame logged in
     */
    private String username;
    
    /**
     * Full name logged in
     */
    private String fullName;

    public void beforeCompose() {
        //Get username and groups
        Execution execution = Executions.getCurrent();
        eGroups = execution.getHeader(DODConstants.ADFS_GROUP);

        //Only show admin link if the user is admin
        admin = EGroupHelper.groupInList(DODConstants.ADMIN_E_GROUP, eGroups);
        
        //Get authenticated user's name
        username = execution.getHeader(DODConstants.ADFS_LOGIN);
        fullName = execution.getHeader(DODConstants.ADFS_FULLNAME);

    }

    public boolean isAdmin() {
        return admin;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }
}
