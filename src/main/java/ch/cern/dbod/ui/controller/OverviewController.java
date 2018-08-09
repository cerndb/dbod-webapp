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
import ch.cern.dbod.ui.renderer.OverviewTreeRenderer;
import ch.cern.dbod.util.CommonConstants;
import java.util.List;
import java.util.Map;
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
     * List of instances. In this case, all the instances in the database.
     */
    private List<Instance> instances;
    /**
     * List of upgrades.
     */
    private Map<String, Upgrade> upgrades;
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
            ((Combobox) getFellow("categoryFilter")).setSelectedIndex(filterCategory);
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
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(filterDbType);
        }
        else{
            ((Combobox) getFellow("dbTypeFilter")).setSelectedIndex(0);
        }
        Integer filterActions = (Integer) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_ACTIONS);
        if (filterActions != null) {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(filterActions);
        }
        else {
            ((Combobox) getFellow("actionFilter")).setSelectedIndex(0);
        }

        //Overview tree
        if (instances != null && instances.size() > 0) {
            Tree overviewTree = (Tree) getFellow("overviewTree");
            overviewTree.setModel(new OverviewTreeModel(instances, overviewTree));
            overviewTree.setItemRenderer(new OverviewTreeRenderer(false));
        }
        
        displayOrHideAreas();
        
        //Get show all from session
        Boolean showAll = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_USER_SHOW_ALL);
        if (showAll != null) {
            showAll(showAll);
        }
        else {
            showAll(false);
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
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_ACTIONS, ((Combobox) getFellow("actionFilter")).getSelectedIndex());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_CATEGORY, ((Combobox) getFellow("categoryFilter")).getSelectedIndex());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_DB_NAME, ((Textbox) getFellow("dbNameFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_DB_TYPE, ((Combobox) getFellow("dbTypeFilter")).getSelectedIndex());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_E_GROUP, ((Textbox) getFellow("eGroupFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_HOST, ((Textbox) getFellow("hostFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_PROJECT, ((Textbox) getFellow("projectFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_USER_FILTER_USERNAME, ((Textbox) getFellow("usernameFilter")).getValue());
    }
}
