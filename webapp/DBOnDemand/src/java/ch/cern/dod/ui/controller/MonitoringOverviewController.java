package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODMonitoringDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODMetric;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.util.DODConstants;
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
 * Controller for the monitoring window.
 * @author Daniel Gomez Blanco
 */
public class MonitoringOverviewController extends Hbox  implements BeforeCompose, AfterCompose{

    /**
     * Instance being managed at the moment
     */
    private DODInstance instance;
    /**
     * DAO to access monitoring
     */
    private DODMonitoringDAO dao;
    
    /**
     * Method called before composing the page, it instantiates variables.
     */
    public void beforeCompose() {
        //Initialize instance and create job helper
        String dbName = (String) Executions.getCurrent().getParameter(DODConstants.INSTANCE);
        DODUpgradeDAO upgradeDAO = new DODUpgradeDAO();
        List<DODUpgrade> upgrades = upgradeDAO.selectAll();
        DODInstanceDAO instanceDAO = new DODInstanceDAO();
        this.instance = instanceDAO.selectByDbName(dbName, upgrades);
        dao = new DODMonitoringDAO();
    }
    
    public void afterCompose() {
        //Set title
        ((Label) getFellow("monitoringTitle")).setValue(Labels.getLabel(DODConstants.LABEL_MONITORING_OVERVIEW_TITLE) + " " + instance.getDbName());
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
        DODMetric[] metrics;
        if (instance.getDbType().equals(DODConstants.DB_TYPE_MYSQL)) {
             metrics = DODConstants.MYSQL_OVERVIEW_METRICS;
        }
        else if (instance.getDbType().equals(DODConstants.DB_TYPE_ORACLE)) {
            metrics = DODConstants.ORACLE_OVERVIEW_METRICS;
        }
        else {
            metrics = new DODMetric[0];
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
                labelLeft.setValue(metrics[i].getName() + " [" + metrics[i].getUnit() + "]");
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
                labelRight.setValue(metrics[i+1].getName() + " [" + metrics[i+1].getUnit() + "]");
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
                labelLeft.setValue(metrics[metrics.length - 1].getName() + " [" + metrics[metrics.length - 1].getUnit() + "]");
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
        } catch (InterruptedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}