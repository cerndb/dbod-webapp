package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODStatsDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODCommandStat;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODJobStat;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.ui.model.CommandStatsModel;
import ch.cern.dod.ui.model.JobStatsModel;
import ch.cern.dod.ui.model.OverviewTreeModel;
import ch.cern.dod.ui.renderer.CommandStatsRenderer;
import ch.cern.dod.ui.renderer.JobStatsRenderer;
import ch.cern.dod.ui.renderer.OverviewTreeRenderer;
import ch.cern.dod.util.DODConstants;
import java.util.List;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.*;

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
     * Stats DAO
     */
    private DODStatsDAO statsDAO;
    /**
     * List of instances. In this case, all the instances in the database.
     */
    private List<DODInstance> instances;
    /**
     * List of upgrades.
     */
    private List<DODUpgrade> upgrades;
    /**
     * List of command stats.
     */
    private List<DODCommandStat> commandStats;
    /**
     * List of job stats.
     */
    private List<DODJobStat> jobStats;
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
        
        //Select stats
        statsDAO = new DODStatsDAO();
        commandStats = statsDAO.selectCommandStats(instances);
        jobStats = statsDAO.selectJobStats(instances);
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    public void afterCompose() {
        if (instances != null && instances.size() > 0) {
            Tree overviewTree = (Tree) getFellow("overviewTree");
            overviewTree.setModel(OverviewTreeModel.getInstance(instances, overviewTree));
            overviewTree.setItemRenderer(new OverviewTreeRenderer(false));
            overviewTree.getPagingChild().setMold("os");
        }
        
        //Command stats grid
        Grid commandStatsGrid = (Grid) getFellow("commandStatsGrid");
        commandStatsGrid.setModel(new CommandStatsModel(commandStats));
        commandStatsGrid.setRowRenderer(new CommandStatsRenderer());
        commandStatsGrid.getPagingChild().setMold("os");
        
        //Job stats grid
        Grid jobStatsGrid = (Grid) getFellow("jobStatsGrid");
        jobStatsGrid.setModel(new JobStatsModel(jobStats));
        jobStatsGrid.setRowRenderer(new JobStatsRenderer());
        jobStatsGrid.getPagingChild().setMold("os");
        
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
        
        if (commandStats != null && commandStats.size() > 0) {
            ((Grid) getFellow("commandStatsGrid")).setStyle("display:block");
            ((Div) getFellow("emptyCommandStatsMsg")).setStyle("display:none");
            if (commandStats.size() > 10 && ((Grid) getFellow("commandStatsGrid")).getMold().equals("paging")) {
                ((Foot) getFellow("commandStatsFooter")).setStyle("display:block");
            }
            else {
                ((Foot) getFellow("commandStatsFooter")).setStyle("display:none");
            }
        }
        else {
            ((Grid) getFellow("commandStatsGrid")).setStyle("display:none");
            ((Div) getFellow("emptyCommandStatsMsg")).setStyle("display:block");
            ((Foot) getFellow("commandStatsFooter")).setStyle("display:none");
        }
        
        if (jobStats != null && jobStats.size() > 0) {
            ((Grid) getFellow("jobStatsGrid")).setStyle("display:block");
            ((Div) getFellow("emptyJobStatsMsg")).setStyle("display:none");
            if (((Grid) getFellow("jobStatsGrid")).getModel().getSize() > 10
                    && ((Grid) getFellow("jobStatsGrid")).getMold().equals("paging")) {
                ((Foot) getFellow("jobStatsFooter")).setStyle("display:block");
            }
            else {
                ((Foot) getFellow("jobStatsFooter")).setStyle("display:none");
            }
        }
        else {
            ((Grid) getFellow("jobStatsGrid")).setStyle("display:none");
            ((Div) getFellow("emptyJobStatsMsg")).setStyle("display:block");
            ((Foot) getFellow("jobStatsFooter")).setStyle("display:none");
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
        //Get command stats
        commandStats = statsDAO.selectCommandStats(instances);
        //Get job stats
        jobStats = statsDAO.selectJobStats(instances);

        //Set the new instances
        Tree overviewTree = (Tree) getFellow("overviewTree");
        int activePage = 0;
        if (overviewTree.getMold().equals("paging")) {
             activePage = overviewTree.getActivePage();
        }
        overviewTree.setModel(OverviewTreeModel.getInstance(instances, overviewTree));
        try {
            if (overviewTree.getMold().equals("paging")) {
                overviewTree.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        //Set the new command stats
        Grid commandStatsGrid = (Grid) getFellow("commandStatsGrid");
        ((CommandStatsModel)commandStatsGrid.getModel()).setCommandStats(commandStats);
        
        //Set the new job stats
        Grid jobStatsGrid = (Grid) getFellow("jobStatsGrid");
        ((JobStatsModel)jobStatsGrid.getModel()).setJobStats(jobStats);
        
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
        tree.setModel(OverviewTreeModel.getInstance(instances, tree));
        displayOrHideAreas();
    }
    
    /**
     * Displays all command stats in the view
     */
    public void showAllCommandStats() {
        Grid grid = (Grid) getFellow("commandStatsGrid");
        grid.setMold("default");
        Foot footer = (Foot) getFellow("commandStatsFooter");
        footer.setStyle("display:none");
    }
    
    /**
     * Displays all job stats in the view
     */
    public void showAllJobStats() {
        Grid grid = (Grid) getFellow("jobStatsGrid");
        grid.setMold("default");
        Foot footer = (Foot) getFellow("jobStatsFooter");
        footer.setStyle("display:none");
    }
    
    /**
     * Re-renders the grid in order to filter job stats.
     */
    public void filterJobStats () {
        //Re-render the grid
        Grid grid = (Grid) getFellow("jobStatsGrid");
        ((JobStatsModel) grid.getModel()).filterJobStats(((Textbox) getFellow("jobStatsDBNameFilter")).getValue(),
                                                            ((Textbox) getFellow("jobStatsCommandFilter")).getValue());
        
        displayOrHideAreas();
    }
}
