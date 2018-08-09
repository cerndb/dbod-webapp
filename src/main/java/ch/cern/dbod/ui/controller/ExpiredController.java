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
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.ui.model.DestroyListModel;
import ch.cern.dbod.ui.renderer.DestroyGridRenderer;
import ch.cern.dbod.util.CommonConstants;
import java.util.List;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.*;

/**
 * Controller for the admin overview of instances. It allows the admins to manage every instance.
 * @author Jose Andres Cordero Benitez
 */
public class ExpiredController extends Vbox implements BeforeCompose, AfterCompose{
    /**
     * Instance DAO
     */
    private InstanceDAO instanceDAO;
    /**
     * List of instances to be destroyed.
     */
    private List<Instance> toDestroy;
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
        //Select instances
        instanceDAO = new InstanceDAO();
        toDestroy = instanceDAO.selectToDestroy(username);
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    @Override
    public void afterCompose() {
        //Destroy grid
        Grid destroyGrid = (Grid) getFellow("destroyGrid");
        destroyGrid.setModel(new DestroyListModel(toDestroy));
        destroyGrid.setRowRenderer(new DestroyGridRenderer(instanceDAO));
        
        displayOrHideAreas();
        
        Boolean showAllToDestroy = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL_TO_DESTROY);
        if (showAllToDestroy != null && showAllToDestroy) {
            showAllToDestroy(showAllToDestroy);
        }
        else {
            showAllToDestroy(false);
        }
    }
    
    /**
     * Displays or hides certain areas when refreshing instances and upgrades
     */
    private void displayOrHideAreas () {
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
    }

    /**
     * Refreshes the list of instances.
     */
    public void refreshInstances(){
        //Get instances to destroy
        toDestroy = instanceDAO.selectToDestroy(username);
        
        int activePage = 0;
        
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
        
        displayOrHideAreas();
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
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL_TO_DESTROY, show);
    }
}