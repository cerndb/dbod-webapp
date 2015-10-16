/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;

import ch.cern.dbod.db.dao.InstanceDAO;
import ch.cern.dbod.db.dao.StatsDAO;
import ch.cern.dbod.db.dao.UpgradeDAO;
import ch.cern.dbod.db.entity.CommandStat;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.JobStat;
import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.ui.model.CommandStatsModel;
import ch.cern.dbod.ui.model.JobStatsModel;
import ch.cern.dbod.ui.model.OverviewTreeModel;
import ch.cern.dbod.ui.renderer.CommandStatsRenderer;
import ch.cern.dbod.ui.renderer.JobStatsRenderer;
import ch.cern.dbod.ui.renderer.OverviewTreeRenderer;
import ch.cern.dbod.util.CommonConstants;
import java.util.List;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.*;

/**
 * Controller for the overview page. Where all the instances are presented
 * to the user.
 * @author Daniel Gomez Blanco
 */
public class OverviewController extends Vbox implements BeforeCompose, AfterCompose{
    /**
     * Upgrade DAO
     */
    private UpgradeDAO upgradeDAO;
    /**
     * Instance DAO
     */
    private InstanceDAO instanceDAO;
    /**
     * Stats DAO
     */
    private StatsDAO statsDAO;
    /**
     * List of instances. In this case, all the instances in the database.
     */
    private List<Instance> instances;
    /**
     * List of upgrades.
     */
    private List<Upgrade> upgrades;
    /**
     * List of command stats.
     */
    private List<CommandStat> commandStats;
    /**
     * List of job stats.
     */
    private List<JobStat> jobStats;
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
    @Override
    public void beforeCompose() {
        //Get username and groups
        Execution execution = Executions.getCurrent();
        username = execution.getHeader(CommonConstants.ADFS_LOGIN);
        eGroups = execution.getHeader(CommonConstants.ADFS_GROUP);
        
        //Get upgrades
        upgradeDAO =  new UpgradeDAO();
        upgrades = upgradeDAO.selectAll();
        
        //Get instances
        instanceDAO = new InstanceDAO();
        instances = instanceDAO.selectByUserNameAndEGroups(username, eGroups, upgrades);
        
        //Select stats
        statsDAO = new StatsDAO();
        commandStats = statsDAO.selectCommandStats(instances);
        jobStats = statsDAO.selectJobStats(instances);
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    @Override
    public void afterCompose() {
        //Get filters for instances from session
        String filterDbName = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_DB_NAME);
        if (filterDbName != null && !filterDbName.isEmpty()) {
            ((Textbox) getFellow("dbNameFilter")).setValue(filterDbName);
        }
        String filterHost = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_HOST);
        if (filterHost != null && !filterHost.isEmpty()) {
            ((Textbox) getFellow("hostFilter")).setValue(filterHost);
        }
        String filterUsername = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_USERNAME);
        if (filterUsername != null && !filterUsername.isEmpty()) {
            ((Textbox) getFellow("usernameFilter")).setValue(filterUsername);
        }
        String filterEGroup = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_E_GROUP);
        if (filterEGroup != null && !filterEGroup.isEmpty()) {
            ((Textbox) getFellow("eGroupFilter")).setValue(filterEGroup);
        }
        Integer filterCategory = (Integer) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_CATEGORY);
        if (filterCategory != null) {
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(filterCategory.intValue());
        }
        else {
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(0);
        }
        String filterProject = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_PROJECT);
        if (filterProject != null && !filterProject.isEmpty()) {
            ((Textbox) getFellow("projectFilter")).setValue(filterProject);
        }
        Integer filterDbType = (Integer) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_DB_TYPE);
        if (filterDbType != null) {
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(filterDbType.intValue());
        }
        else{
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(0);
        }
        Integer filterActions = (Integer) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_ACTIONS);
        if (filterActions != null) {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(filterActions.intValue());
        }
        else {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(0);
        }
        
        //Filters for jobs
        String filterJobDbName = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_JOB_DB_NAME);
        if (filterJobDbName != null && !filterJobDbName.isEmpty()) {
            ((Textbox) getFellow("jobStatsDBNameFilter")).setValue(filterJobDbName);
        }
        String filterJobCommandName = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_JOB_COMMAND_NAME);
        if (filterJobCommandName != null && !filterJobCommandName.isEmpty()) {
            ((Textbox) getFellow("jobStatsCommandFilter")).setValue(filterJobCommandName);
        }
        
        //Overview tree
        if (instances != null && instances.size() > 0) {
            Tree overviewTree = (Tree) getFellow("overviewTree");
            overviewTree.setModel(new OverviewTreeModel(instances, overviewTree));
            overviewTree.setItemRenderer(new OverviewTreeRenderer(false));
        }
        
        //Command stats grid
        Grid commandStatsGrid = (Grid) getFellow("commandStatsGrid");
        commandStatsGrid.setModel(new CommandStatsModel(commandStats));
        commandStatsGrid.setRowRenderer(new CommandStatsRenderer());
        
        //Job stats grid
        Grid jobStatsGrid = (Grid) getFellow("jobStatsGrid");
        jobStatsGrid.setModel(new JobStatsModel(jobStats));
        jobStatsGrid.setRowRenderer(new JobStatsRenderer());
        filterJobStats(); //Filter jobs (there could be values from session)
        
        displayOrHideAreas();
        
        //Get show all from session
        Boolean showAll = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_SHOW_ALL);
        if (showAll != null) {
            showAll(showAll);
        }
        else {
            showAll(false);
        }
        Boolean showAllJobStats = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_SHOW_ALL_JOB_STATS);
        if (showAllJobStats != null){
            showAllJobStats(showAllJobStats);
        }
        else {
            showAllJobStats(false);
        }
        Boolean showAllCommandStats = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_SHOW_ALL_COMMAND_STATS);
        if (showAllCommandStats != null) {
            showAllCommandStats(showAllCommandStats);
        }
        else {
            showAllCommandStats(false);
        }
    }
    
    /**
     * Displays or hides certain areas when refreshing instances and upgrades
     */
    private void displayOrHideAreas () {
        if (instances != null && instances.size() > 0) {
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
            ((Tree) getFellow("overviewTree")).setStyle("display:none");
            ((Div) getFellow("emptyInstancesMsg")).setStyle("display:block");
            ((Treefoot) getFellow("footer")).setStyle("display:none");
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
        instances = instanceDAO.selectByUserNameAndEGroups(username, eGroups, upgrades);
        //Get command stats
        commandStats = statsDAO.selectCommandStats(instances);
        //Get job stats
        jobStats = statsDAO.selectJobStats(instances);

        //Set the new instances
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
     * Displays all instances in the view (or goes back to normal mold)
     * 
     * @param show indicates if all the rows should be displayed or not
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
            tree.setPageSize(20);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_SHOW_ALL, show);
    }
    
    /**
     * Filters instances according to the content on the filter fields.
     */
    public void filterInstances () {
        //Re-render the tree
        Tree tree = (Tree) getFellow("overviewTree");
        //Set the new instances
        ((OverviewTreeModel) tree.getModel()).setInstances(instances);
        //Hide fields
        displayOrHideAreas();
        
        //Set filters on session
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_ACTIONS, new Integer(((Combobox) getFellow("actionFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_CATEGORY, new Integer(((Combobox) getFellow("categoryFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_DB_NAME, ((Textbox) getFellow("dbNameFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_DB_TYPE, new Integer(((Combobox) getFellow("dbTypeFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_E_GROUP, ((Textbox) getFellow("eGroupFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_HOST, ((Textbox) getFellow("hostFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_PROJECT, ((Textbox) getFellow("projectFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_USERNAME, ((Textbox) getFellow("usernameFilter")).getValue());
    }
    
    /**
     * Displays all command stats in the view
     * 
     * @param show indicates if all the rows should be displayed or not
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
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_SHOW_ALL_COMMAND_STATS, show);
    }
    
    /**
     * Displays all job stats in the view
     * 
     * @param show indicates if all the rows should be displayed or not
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
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_SHOW_ALL_JOB_STATS, show);
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
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_JOB_COMMAND_NAME, ((Textbox) getFellow("jobStatsCommandFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_JOB_DB_NAME, ((Textbox) getFellow("jobStatsDBNameFilter")).getValue());
    }
}