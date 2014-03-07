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
import ch.cern.dod.ui.renderer.DestroyGridRenderer;
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
     * List of instances to be destroyed.
     */
    private List<DODInstance> toDestroy;
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
    @Override
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
        toDestroy = instanceDAO.selectToDestroy();
        
        //Select stats
        statsDAO = new DODStatsDAO();
        commandStats = statsDAO.selectCommandStats();
        jobStats = statsDAO.selectJobStats();
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    @Override
    public void afterCompose() {
        //Get filters for instances from session
        String filterDbName = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_DB_NAME);
        if (filterDbName != null && !filterDbName.isEmpty()) {
            ((Textbox) getFellow("dbNameFilter")).setValue(filterDbName);
        }
        String filterHost = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_HOST);
        if (filterHost != null && !filterHost.isEmpty()) {
            ((Textbox) getFellow("hostFilter")).setValue(filterHost);
        }
        String filterUsername = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_USERNAME);
        if (filterUsername != null && !filterUsername.isEmpty()) {
            ((Textbox) getFellow("usernameFilter")).setValue(filterUsername);
        }
        String filterEGroup = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_E_GROUP);
        if (filterEGroup != null && !filterEGroup.isEmpty()) {
            ((Textbox) getFellow("eGroupFilter")).setValue(filterEGroup);
        }
        Integer filterCategory = (Integer) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_CATEGORY);
        if (filterCategory != null) {
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(filterCategory.intValue());
        }
        else {
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(0);
        }
        String filterProject = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_PROJECT);
        if (filterProject != null && !filterProject.isEmpty()) {
            ((Textbox) getFellow("projectFilter")).setValue(filterProject);
        }
        Integer filterDbType = (Integer) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_DB_TYPE);
        if (filterDbType != null) {
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(filterDbType.intValue());
        }
        else{
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(0);
        }
        Integer filterActions = (Integer) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_ACTIONS);
        if (filterActions != null) {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(filterActions.intValue());
        }
        else {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(0);
        }
        
        //Filters for jobs
        String filterJobDbName = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_JOB_DB_NAME);
        if (filterJobDbName != null && !filterJobDbName.isEmpty()) {
            ((Textbox) getFellow("jobStatsDBNameFilter")).setValue(filterJobDbName);
        }
        String filterJobCommandName = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_JOB_COMMAND_NAME);
        if (filterJobCommandName != null && !filterJobCommandName.isEmpty()) {
            ((Textbox) getFellow("jobStatsCommandFilter")).setValue(filterJobCommandName);
        }
        
        //Instances tree
        Tree overviewTree = (Tree) getFellow("overviewTree");
        overviewTree.setModel(new OverviewTreeModel(instances, overviewTree));
        overviewTree.setItemRenderer(new OverviewTreeRenderer(true));
        
        //Upgrades grid
        Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
        upgradesGrid.setModel(new UpgradesListModel(upgrades));
        upgradesGrid.setRowRenderer(new UpgradesGridRenderer(upgradeDAO));
        
        //Destroy grid
        Grid destroyGrid = (Grid) getFellow("destroyGrid");
        destroyGrid.setModel(new DestroyListModel(toDestroy));
        destroyGrid.setRowRenderer(new DestroyGridRenderer(instanceDAO));
        
        //Command stats grid
        Grid commandStatsGrid = (Grid) getFellow("commandStatsGrid");
        commandStatsGrid.setModel(new CommandStatsModel(commandStats));
        commandStatsGrid.setRowRenderer(new CommandStatsRenderer());
        
        //Job stats grid
        Grid jobStatsGrid = (Grid) getFellow("jobStatsGrid");
        jobStatsGrid.setModel(new JobStatsModel(jobStats));
        jobStatsGrid.setRowRenderer(new JobStatsRenderer());
        filterJobStats(); //Filter jobs (there could be values from session)
        filterJobStats(); //Filter jobs (there could be values from session)
        
        displayOrHideAreas();
        
        //Get show all from session
        Boolean showAll = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL);
        if (showAll != null){
            showAll(showAll);
        }
        else{
            showAll(false);
        }
        Boolean showAllToDestroy = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL_TO_DESTROY);
        if (showAllToDestroy != null && showAllToDestroy) {
            showAllToDestroy(showAllToDestroy);
        }
        else {
            showAllToDestroy(false);
        }
        Boolean showAllJobStats = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL_JOB_STATS);
        if (showAllJobStats != null && showAllJobStats) {
            showAllJobStats(showAllJobStats);
        }
        else {
            showAllJobStats(false);
        }
        Boolean showAllCommandStats = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL_COMMAND_STATS);
        if (showAllCommandStats != null && showAllCommandStats) {
            showAllCommandStats(showAllCommandStats);
        }
        else {
            showAllCommandStats(false);
        }
        Boolean showAllUpgrades = (Boolean) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL_UPGRADES);
        if (showAllUpgrades != null && showAllUpgrades) {
            showAllUpgrades(showAllUpgrades);
        }
        else {
            showAllUpgrades(false);
        }
    }
    
    /**
     * Displays or hides certain areas when refreshing instances and upgrades
     */
    private void displayOrHideAreas () {
        if (instances != null && instances.size() > 0) {
            ((Hbox) getFellow("collectiveBtns")).setStyle("display:block");
            ((Tree) getFellow("overviewTree")).setStyle("display:block");
            ((Div) getFellow("emptyInstancesMsg")).setStyle("display:none");
            if (instances.size() > 10) {
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
            if (upgrades.size() > 10) {
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
        
        if (toDestroy != null && toDestroy.size() > 0) {
            ((Grid) getFellow("destroyGrid")).setStyle("display:block");
            ((Div) getFellow("emptyDestroyMsg")).setStyle("display:none");
            if (toDestroy.size() > 10) {
                ((Foot) getFellow("footerDestroy")).setStyle("display:block");
            }
            else {
                ((Foot) getFellow("footerDestroy")).setStyle("display:none");
            }
        }
        else {
            ((Grid) getFellow("destroyGrid")).setStyle("display:none");
            ((Div) getFellow("emptyDestroyMsg")).setStyle("display:block");
            ((Foot) getFellow("footerDestroy")).setStyle("display:none");
        }
        
        if (commandStats != null && commandStats.size() > 0) {
            ((Grid) getFellow("commandStatsGrid")).setStyle("display:block");
            ((Div) getFellow("emptyCommandStatsMsg")).setStyle("display:none");
            if (commandStats.size() > 10) {
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
            if (jobStats.size() > 10) {
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
        
        //Get instances to destroy
        toDestroy = instanceDAO.selectToDestroy();
        
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
        if (instances != null && instances.size() > 0) {
            if (tree.getModel() != null) {
                ((OverviewTreeModel) tree.getModel()).setInstances(instances);
            }
            else {
                tree.setModel(new OverviewTreeModel(instances, tree));
                tree.setItemRenderer(new OverviewTreeRenderer(false));
            }
        }
        try {
            if (tree.getMold().equals("paging")) {
                tree.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        //Set the new instances to be destroyed
        Grid destroyGrid = (Grid) getFellow("destroyGrid");
        if (destroyGrid.getMold().equals("paging")) {
            activePage = destroyGrid.getActivePage();
        }
        if (toDestroy != null && toDestroy.size() > 0) {
            if (destroyGrid.getModel() != null) {
                ((DestroyListModel)destroyGrid.getModel()).setInstances(toDestroy);
            }
            else {
                destroyGrid.setModel(new DestroyListModel(toDestroy));
                destroyGrid.setRowRenderer(new DestroyGridRenderer(instanceDAO));
            }
        }
        try {
            if (destroyGrid.getMold().equals("paging")) {
                destroyGrid.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        //Set the new upgrades
        Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
        if (upgradesGrid.getMold().equals("paging")) {
            activePage = upgradesGrid.getActivePage();
        }
        if (upgrades != null && upgrades.size() > 0) {
            if (upgradesGrid.getModel() != null) {
                ((UpgradesListModel)upgradesGrid.getModel()).setUpgrades(upgrades);
            }
            else {
                upgradesGrid.setModel(new UpgradesListModel(upgrades));
                upgradesGrid.setRowRenderer(new UpgradesGridRenderer(upgradeDAO));
            }
        }
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
        if (commandStats != null && commandStats.size() > 0) {
            if (commandStatsGrid.getModel() != null) {
                ((CommandStatsModel)commandStatsGrid.getModel()).setCommandStats(commandStats);
            }
            else {
                commandStatsGrid.setModel(new CommandStatsModel(commandStats));
                commandStatsGrid.setRowRenderer(new CommandStatsRenderer());
            }
        }
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
        if (jobStats != null && jobStats.size() > 0) {
            if (jobStatsGrid.getModel() != null) {
                ((JobStatsModel)jobStatsGrid.getModel()).setJobStats(jobStats);
            }
            else {
                jobStatsGrid.setModel(new JobStatsModel(jobStats));
                jobStatsGrid.setRowRenderer(new JobStatsRenderer());
            }
        }
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
        ((OverviewTreeModel)tree.getModel()).checkAll((OverviewTreeNode)tree.getModel().getRoot(), checkAll.isChecked());
        List<OverviewTreeNode> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        
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
    }
    
    /**
     * Startup all selected instances. It checks that instances are not awaiting approval or running (busy and unknown included).
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void startupAll() {
        boolean error = false;
        Tree tree = (Tree) getFellow("overviewTree");
        OverviewTreeModel model = (OverviewTreeModel)tree.getModel();
        List<OverviewTreeNode> checked = model.getChecked((OverviewTreeNode)model.getRoot());
        for (int i=0; i<checked.size(); i++) {
            OverviewTreeNode node = checked.get(i);
            DODInstance instance = (DODInstance) node.getData();
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)
                    && !instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)
                    && !instance.getState().equals(DODConstants.INSTANCE_STATE_BUSY)
                    && !instance.getState().equals(DODConstants.INSTANCE_STATE_UNKNOWN)) {
                if (!jobHelper.doStartup(instance, username)){
                    error = true;
                }
                else {
                    //Reload node
                    model.updateNode(node);
                }
            }
        }
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
        OverviewTreeModel model = (OverviewTreeModel)tree.getModel();
        List<OverviewTreeNode> checked = model.getChecked((OverviewTreeNode)model.getRoot());
        for (int i=0; i<checked.size(); i++) {
            OverviewTreeNode node = checked.get(i);
            DODInstance instance = (DODInstance) node.getData();
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
                if (!jobHelper.doShutdown(instance, username)){
                    error = true;
                }
                else {
                    //Reload node
                    model.updateNode(node);
                }
            }
        }
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
        OverviewTreeModel model = (OverviewTreeModel)tree.getModel();
        List<OverviewTreeNode> checked = model.getChecked((OverviewTreeNode)model.getRoot());
        for (int i=0; i<checked.size(); i++) {
            OverviewTreeNode node = checked.get(i);
            DODInstance instance = (DODInstance) node.getData();
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
                if (!jobHelper.doBackup(instance, username)){
                    error = true;
                }
                else {
                    //Reload node
                    model.updateNode(node);
                }
            }
        }
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
        OverviewTreeModel model = (OverviewTreeModel)tree.getModel();
        List<OverviewTreeNode> checked = model.getChecked((OverviewTreeNode)model.getRoot());
        for (int i=0; i<checked.size(); i++) {
            OverviewTreeNode node = checked.get(i);
            DODInstance instance = (DODInstance) node.getData();
            DODInstance clone = instance.clone();
            clone.setState(DODConstants.INSTANCE_STATE_MAINTENANCE);
            if (instanceDAO.update(instance, clone, username) <= 0) {
                error = true;
            }
            else {
                instance.setState(DODConstants.INSTANCE_STATE_MAINTENANCE);
                //Reload node
                model.updateNode(node);
            }
        }
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
        OverviewTreeModel model = (OverviewTreeModel)tree.getModel();
        List<OverviewTreeNode> checked = model.getChecked((OverviewTreeNode)model.getRoot());
        for (int i=0; i<checked.size(); i++) {
            OverviewTreeNode node = checked.get(i);
            DODInstance instance = (DODInstance) node.getData();
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && instance.getUpgradeTo() != null && !instance.getUpgradeTo().isEmpty()) {
                if (!jobHelper.doUpgrade(instance, username)){
                    error = true;
                }
                else {
                    //Reload node
                    model.updateNode(node);
                }
            }
        }
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
     * Displays all instances in the view (or goes back to normal mold)
     * 
     * @param show indicates if all should be displayed or not
     */
    public void showAll(boolean show) {
        Tree tree = (Tree) getFellow("overviewTree");
        Hbox showAll = (Hbox) getFellow("showAll");
        Hbox paging = (Hbox) getFellow("paging");
        if (show) {
            tree.setMold("default");
            showAll.setStyle("display:none");
            paging.setStyle("display:block");
        }
        else {
            tree.setMold("paging");
            tree.setPageSize(10);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL, show);
    }
    
    /**
     * Displays all instances to destroy
     * 
     * @param show indicates if all should be displayed or not
     */
    public void showAllToDestroy(boolean show) {
        Grid grid = (Grid) getFellow("destroyGrid");
        Hbox showAll = (Hbox) getFellow("showAllToDestroy");
        Hbox paging = (Hbox) getFellow("pagingToDestroy");
        if (show) {
            grid.setMold("default");
            showAll.setStyle("display:none");
            paging.setStyle("display:block");
        }
        else {
            grid.setMold("paging");
            grid.setPageSize(10);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL_TO_DESTROY, show);
    }
    
    /**
     * Displays all upgrades in the view
     * 
     * @param show indicates if all should be displayed or not
     */
    public void showAllUpgrades(boolean show) {
        Grid grid = (Grid) getFellow("upgradesGrid");
        Hbox showAll = (Hbox) getFellow("showAllUpgrades");
        Hbox paging = (Hbox) getFellow("pagingUpgrades");
        if (show) {
            grid.setMold("default");
            showAll.setStyle("display:none");
            paging.setStyle("display:block");
        }
        else {
            grid.setMold("paging");
            grid.setPageSize(10);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL_UPGRADES, show);
    }
    
    /**
     * Displays all command stats in the view
     * 
     * @param show indicates if all should be displayed or not
     */
    public void showAllCommandStats(boolean show) {
        Grid grid = (Grid) getFellow("commandStatsGrid");
        Hbox showAll = (Hbox) getFellow("showAllCommandStats");
        Hbox paging = (Hbox) getFellow("pagingCommandStats");
        if (show) {
            grid.setMold("default");
            showAll.setStyle("display:none");
            paging.setStyle("display:block");
        }
        else {
            grid.setMold("paging");
            grid.setPageSize(10);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL_COMMAND_STATS, show);
    }
    
    /**
     * Displays all job stats in the view
     * 
     * @param show indicates if all should be displayed or not
     */
    public void showAllJobStats(boolean show) {
        Grid grid = (Grid) getFellow("jobStatsGrid");
        Hbox showAll = (Hbox) getFellow("showAllJobStats");
        Hbox paging = (Hbox) getFellow("pagingJobStats");
        if (show) {
            grid.setMold("default");
            showAll.setStyle("display:none");
            paging.setStyle("display:block");
        }
        else {
            grid.setMold("paging");
            grid.setPageSize(10);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_SHOW_ALL_JOB_STATS, show);
    }
    
    /**
     * Re-renders the tree in order to filter instances.
     */
    public void filterInstances () {
        //Re-render the tree
        Tree tree = (Tree) getFellow("overviewTree");
        //Set the new instances
        ((OverviewTreeModel) tree.getModel()).setInstances(instances);
        //Update group actions
        List<OverviewTreeNode> checked = ((OverviewTreeModel)tree.getModel()).getChecked((OverviewTreeNode)tree.getModel().getRoot());
        
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
        
        //Set filters on session
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_ACTIONS, new Integer(((Combobox) getFellow("actionFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_CATEGORY, new Integer(((Combobox) getFellow("categoryFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_DB_NAME, ((Textbox) getFellow("dbNameFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_DB_TYPE, new Integer(((Combobox) getFellow("dbTypeFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_E_GROUP, ((Textbox) getFellow("eGroupFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_HOST, ((Textbox) getFellow("hostFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_PROJECT, ((Textbox) getFellow("projectFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_USERNAME, ((Textbox) getFellow("usernameFilter")).getValue());
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
        
        //Set filters on session
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_JOB_COMMAND_NAME, ((Textbox) getFellow("jobStatsCommandFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_ADMIN_FILTER_JOB_DB_NAME, ((Textbox) getFellow("jobStatsDBNameFilter")).getValue());
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
        }
        catch (SuspendNotAllowedException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}