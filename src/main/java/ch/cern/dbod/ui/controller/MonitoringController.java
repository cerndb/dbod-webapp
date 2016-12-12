/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;

import ch.cern.dbod.appservlet.ConfigLoader;
import ch.cern.dbod.db.dao.MonitoringDAO;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Metric;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.ParamsHelper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the monitoring window.
 * @author Daniel Gomez Blanco
 */
public class MonitoringController extends Window {

    /**
     * Instance being managed at the moment
     */
    private Instance instance;
    /**
     * Helper to access monitoring
     */
    private MonitoringDAO dao;
    /**
     * Available metrics for this instance
     */
    private Combobox metrics;
    /**
     * URL to access Lemon
     */
    String lemonURL = "";
    /**
     * Constructor for the monitoring window. It creates the components cotained in this window.
     * @param inst instance being managed at the moment.
     * @throws InterruptedException if the window cannot be created.
     */
    public MonitoringController(Instance inst) throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance and create job helper
        this.instance = inst;
        dao = new MonitoringDAO();

        //Create params helper to get the host name for the link to Lemon
        //Get user and password for the web services account
        String wsUser = ConfigLoader.getProperty(CommonConstants.WS_USER);
        String wsPswd = ConfigLoader.getProperty(CommonConstants.WS_PSWD);
        ParamsHelper paramsHelper = new ParamsHelper(wsUser, wsPswd);

        String hostname = paramsHelper.getHost(instance);
        if (hostname != null)
            hostname = hostname.substring(0, hostname.indexOf("."));
        final String host = hostname;
        if (host != null && !host.isEmpty())
           lemonURL = CommonConstants.LEMON_URL + host;

        //Basic window properties
        this.setId("monitoringWindow");
        this.setTitle(Labels.getLabel(CommonConstants.LABEL_METRICS_TITLE) + " " + instance.getDbName());
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("590px");

        //Main box, used to apply padding
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        //Select metric label
        Label metricsLabel = new Label(Labels.getLabel(CommonConstants.LABEL_SELECT_METRIC) + ":");
        metricsLabel.setStyle("font-weight:bold");
        mainBox.appendChild(metricsLabel);

        //Load available metrics
        metrics = new Combobox();
        metrics.setMold("rounded");
        metrics.setReadonly(true);
        metrics.setWidth("560px");
        loadMetrics();
        metrics.addEventListener(Events.ON_SELECT, new EventListener() {
            @Override
            public void onEvent(Event event) {
                if (metrics.getSelectedItem().getValue() != null) {
                    Clients.evalJavaScript("document.getElementById(\"graphDiv\").className += \" preloader\"; "
                                + "drawGraph(" + dao.selectJSONData(instance, host, (Metric) metrics.getSelectedItem().getValue(), CommonConstants.MONITORING_DAYS) + ", 'graphDiv');");
                }
            }
        });
        mainBox.appendChild(metrics);

        //Create graph
        Html graphDiv = new Html();
        graphDiv.setContent("<div id=\"graphDiv\" style=\"width:560px; height:300px\" class=\"preloader\"></div>");
        mainBox.appendChild(graphDiv);
        if (metrics.getItemCount() > 0) {
            Clients.evalJavaScript("drawGraph(" + dao.selectJSONData(instance, host, (Metric) metrics.getItemAtIndex(1).getValue(), CommonConstants.MONITORING_DAYS) + ", 'graphDiv');");
        }
        
        //Link to monitoring overview
        Hbox overviewBox = new Hbox();
        Label overviewMessage = new Label(Labels.getLabel(CommonConstants.LABEL_MONITORING_OVERVIEW_MESSAGE));
        Html overviewLink = new Html();
        overviewLink.setContent("<a target=\"_blank\" style=\"text-decoration:underline;color:blue\" class=\"z-label\" href=\""
                                    + Executions.encodeURL(CommonConstants.PAGE_MONITORING_OVERVIEW + "?" + CommonConstants.INSTANCE + "=" + instance.getDbName()) 
                                    +"\">" + Labels.getLabel(CommonConstants.LABEL_MONITORING_OVERVIEW) + "</a>");
        Label overviewWarning = new Label(Labels.getLabel(CommonConstants.LABEL_MONITORING_OVERVIEW_WARNING));
        overviewBox.appendChild(overviewMessage);
        overviewBox.appendChild(overviewLink);
        overviewBox.appendChild(overviewWarning);
        mainBox.appendChild(overviewBox);

        //Load link to Lemon
        if (!lemonURL.isEmpty()) {
            Hbox lemonBox = new Hbox();
            Label lemonMessage = new Label(Labels.getLabel(CommonConstants.LABEL_LEMON_MESSAGE));
            Html lemonLink = new Html();
            lemonLink.setContent("<a target=\"_blank\" class=\"z-label\" href=\""+ lemonURL +"\">" + Labels.getLabel(CommonConstants.LABEL_LEMON_LINK) + "</a>");
            lemonBox.appendChild(lemonMessage);
            lemonBox.appendChild(lemonLink);
            mainBox.appendChild(lemonBox);
        }
        
        //Close button
        Div buttonsDiv = new Div();
        buttonsDiv.setWidth("100%");
        Hbox acceptBox = new Hbox();
        acceptBox.setHeight("24px");
        acceptBox.setAlign("bottom");
        acceptBox.setStyle("float:left;");
        Label acceptLabel = new Label(Labels.getLabel(CommonConstants.LABEL_CLOSE));
        acceptLabel.setSclass(CommonConstants.STYLE_TITLE);
        acceptLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        acceptLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doAccept();
            }
        });
        Toolbarbutton acceptButton = new Toolbarbutton();
        acceptButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_CLOSE));
        acceptButton.setZclass(CommonConstants.STYLE_BUTTON);
        acceptButton.setImage(CommonConstants.IMG_CANCEL);
        acceptButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doAccept();
            }
        });
        acceptBox.appendChild(acceptButton);
        acceptBox.appendChild(acceptLabel);
        buttonsDiv.appendChild(acceptBox);
        this.appendChild(buttonsDiv);
    }

    /**
     * Method executed when user accepts form.
     */
    private void doAccept() {
        metrics.getFellow("monitoringWindow").detach();
    }

    /**
     * Loads the combobox with the available metrics
     */
    private void loadMetrics() {
        //Get available metrics
        List<Metric> metricsList = dao.selectAvailableMetrics(instance);
        //Insert items in combobox
        for (int i = 0; i < metricsList.size(); i++) {
            Metric metric = metricsList.get(i);
            //Add title
            if (i == 0 || !metric.getType().equals(metricsList.get(i - 1).getType())) {
                Comboitem title = new Comboitem();
                title.setLabel(Labels.getLabel(CommonConstants.LABEL_METRICS + metric.getType()));
                title.setSclass("metricGroup");
                title.setDisabled(true);
                metrics.appendChild(title);
            }
            Comboitem item = new Comboitem();
            item.setValue(metric);
            if (metric.getUnit() != null) {
                item.setLabel("\t" + metric.getName() + " (" + metric.getUnit() + " )");
            }
            else {
                item.setLabel("\t" + metric.getName());
            }
            metrics.appendChild(item);
        }
        //Selects first element
        if (metricsList.size() > 0) {
            metrics.setSelectedIndex(1);
        }
    }

    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(String errorCode) {
        Window errorWindow = (Window) this.getParent().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
