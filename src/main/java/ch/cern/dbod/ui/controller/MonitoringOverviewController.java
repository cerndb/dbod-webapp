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
import ch.cern.dbod.db.dao.MonitoringDAO;
import ch.cern.dbod.db.dao.UpgradeDAO;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Metric;
import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.util.CommonConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

/**
 * Controller for the monitoring window in the overview page, where all the
 * important metrics are displayed.
 * @author Daniel Gomez Blanco
 */
public class MonitoringOverviewController extends Hbox  implements BeforeCompose, AfterCompose{

    /**
     * Instance being managed at the moment
     */
    private Instance instance;
    /**
     * DAO to access monitoring
     */
    private MonitoringDAO dao;
    
    /**
     * Method called before composing the page, it instantiates variables.
     */
    @Override
    public void beforeCompose() {
        //Initialize instance and create job helper
        String dbName = (String) Executions.getCurrent().getParameter(CommonConstants.INSTANCE);
        UpgradeDAO upgradeDAO = new UpgradeDAO();
        List<Upgrade> upgrades = upgradeDAO.selectAll();
        InstanceDAO instanceDAO = new InstanceDAO();
        this.instance = instanceDAO.selectByDbName(dbName, upgrades);
        dao = new MonitoringDAO();
    }
    
    @Override
    public void afterCompose() {
        //Set title
        ((Label) getFellow("monitoringTitle")).setValue(Labels.getLabel(CommonConstants.LABEL_MONITORING_OVERVIEW_TITLE) + " " + instance.getDbName());
        ((Groupbox) getFellow("container")).setOpen(true);
        Events.echoEvent("onOpen", (Groupbox) getFellow("container"), null);
    }
    
    /**
     * Method called after composing the page, it load metrics.
     */
    public void renderCharts() {
        //Detach loading GIF
        ((Image) getFellow("loading")).detach();
        String javascript = "";
        //Compose graphs
        Metric[] metrics;
        switch (instance.getDbType()) {
            case CommonConstants.DB_TYPE_MYSQL:
                metrics = CommonConstants.MYSQL_OVERVIEW_METRICS;
                break;
            case CommonConstants.DB_TYPE_PG:
                metrics = CommonConstants.PG_OVERVIEW_METRICS;
                break;
            default:
                metrics = new Metric[0];
                break;
        }
        
        for (int i=0; i+1 < metrics.length; i = i+2) {
            //Hbox for two graphs
            Hbox hbox = new Hbox();
            hbox.setWidth("1200px");
            hbox.setStyle("margin-bottom:10px");
            //Vbox for first graph
            Vbox vboxLeft = new Vbox();
            vboxLeft.setAlign("center");
            vboxLeft.setWidth("600px");
            Label labelLeft = new Label();
            if (metrics[i].getUnit() != null) {
                labelLeft.setValue(metrics[i].getName() + " (" + metrics[i].getUnit() + ")");
            }
            else {
                labelLeft.setValue(metrics[i].getName());
            }
            labelLeft.setSclass("titleSmall");
            vboxLeft.appendChild(labelLeft);
            Html graphLeft = new Html();
            graphLeft.setContent("<div id=\"graphDiv" + metrics[i].getCode() +"\" style=\"width:560px; height:300px\" class=\"preloader\"></div>");
            vboxLeft.appendChild(graphLeft);
            javascript += "drawGraph(" + dao.selectJSONData(instance, "", metrics[i], 14) + ", 'graphDiv" + metrics[i].getCode() +"');";
            hbox.appendChild(vboxLeft);
            //Vbox for second graph
            Vbox vboxRight = new Vbox();
            vboxRight.setAlign("center");
            vboxRight.setWidth("600px");
            Label labelRight = new Label();
            if (metrics[i+1].getUnit() != null) {
                labelRight.setValue(metrics[i+1].getName() + " (" + metrics[i+1].getUnit() + ")");
            }
            else {
                labelRight.setValue(metrics[i+1].getName());
            }
            labelRight.setSclass("titleSmall");
            vboxRight.appendChild(labelRight);
            Html graphRight = new Html();
            graphRight.setContent("<div id=\"graphDiv" + metrics[i+1].getCode() +"\" style=\"width:560px; height:300px\" class=\"preloader\"></div>");
            vboxRight.appendChild(graphRight);
            javascript += "drawGraph(" + dao.selectJSONData(instance, "", metrics[i+1], 14) + ", 'graphDiv" + metrics[i+1].getCode() +"');";
            hbox.appendChild(vboxRight);
            //Add to container
            ((Vbox) getFellow("chartContainer")).appendChild(hbox);
        }
        //If array has an odd number of elements add one more in the center
        if  (metrics.length % 2 == 1) {
            //Vbox for firsst graph
            Vbox vbox = new Vbox();
            vbox.setAlign("center");
            vbox.setWidth("1200px");
            Label labelLeft = new Label();
            if (metrics[metrics.length - 1].getUnit() != null) {
                labelLeft.setValue(metrics[metrics.length - 1].getName() + " (" + metrics[metrics.length - 1].getUnit() + ")");
            }
            else {
                labelLeft.setValue(metrics[metrics.length - 1].getName());
            }
            labelLeft.setSclass("titleSmall");
            vbox.appendChild(labelLeft);
            Html graphLeft = new Html();
            graphLeft.setContent("<div id=\"graphDiv" + metrics[metrics.length - 1].getCode() +"\" style=\"width:560px; height:300px\" class=\"preloader\"></div>");
            vbox.appendChild(graphLeft);
            javascript += "drawGraph(" + dao.selectJSONData(instance, "", metrics[metrics.length - 1], 14)
                                        + ", 'graphDiv" + metrics[metrics.length - 1].getCode() +"');";
            ((Vbox) getFellow("chartContainer")).appendChild(vbox);
        }
        //Eval javascript
        Clients.evalJavaScript(javascript);
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
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}