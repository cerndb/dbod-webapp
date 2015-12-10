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
import ch.cern.dbod.db.dao.UpgradeDAO;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.ui.model.OverviewTreeModel;
import ch.cern.dbod.ui.model.OverviewTreeNode;
import ch.cern.dbod.ui.renderer.OverviewTreeRenderer;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.JobHelper;
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
    private UpgradeDAO upgradeDAO;
    /**
     * Instance DAO
     */
    private InstanceDAO instanceDAO;
    /**
     * List of instances. In this case, all the instances in the database.
     */
    private List<Instance> instances;

    /**
     * List of upgrades.
     */
    private List<Upgrade> upgrades;
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
        username = execution.getHeader(CommonConstants.ADFS_LOGIN);
        jobHelper = new JobHelper(true);
        
        //Select upgrades
        upgradeDAO = new UpgradeDAO();
        upgrades = upgradeDAO.selectAll();

        //Select instances
        instanceDAO = new InstanceDAO();
        instances = instanceDAO.selectAll(upgrades);
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    @Override
    public void afterCompose() {
        //Get filters for instances from session
        String filterDbName = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_DB_NAME);
        if (filterDbName != null && !filterDbName.isEmpty()) {
            ((Textbox) getFellow("dbNameFilter")).setValue(filterDbName);
        }
        String filterHost = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_HOST);
        if (filterHost != null && !filterHost.isEmpty()) {
            ((Textbox) getFellow("hostFilter")).setValue(filterHost);
        }
        String filterUsername = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_USERNAME);
        if (filterUsername != null && !filterUsername.isEmpty()) {
            ((Textbox) getFellow("usernameFilter")).setValue(filterUsername);
        }
        String filterEGroup = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_E_GROUP);
        if (filterEGroup != null && !filterEGroup.isEmpty()) {
            ((Textbox) getFellow("eGroupFilter")).setValue(filterEGroup);
        }
        Integer filterCategory = (Integer) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_CATEGORY);
        if (filterCategory != null) {
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(filterCategory.intValue());
        }
        else {
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(0);
        }
        String filterProject = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_PROJECT);
        if (filterProject != null && !filterProject.isEmpty()) {
            ((Textbox) getFellow("projectFilter")).setValue(filterProject);
        }
        Integer filterDbType = (Integer) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_DB_TYPE);
        if (filterDbType != null) {
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(filterDbType.intValue());
        }
        else{
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(0);
        }
        Integer filterActions = (Integer) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_ACTIONS);
        if (filterActions != null) {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(filterActions.intValue());
        }
        else {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(0);
        }
        
        //Instances tree
        Tree overviewTree = (Tree) getFellow("overviewTree");
        overviewTree.setModel(new OverviewTreeModel(instances, overviewTree));
        overviewTree.setItemRenderer(new OverviewTreeRenderer(true));

        displayOrHideAreas();
        
        //Get show all from session
        Boolean showAll = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL);
        if (showAll != null){
            showAll(showAll);
        }
        else{
            showAll(false);
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
    }

    /**
     * Refreshes the list of instances.
     */
    public void refreshInstances(){
        //Get upgrades
        upgrades = upgradeDAO.selectAll();
        
        //Get instances
        List<Instance> newInstances = instanceDAO.selectAll(upgrades);
        
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
            Instance instance = (Instance) node.getData();
            if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL)
                    && !instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                    && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                    && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
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
            showError(CommonConstants.ERROR_COLLECTIVE_ACTION);
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
            Instance instance = (Instance) node.getData();
            if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL) && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)) {
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
            showError(CommonConstants.ERROR_COLLECTIVE_ACTION);
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
            Instance instance = (Instance) node.getData();
            if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
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
            showError(CommonConstants.ERROR_COLLECTIVE_ACTION);
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
            Instance instance = (Instance) node.getData();
            Instance clone = instance.clone();
            clone.setState(CommonConstants.INSTANCE_STATE_MAINTENANCE);
            if (instanceDAO.update(instance, clone, username) <= 0) {
                error = true;
            }
            else {
                instance.setState(CommonConstants.INSTANCE_STATE_MAINTENANCE);
                //Reload node
                model.updateNode(node);
            }
        }
        //Show error if any
        if (error)
            showError(CommonConstants.ERROR_COLLECTIVE_ACTION);
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
            Instance instance = (Instance) node.getData();
            if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL) && instance.getUpgradeTo() != null && !instance.getUpgradeTo().isEmpty()) {
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
            showError(CommonConstants.ERROR_COLLECTIVE_ACTION);
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
            tree.setPageSize(20);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL, show);
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
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_ACTIONS, new Integer(((Combobox) getFellow("actionFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_CATEGORY, new Integer(((Combobox) getFellow("categoryFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_DB_NAME, ((Textbox) getFellow("dbNameFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_DB_TYPE, new Integer(((Combobox) getFellow("dbTypeFilter")).getSelectedIndex()));
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_E_GROUP, ((Textbox) getFellow("eGroupFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_HOST, ((Textbox) getFellow("hostFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_PROJECT, ((Textbox) getFellow("projectFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_USERNAME, ((Textbox) getFellow("usernameFilter")).getValue());
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