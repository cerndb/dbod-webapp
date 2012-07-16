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
import ch.cern.dod.ui.model.UpgradesListModel;
import ch.cern.dod.ui.renderer.CommandStatsRenderer;
import ch.cern.dod.ui.renderer.JobStatsRenderer;
import ch.cern.dod.ui.renderer.OverviewTreeRenderer;
import ch.cern.dod.ui.renderer.UpgradesGridRenderer;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.*;

/**
 * Controller for the admin overview of instances. It allows the admins to manage every instance.
 * @author Daniel Gomez Blanco
 */
public class AdminController extends Vbox implements BeforeCompose, AfterCompose{
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
     * List of upgrades.
     */
    private List<DODJobStat> jobStats;
    /**
     * Job helper to perform admin actions.
     */
    private JobHelper jobHelper;
    /**
     * User authenticated in the system.
     */
    private String username;

    /**
     * Method executed before the page is composed. Obtains instances from DB.
     */
    public void beforeCompose() {
        Execution execution = Executions.getCurrent();
        username = execution.getHeader(DODConstants.ADFS_LOGIN);
        jobHelper = new JobHelper(true);
        
        //Select upgrades
        upgradeDAO = new DODUpgradeDAO();
        upgrades = upgradeDAO.selectAll();

        //Select instances
        instanceDAO = new DODInstanceDAO();
        instances = instanceDAO.selectAll(upgrades);
        
        //Select stats
        statsDAO = new DODStatsDAO();
        commandStats = statsDAO.selectCommandStats();
        jobStats = statsDAO.selectJobStats();
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    public void afterCompose() {
        //Instances tree
        Tree overviewTree = (Tree) getFellow("overviewTree");
        overviewTree.setModel(OverviewTreeModel.getInstance(instances));
        overviewTree.setItemRenderer(new OverviewTreeRenderer(true));
        overviewTree.getPagingChild().setMold("os");
        
        //Upgrades grid
        Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
        upgradesGrid.setModel(new UpgradesListModel(upgrades));
        upgradesGrid.setRowRenderer(new UpgradesGridRenderer(upgradeDAO));
        upgradesGrid.getPagingChild().setMold("os");
        
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
            ((Hbox) getFellow("collectiveBtns")).setStyle("display:block");
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
            ((Hbox) getFellow("collectiveBtns")).setStyle("display:none");
            ((Tree) getFellow("overviewTree")).setStyle("display:none");
            ((Div) getFellow("emptyInstancesMsg")).setStyle("display:block");
            ((Treefoot) getFellow("footer")).setStyle("display:none");
        }
        
        if (upgrades != null && upgrades.size() > 0) {
            ((Grid) getFellow("upgradesGrid")).setStyle("display:block");
            ((Div) getFellow("emptyUpgradesMsg")).setStyle("display:none");
            if (upgrades.size() > 10 && ((Grid) getFellow("upgradesGrid")).getMold().equals("paging")) {
                ((Foot) getFellow("footerUpgrades")).setStyle("display:block");
            }
            else {
                ((Foot) getFellow("footerUpgrades")).setStyle("display:none");
            }
        }
        else {
            ((Grid) getFellow("upgradesGrid")).setStyle("display:none");
            ((Div) getFellow("emptyUpgradesMsg")).setStyle("display:block");
            ((Foot) getFellow("footerUpgrades")).setStyle("display:none");
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
            if (jobStats.size() > 10 && ((Grid) getFellow("jobStatsGrid")).getMold().equals("paging")) {
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
        instances = instanceDAO.selectAll(upgrades);
        
        //Get command stats
        commandStats = statsDAO.selectCommandStats();
        
        //Get job stats
        jobStats = statsDAO.selectJobStats();
        
        //Refresh checked list
        Tree tree = (Tree) getFellow("overviewTree");
        int activePage = 0;
        if (tree.getMold().equals("paging")) {
            activePage = tree.getActivePage();
        }
        ((OverviewTreeRenderer)tree.getItemRenderer()).updateCheckedInstances(instances);
        //Set the new instances
        tree.setModel(OverviewTreeModel.getInstance(instances));
        try {
            if (tree.getMold().equals("paging")) {
                tree.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        //Set the new upgrades
        Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
        if (upgradesGrid.getMold().equals("paging")) {
            activePage = upgradesGrid.getActivePage();
        }
        ((UpgradesListModel)upgradesGrid.getModel()).setUpgrades(upgrades);
        try {
            if (upgradesGrid.getMold().equals("paging")) {
                upgradesGrid.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        //Set the new command stats
        Grid commandStatsGrid = (Grid) getFellow("commandStatsGrid");
        if (commandStatsGrid.getMold().equals("paging")) {
            activePage = commandStatsGrid.getActivePage();
        }
        ((CommandStatsModel)commandStatsGrid.getModel()).setCommandStats(commandStats);
        try {
            if (commandStatsGrid.getMold().equals("paging")) {
                commandStatsGrid.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        //Set the new job stats
        Grid jobStatsGrid = (Grid) getFellow("jobStatsGrid");
        if (jobStatsGrid.getMold().equals("paging")) {
            activePage = jobStatsGrid.getActivePage();
        }
        ((JobStatsModel)jobStatsGrid.getModel()).setJobStats(jobStats);
        try {
            if (jobStatsGrid.getMold().equals("paging")) {
                jobStatsGrid.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}

        displayOrHideAreas();
    }
    
    /**
     * Checks all the instances for a group operation
     */
    public void checkAll() {
        Checkbox checkAll = (Checkbox)getFellow("checkAll");
        Tree tree = (Tree) getFellow("overviewTree");
        ((OverviewTreeRenderer)tree.getItemRenderer()).checkAll(instances, tree, checkAll.isChecked());
        List<DODInstance> checked = ((OverviewTreeRenderer)tree.getItemRenderer()).getChecked();
        
        if (checkAll.isChecked()) {
            if (checked.size() > 0) {
                ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(false);
                ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(false);
                ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(false);
                ((Toolbarbutton) getFellow("upgradeAllBtn")).setDisabled(false);
                ((Toolbarbutton) getFellow("startupAllBtn")).setZclass("button");
                ((Toolbarbutton) getFellow("shutdownAllBtn")).setZclass("button");
                ((Toolbarbutton) getFellow("backupAllBtn")).setZclass("button");
                ((Toolbarbutton) getFellow("upgradeAllBtn")).setZclass("button");
            }
        }
        else {
            ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("startupAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("backupAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setZclass("buttonDisabled");
        }
        
        //Re-render the tree
        tree.setModel(OverviewTreeModel.getInstance(instances));
    }
    
    /**
     * Startup all selected instances. It checks that instances are not awaiting approval or running.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void startupAll() {
        boolean error = false;
        Tree tree = (Tree) getFellow("overviewTree");
        List<DODInstance> checked = ((OverviewTreeRenderer)tree.getItemRenderer()).getChecked();
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && !instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)) {
                if (!jobHelper.doStartup(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the tree
        tree.setModel(OverviewTreeModel.getInstance(instances));
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Shutdown all selected instances. It checks that instances are not awaiting approval or stopped.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void shutdownAll() {
        boolean error = false;
        Tree tree = (Tree) getFellow("overviewTree");
        List<DODInstance> checked = ((OverviewTreeRenderer)tree.getItemRenderer()).getChecked();
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
                if (!jobHelper.doShutdown(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the tree
        tree.setModel(OverviewTreeModel.getInstance(instances));
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Backup all selected instances. It checks that instances are not awaiting approval.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void backupAll(){
        boolean error = false;
        Tree tree = (Tree) getFellow("overviewTree");
        List<DODInstance> checked = ((OverviewTreeRenderer)tree.getItemRenderer()).getChecked();
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
                if (!jobHelper.doBackup(instance, username, 0)){
                    error = true;
                }
            }
        }

        //Re-render the tree
        tree.setModel(OverviewTreeModel.getInstance(instances));
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Upgrade all selected instances. It checks that instances are not awaiting approval and have a pending upgrade.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void upgradeAll(){
        boolean error = false;
        Tree tree = (Tree) getFellow("overviewTree");
        List<DODInstance> checked = ((OverviewTreeRenderer)tree.getItemRenderer()).getChecked();
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && instance.getUpgradeTo() != null && !instance.getUpgradeTo().isEmpty()) {
                if (!jobHelper.doUpgrade(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the tree
        tree.setModel(OverviewTreeModel.getInstance(instances));
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Opens the add upgrade window.
     */
    public void openAddUpgrade(){
        try {
            AddUpgradeController upgradeController = new AddUpgradeController();
            //Only show window if it is not already being diplayed
            if (getFellowIfAny(upgradeController.getId()) == null) {
                upgradeController.setParent(this);
                upgradeController.doModal();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, "ERROR OPENING ADD UPGRADE WINDOW", ex);
            showError(DODConstants.ERROR_OPENING_ADD_UPGRADE);
        }
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
     * Displays all upgrades in the view
     */
    public void showAllUpgrades() {
        Grid grid = (Grid) getFellow("upgradesGrid");
        grid.setMold("default");
        Foot footer = (Foot) getFellow("footerUpgrades");
        footer.setStyle("display:none");
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
     * Re-renders the tree in order to filter instances.
     */
    public void filterInstances () {
        //Re-render the tree
        Tree tree = (Tree) getFellow("overviewTree");
        tree.setModel(OverviewTreeModel.getInstance(instances));
        displayOrHideAreas();
    }
    
    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(String errorCode) {
        Window errorWindow = (Window) getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}