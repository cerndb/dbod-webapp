package ch.cern.dod.ui.controller;

import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.Div;
import org.zkoss.zul.Menu;

/**
 * Controller for top bar's actions.
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class TopBarController extends Div implements BeforeCompose{
    
    /**
     * Authenticated user's full name
     */
    private String fullName;
    /**
     * e-groups this user belongs to
     */
    private String eGroups;
    /**
     * Indicates if the user is admin
     */
    private boolean admin;

    public void beforeCompose() {
        //Get username and groups
        Execution execution = Executions.getCurrent();
        eGroups = execution.getHeader(DODConstants.ADFS_GROUP);

        //Only show admin link if the user is admin
        admin = EGroupHelper.groupInList(DODConstants.ADMIN_E_GROUP, eGroups);
    }

    /**
     * Initializes the top bar component
     */
    public void init() {
        //Get authenticated user's name
        Execution execution = Executions.getCurrent();
        fullName = execution.getHeader(DODConstants.ADFS_FULLNAME);

        //Set the drop down menu title
        String title = Labels.getLabel(DODConstants.LABEL_WELCOME) + " " + fullName;
        ((Menu) getFellow("accountMenu")).setLabel(title);
    }

    /**
     * Getter for the admin attribute.
     * @return true if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return admin;
    }
}
