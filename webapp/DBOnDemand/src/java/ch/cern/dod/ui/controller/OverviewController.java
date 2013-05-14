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
import org.zkoss.zk.ui.Sessions;
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
        //Get filters for instances from session
        String filterDbName = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_DB_NAME);
        if (filterDbName != null && !filterDbName.isEmpty()) {
            ((Textbox) getFellow("dbNameFilter")).setValue(filterDbName);
        }
        String filterHost = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_HOST);
        if (filterHost != null && !filterHost.isEmpty()) {
            ((Textbox) getFellow("hostFilter")).setValue(filterHost);
        }
        String filterUsername = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_USERNAME);
        if (filterUsername != null && !filterUsername.isEmpty()) {
            ((Textbox) getFellow("usernameFilter")).setValue(filterUsername);
        }
        String filterEGroup = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_E_GROUP);
        if (filterEGroup != null && !filterEGroup.isEmpty()) {
            ((Textbox) getFellow("eGroupFilter")).setValue(filterEGroup);
        }
        Integer filterCategory = (Integer) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_CATEGORY);
        if (filterCategory != null) {
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(filterCategory.intValue());
        }
        else {
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(0);
        }
        String filterProject = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_PROJECT);
        if (filterProject != null && !filterProject.isEmpty()) {
            ((Textbox) getFellow("projectFilter")).setValue(filterProject);
        }
        Integer filterDbType = (Integer) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_DB_TYPE);
        if (filterDbType != null) {
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(filterDbType.intValue());
        }
        else{
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(0);
        }
        Integer filterActions = (Integer) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_ACTIONS);
        if (filterActions != null) {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(filterActions.intValue());
        }
        else {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(0);
        }
        
        //Filters for jobs
        String filterJobDbName = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_JOB_DB_NAME);
        if (filterJobDbName != null && !filterJobDbName.isEmpty()) {
            ((Textbox) getFellow("jobStatsDBNameFilter")).setValue(filterJobDbName);
        }
        String filterJobCommandName = (String) Sessions.getCurrent().getAttribute(DODConstants.ATTRIBUTE_USER_FILTER_JOB_COMMAND_NAME);
        if (filterJobCommandName != null && !filterJobCommandName.isEmpty()) {
            ((Textbox) getFellow("jobStatsCommandFilter")).setValue(filterJobCommandName);
        }
        
        //Overview tree
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
        filterJobStats(); //Filter jobs (there could be values from session)
        
        displayOrHideAreas();
        
        //Get show all from session
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
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_SHOW_ALL, new Boolean(true));
    }
    
    /**
     * Filters instances according to the content on the filter fields.
     */
    public void filterInstances () {
        //Re-render the tree
        Tree tree = (Tree) getFellow("overviewTree");
        tree.setModel(OverviewTreeModel.getInstance(instances, tree));
        displayOrHideAreas();
        
        //Set filters on session
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_ACTIONS, new Integer(((Combobox) getFellow("actionFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_CATEGORY, new Integer(((Combobox) getFellow("categoryFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_DB_NAME, ((Textbox) getFellow("dbNameFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_DB_TYPE, new Integer(((Combobox) getFellow("dbTypeFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_E_GROUP, ((Textbox) getFellow("eGroupFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_HOST, ((Textbox) getFellow("hostFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_PROJECT, ((Textbox) getFellow("projectFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_USERNAME, ((Textbox) getFellow("usernameFilter")).getValue());
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
     * Re-renders the grid in order to filter job stats.
     */
    public void filterJobStats () {
        //Re-render the grid
        Grid grid = (Grid) getFellow("jobStatsGrid");
        ((JobStatsModel) grid.getModel()).filterJobStats(((Textbox) getFellow("jobStatsDBNameFilter")).getValue(),
                                                            ((Textbox) getFellow("jobStatsCommandFilter")).getValue());
        
        displayOrHideAreas();
        
        //Set filters on session
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_JOB_COMMAND_NAME, ((Textbox) getFellow("jobStatsCommandFilter")).getValue());
        Sessions.getCurrent().setAttribute(DODConstants.ATTRIBUTE_USER_FILTER_JOB_DB_NAME, ((Textbox) getFellow("jobStatsDBNameFilter")).getValue());
    }
}