package ch.cern.dod.ui.controller;

import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.MonitoringHelper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

/**
 * Controller for the monitoring window.
 * @author Daniel Gomez Blanco
 */
public class MonitoringOverviewController extends Hbox  implements AfterCompose, BeforeCompose{

    /**
     * Instance being managed at the moment
     */
    private String instance;
    /**
     * Helper to access monitoring
     */
    private MonitoringHelper helper;
    
    /**
     * Method called before composing the page, it instantiates variables.
     */
    public void beforeCompose() {
        //Initialize instance and create job helper
        this.instance = (String) Executions.getCurrent().getParameter(DODConstants.INSTANCE);
        helper = new MonitoringHelper();
    }
    
    /**
     * Method called after composing the page, it load metrics.
     */
    public void afterCompose() {
        //Set title
        ((Label) getFellow("monitoringTitle")).setValue(Labels.getLabel(DODConstants.LABEL_MONITORING_OVERVIEW_TITLE) + " " + instance);
        String javascript = "";
        
        //Compose graphs
        for (int i=0; i+1 < DODConstants.MYSQL_OVERVIEW_METRICS.length; i = i+2) {
            //Hbox for two graphs
            Hbox hbox = new Hbox();
            hbox.setWidth("1200px");
            hbox.setStyle("margin-bottom:10px");
            //Vbox for first graph
            Vbox vboxLeft = new Vbox();
            vboxLeft.setAlign("center");
            vboxLeft.setWidth("600px");
            Label labelLeft = new Label(DODConstants.MYSQL_OVERVIEW_METRICS[i][1]);
            labelLeft.setSclass("titleSmall");
            vboxLeft.appendChild(labelLeft);
            Html graphLeft = new Html();
            graphLeft.setContent("<div id=\"graphDiv" + DODConstants.MYSQL_OVERVIEW_METRICS[i][1] +"\" style=\"width:560px; height:300px\" class=\"preloader\"></div>");
            vboxLeft.appendChild(graphLeft);
            try {
                javascript += "drawGraph(" + helper.getJSONMetric(instance, DODConstants.MYSQL_OVERVIEW_METRICS[i][0], 14) + ", 'graphDiv" + DODConstants.MYSQL_OVERVIEW_METRICS[i][1] +"');";
            } catch (IOException ex) {
                Logger.getLogger(MonitoringController.class.getName()).log(Level.SEVERE, "ERROR DISPLAYING METRIC", ex);
                showError(DODConstants.ERROR_DISPATCHING_JOB);
            }
            hbox.appendChild(vboxLeft);
            //Vbox for second graph
            Vbox vboxRight = new Vbox();
            vboxRight.setAlign("center");
            vboxRight.setWidth("600px");
            Label labelRight = new Label(DODConstants.MYSQL_OVERVIEW_METRICS[i+1][1]);
            labelRight.setSclass("titleSmall");
            vboxRight.appendChild(labelRight);
            Html graphRight = new Html();
            graphRight.setContent("<div id=\"graphDiv" + DODConstants.MYSQL_OVERVIEW_METRICS[i+1][1] +"\" style=\"width:560px; height:300px\" class=\"preloader\"></div>");
            vboxRight.appendChild(graphRight);
            try {
                javascript += "drawGraph(" + helper.getJSONMetric(instance, DODConstants.MYSQL_OVERVIEW_METRICS[i+1][0], 14) + ", 'graphDiv" + DODConstants.MYSQL_OVERVIEW_METRICS[i+1][1] +"');";
            } catch (IOException ex) {
                Logger.getLogger(MonitoringController.class.getName()).log(Level.SEVERE, "ERROR DISPLAYING METRIC", ex);
                showError(DODConstants.ERROR_DISPATCHING_JOB);
            }
            hbox.appendChild(vboxRight);
            //Add to container
            ((Groupbox) getFellow("container")).appendChild(hbox);
        }
        //If array has an odd number of elements add one more in the center
        if  (DODConstants.MYSQL_OVERVIEW_METRICS.length % 2 == 1) {
            //Vbox for firsst graph
            Vbox vbox = new Vbox();
            vbox.setAlign("center");
            vbox.setWidth("1200px");
            Label labelLeft = new Label(DODConstants.MYSQL_OVERVIEW_METRICS[DODConstants.MYSQL_OVERVIEW_METRICS.length - 1][1]);
            labelLeft.setSclass("titleSmall");
            vbox.appendChild(labelLeft);
            Html graphLeft = new Html();
            graphLeft.setContent("<div id=\"graphDiv" + DODConstants.MYSQL_OVERVIEW_METRICS[DODConstants.MYSQL_OVERVIEW_METRICS.length - 1][1] +"\" style=\"width:560px; height:300px\" class=\"preloader\"></div>");
            vbox.appendChild(graphLeft);
            try {
                javascript += "drawGraph(" + helper.getJSONMetric(instance, DODConstants.MYSQL_OVERVIEW_METRICS[DODConstants.MYSQL_OVERVIEW_METRICS.length - 1][0], 14)
                                        + ", 'graphDiv" + DODConstants.MYSQL_OVERVIEW_METRICS[DODConstants.MYSQL_OVERVIEW_METRICS.length - 1][1] +"');";
            } catch (IOException ex) {
                Logger.getLogger(MonitoringController.class.getName()).log(Level.SEVERE, "ERROR DISPLAYING METRIC", ex);
                showError(DODConstants.ERROR_DISPATCHING_JOB);
            }
            ((Groupbox) getFellow("container")).appendChild(vbox);
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