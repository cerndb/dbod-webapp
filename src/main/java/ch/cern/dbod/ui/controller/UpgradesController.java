/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;

import ch.cern.dbod.db.dao.UpgradeDAO;
import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.ui.model.UpgradesListModel;
import ch.cern.dbod.ui.renderer.UpgradesGridRenderer;
import ch.cern.dbod.util.CommonConstants;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.*;

/**
 * Controller for the admin overview of instances. It allows the admins to manage every instance.
 * @author Jose Andres Cordero Benitez
 */
public class UpgradesController extends Vbox implements BeforeCompose, AfterCompose{
    /**
     * Upgrade DAO
     */
    private UpgradeDAO upgradeDAO;
    /**
     * List of upgrades.
     */
    private Map<String, Upgrade> upgrades;

    /**
     * Method executed before the page is composed. Obtains instances from DB.
     */
    @Override
    public void beforeCompose() {
        //Select upgrades
        upgradeDAO = new UpgradeDAO();
        upgrades = upgradeDAO.selectAll();
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    @Override
    public void afterCompose() {
        //Upgrades grid
        Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
        upgradesGrid.setModel(new UpgradesListModel((List)upgrades.values()));
        upgradesGrid.setRowRenderer(new UpgradesGridRenderer(upgradeDAO));
        
        displayOrHideAreas();
        
        Boolean showAllUpgrades = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL_UPGRADES);
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
    }

    /**
     * Refreshes the list of instances.
     */
    public void refreshInstances(){
        //Get upgrades
        upgrades = upgradeDAO.selectAll();
        
        int activePage = 0;
        
        //Set the new upgrades
        Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
        if (upgradesGrid.getMold().equals("paging")) {
            activePage = upgradesGrid.getActivePage();
        }
        if (upgrades != null && upgrades.size() > 0) {
            if (upgradesGrid.getModel() != null) {
                ((UpgradesListModel)upgradesGrid.getModel()).setUpgrades((List)upgrades.values());
            }
            else {
                upgradesGrid.setModel(new UpgradesListModel((List)upgrades.values()));
                upgradesGrid.setRowRenderer(new UpgradesGridRenderer(upgradeDAO));
            }
        }
        try {
            if (upgradesGrid.getMold().equals("paging")) {
                upgradesGrid.setActivePage(activePage);
            }
        }
        catch (WrongValueException ex) {}
        
        displayOrHideAreas();
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
            Logger.getLogger(UpgradesController.class.getName()).log(Level.SEVERE, "ERROR OPENING ADD UPGRADE WINDOW", ex);
            showError(CommonConstants.ERROR_OPENING_ADD_UPGRADE);
        }
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
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL_UPGRADES, show);
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
            Logger.getLogger(UpgradesController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}