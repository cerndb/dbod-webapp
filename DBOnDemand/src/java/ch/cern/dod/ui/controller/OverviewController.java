package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.ui.model.InstanceListModel;
import ch.cern.dod.ui.renderer.OverviewGridRenderer;
import ch.cern.dod.util.DODConstants;
import java.util.List;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.Grid;
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
     * Number of instances, defining a public method to show the grid or not.
     */
    private int instancesSize;
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
        if (instances != null)
            instancesSize = instances.size();
        else
            instancesSize = 0;

    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    public void afterCompose() {
        if (instancesSize > 0) {
            Grid grid = (Grid) getFellow("overviewGrid");
            grid.setModel(new InstanceListModel(instances));
            grid.setRowRenderer(new OverviewGridRenderer());
            grid.getPagingChild().setMold("os");
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
        if (instances != null)
            instancesSize = instances.size();
        else
            instancesSize = 0;

        //Set the new instances
        Grid grid = (Grid) getFellow("overviewGrid");
        ((InstanceListModel)grid.getModel()).setInstances(instances);
    }

    /**
     * Getter for the instances size.
     * @return number of instances.
     */
    public int getInstancesSize() {
        return instancesSize;
    }
}
