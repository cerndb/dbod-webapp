package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.ui.model.OverviewTreeModel;
import ch.cern.dod.ui.renderer.OverviewTreeRenderer;
import ch.cern.dod.util.DODConstants;
import java.util.List;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.Div;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treefoot;
import org.zkoss.zul.Vbox;

/**
 * Controller for the overview page
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class OverviewController extends Vbox implements BeforeCompose, AfterCompose{
    /**
     * Upgrade DAO
     */
    private DODUpgradeDAO upgradeDAO;
    /**
     * Instance DAO
     */
    private DODInstanceDAO instanceDAO;
    /**
     * List of instances. In this case, all the instances in the database.
     */
    private List<DODInstance> instances;
    /**
     * List of upgrades.
     */
    private List<DODUpgrade> upgrades;
    /**
     * User authenticated in the system.
     */
    private String username;
    /**
     * e-groups the authenticated user belongs to.
     */
    private String eGroups;

    /**
     * Method executed before the page is composed. It checks authorization
     * and obtains instances from the database.
     */
    public void beforeCompose() {
        //Get username and groups
        Execution execution = Executions.getCurrent();
        username = execution.getHeader(DODConstants.ADFS_LOGIN);
        eGroups = execution.getHeader(DODConstants.ADFS_GROUP);
        
        //Get upgrades
        upgradeDAO =  new DODUpgradeDAO();
        upgrades = upgradeDAO.selectAll();
        
        //Get instances
        instanceDAO = new DODInstanceDAO();
        instances = instanceDAO.selectByUserNameAndEGroups(username, eGroups, upgrades);
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    public void afterCompose() {
        if (instances != null && instances.size() > 0) {
            Tree overviewTree = (Tree) getFellow("overviewTree");
            overviewTree.setModel(OverviewTreeModel.getInstance(instances));
            overviewTree.setItemRenderer(new OverviewTreeRenderer(false));
            overviewTree.getPagingChild().setMold("os");
        }
        
        displayOrHideAreas();
    }
    
    /**
     * Displays or hides certain areas when refreshing instances and upgrades
     */
    private void displayOrHideAreas () {
        if (instances != null && instances.size() > 0) {
            ((Tree) getFellow("overviewTree")).setStyle("display:block");
            ((Div) getFellow("emptyInstancesMsg")).setStyle("display:none");
            if (((Tree) getFellow("overviewTree")).getItemCount() > 10 && ((Tree) getFellow("overviewTree")).getMold().equals("paging")) {
                ((Treefoot) getFellow("footer")).setStyle("display:block");
            }
            else {
                ((Treefoot) getFellow("footer")).setStyle("display:none");
            }
        }
        else {
            ((Tree) getFellow("overviewTree")).setStyle("display:none");
            ((Div) getFellow("emptyInstancesMsg")).setStyle("display:block");
            ((Treefoot) getFellow("footer")).setStyle("display:none");
        }
    }

    /**
     * Refreshes the list of instances.
     */
    public void refreshInstances(){
        //Get upgrades
        upgrades = upgradeDAO.selectAll();
        //Get instances
        instances = instanceDAO.selectByUserNameAndEGroups(username, eGroups, upgrades);

        //Set the new instances
        Tree overviewTree = (Tree) getFellow("overviewTree");
        int activePage = 0;
        if (overviewTree.getMold().equals("paging")) {
             activePage = overviewTree.getActivePage();
        }
        overviewTree.setModel(OverviewTreeModel.getInstance(instances));
        try {
            if (overviewTree.getMold().equals("paging")) {
                overviewTree.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        displayOrHideAreas();
    }
    
    /**
     * Displays all instances in the view
     */
    public void showAll() {
        Tree tree = (Tree) getFellow("overviewTree");
        tree.setMold("default");
        Treefoot footer = (Treefoot) getFellow("footer");
        footer.setStyle("display:none");
    }
    
    /**
     * Filters instances according to the content on the filter fields.
     */
    public void filterInstances () {
        //Re-render the tree
        Tree tree = (Tree) getFellow("overviewTree");
        tree.setModel(OverviewTreeModel.getInstance(instances));
        displayOrHideAreas();
    }
}
