package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODStatsDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODCommandStat;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODJobStat;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.ui.model.*;
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
import org.zkoss.zk.ui.Sessions;
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
     * List of job stats.
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
        overviewTree.setModel(OverviewTreeModel.getInstance(instances, overviewTree));
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
        
        //Get show all from session
        Boolean showAll = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_SHOW_ALL);
        if (showAll != null && showAll)
            showAll();
        Boolean showAllJobStats = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_SHOW_ALL_JOB_STATS);
        if (showAllJobStats != null && showAllJobStats)
            showAllJobStats();
        Boolean showAllCommandStats = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_SHOW_ALL_COMMAND_STATS);
        if (showAllCommandStats != null && showAllCommandStats)
            showAllCommandStats();
        Boolean showAllUpgrades = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_SHOW_ALL_UPGRADES);
        if (showAllUpgrades != null && showAllUpgrades)
            showAllUpgrades();
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
        List<DODInstance> newInstances = instanceDAO.selectAll(upgrades);
        
        //Update checked instances
        for (int i=0; i < newInstances.size(); i++) {
            for (int j=0; j < instances.size(); j++) {
                if (newInstances.get(i).equals(instances.get(j)) && instances.get(j).isChecked()) {
                    newInstances.get(i).setChecked(true);
                    break;
                }
            }
        }
        instances = newInstances;
        
        //Get command stats
        commandStats = statsDAO.selectCommandStats();
        
        //Get job stats
        jobStats = statsDAO.selectJobStats();
        
        //Refresh tree
        Tree tree = (Tree) getFellow("overviewTree");
        int activePage = 0;
        if (tree.getMold().equals("paging")) {
            activePage = tree.getActivePage();
        }
        //Set the new instances
        tree.setModel(OverviewTreeModel.getInstance(instances, tree));
        try {
            if (tree.getMold().equals("paging")) {
                tree.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        //Set the new upgrades
        Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
        ((UpgradesListModel)upgradesGrid.getModel()).setUpgrades(upgrades);
        
        //Set the new command stats
        Grid commandStatsGrid = (Grid) getFellow("commandStatsGrid");
        ((CommandStatsModel)commandStatsGrid.getModel()).setCommandStats(commandStats);
        
        //Set the new job stats
        Grid jobStatsGrid = (Grid) getFellow("jobStatsGrid");
        ((JobStatsModel)jobStatsGrid.getModel()).setJobStats(jobStats);

        displayOrHideAreas();
    }
    
    /**
     * Checks all the instances for a group operation
     */
    public void checkAll() {
        Checkbox checkAll = (Checkbox)getFellow("checkAll");
        Tree tree = (Tree) getFellow("overviewTree");
        ((OverviewTreeModel)tree.getModel()).checkAll((OverviewTreeNode)tree.getModel().getRoot(), checkAll.isChecked());
        List<DODInstance> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        
        if (checked.size() > 0) {
            ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("maintainAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("startupAllBtn")).setZclass("button");
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setZclass("button");
            ((Toolbarbutton) getFellow("backupAllBtn")).setZclass("button");
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setZclass("button");
            ((Toolbarbutton) getFellow("maintainAllBtn")).setZclass("button");
        }
        else {
            ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("maintainAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("startupAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("backupAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("maintainAllBtn")).setZclass("buttonDisabled");
        }
        
        //Re-render the tree
        tree.setModel(tree.getModel());
    }
    
    /**
     * Startup all selected instances. It checks that instances are not awaiting approval or running (busy and unknown included).
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void startupAll() {
        boolean error = false;
        Tree tree = (Tree) getFellow("overviewTree");
        List<DODInstance> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)
                    && !instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)
                    && !instance.getState().equals(DODConstants.INSTANCE_STATE_BUSY)
                    && !instance.getState().equals(DODConstants.INSTANCE_STATE_UNKNOWN)) {
                if (!jobHelper.doStartup(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the tree
        tree.setModel(tree.getModel());
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
        List<DODInstance> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
                if (!jobHelper.doShutdown(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the tree
        tree.setModel(tree.getModel());
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
        List<DODInstance> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
                if (!jobHelper.doBackup(instance, username, 0)){
                    error = true;
                }
            }
        }

        //Re-render the tree
        tree.setModel(tree.getModel());
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Set all selected instances in maintenance state.
     */
    public void maintainAll() {
        boolean error = false;
        Tree tree = (Tree) getFellow("overviewTree");
        List<DODInstance> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            DODInstance clone = instance.clone();
            clone.setState(DODConstants.INSTANCE_STATE_MAINTENANCE);
            if (instanceDAO.update(instance, clone, username) <= 0) {
                error = true;
            }
            else {
                instance.setState(DODConstants.INSTANCE_STATE_MAINTENANCE);
            }
        }
        //Re-render the tree
        tree.setModel(tree.getModel());
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
        List<DODInstance> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && instance.getUpgradeTo() != null && !instance.getUpgradeTo().isEmpty()) {
                if (!jobHelper.doUpgrade(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the tree
        tree.setModel(tree.getModel());
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
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_SHOW_ALL, new Boolean(true));
    }
    
    /**
     * Displays all upgrades in the view
     */
    public void showAllUpgrades() {
        Grid grid = (Grid) getFellow("upgradesGrid");
        grid.setMold("default");
        Foot footer = (Foot) getFellow("footerUpgrades");
        footer.setStyle("display:none");
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_SHOW_ALL_UPGRADES, new Boolean(true));
    }
    
    /**
     * Displays all command stats in the view
     */
    public void showAllCommandStats() {
        Grid grid = (Grid) getFellow("commandStatsGrid");
        grid.setMold("default");
        Foot footer = (Foot) getFellow("commandStatsFooter");
        footer.setStyle("display:none");
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_SHOW_ALL_COMMAND_STATS, new Boolean(true));
    }
    
    /**
     * Displays all job stats in the view
     */
    public void showAllJobStats() {
        Grid grid = (Grid) getFellow("jobStatsGrid");
        grid.setMold("default");
        Foot footer = (Foot) getFellow("jobStatsFooter");
        footer.setStyle("display:none");
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_SHOW_ALL_JOB_STATS, new Boolean(true));
    }
    
    /**
     * Re-renders the tree in order to filter instances.
     */
    public void filterInstances () {
        //Re-render the tree
        Tree tree = (Tree) getFellow("overviewTree");
        //Set the new instances
        tree.setModel(OverviewTreeModel.getInstance(instances, tree));
        //Update group actions
        List<DODInstance> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        
        if (checked.size() > 0) {
            ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("maintainAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("startupAllBtn")).setZclass("button");
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setZclass("button");
            ((Toolbarbutton) getFellow("backupAllBtn")).setZclass("button");
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setZclass("button");
            ((Toolbarbutton) getFellow("maintainAllBtn")).setZclass("button");
        }
        else {
            ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("maintainAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("startupAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("backupAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("maintainAllBtn")).setZclass("buttonDisabled");
        }
        
        displayOrHideAreas();
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