package ch.cern.dod.ui.controller;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODMetric;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.MonitoringHelper;
import ch.cern.dod.util.ParamsHelper;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
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
    private DODInstance instance;
    /**
     * Helper to access monitoring
     */
    private MonitoringHelper helper;
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
    public MonitoringController(DODInstance inst) throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance and create job helper
        this.instance = inst;
        helper = new MonitoringHelper();

        //Create params helper to get the host name for the link to Lemon
        //Get user and password for the web services account
        String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_USER);
        String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_PSWD);
        ParamsHelper paramsHelper = new ParamsHelper(wsUser, wsPswd);

        String hostname = paramsHelper.getParam(instance, DODConstants.PARAM_HOST);
        final String host = hostname.substring(0, hostname.indexOf("."));
        if (host != null)
           lemonURL = DODConstants.LEMON_URL + host;

        //Basic window properties
        this.setId("monitoringWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_METRICS_TITLE) + " " + instance.getDbName());
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
        Label metricsLabel = new Label(Labels.getLabel(DODConstants.LABEL_SELECT_METRIC) + ":");
        metricsLabel.setStyle("font-weight:bold");
        mainBox.appendChild(metricsLabel);

        //Load available metrics
        metrics = new Combobox();
        metrics.setMold("rounded");
        metrics.setReadonly(true);
        metrics.setWidth("560px");
        loadMetrics();
        metrics.addEventListener(Events.ON_SELECT, new EventListener() {
            public void onEvent(Event event) {
                if (metrics.getSelectedItem().getValue() != null) {
                    try {
                        Clients.evalJavaScript("document.getElementById(\"graphDiv\").className += \" preloader\"; "
                                + "drawGraph(" + helper.getJSONMetric(instance, host, (DODMetric) metrics.getSelectedItem().getValue(), 30) + ", 'graphDiv');");
                    } catch (IOException ex) {
                        Logger.getLogger(MonitoringController.class.getName()).log(Level.SEVERE, "ERROR DISPLAYING METRIC", ex);
                        showError(DODConstants.ERROR_DISPATCHING_JOB);
                    }
                }
            }
        });
        mainBox.appendChild(metrics);

        //Create graph
        Html graphDiv = new Html();
        graphDiv.setContent("<div id=\"graphDiv\" style=\"width:560px; height:300px\" class=\"preloader\"></div>");
        mainBox.appendChild(graphDiv);
        try {
            Clients.evalJavaScript("drawGraph(" + helper.getJSONMetric(instance, host, (DODMetric) metrics.getItemAtIndex(0).getValue(), 30) + ", 'graphDiv');");
        } catch (IOException ex) {
            Logger.getLogger(MonitoringController.class.getName()).log(Level.SEVERE, "ERROR DISPLAYING METRIC", ex);
            showError(DODConstants.ERROR_DISPATCHING_JOB);
        }
        
        //Link to monitoring overview
        Hbox overviewBox = new Hbox();
        Label overviewMessage = new Label(Labels.getLabel(DODConstants.LABEL_MONITORING_OVERVIEW_MESSAGE));
        Html overviewLink = new Html();
        overviewLink.setContent("<a target=\"_blank\" style=\"text-decoration:underline;color:blue\" class=\"z-label\" href=\""
                                    + Executions.encodeURL(DODConstants.PAGE_MONITORING_OVERVIEW + "?" + DODConstants.INSTANCE + "=" + instance.getDbName()) 
                                    +"\">" + Labels.getLabel(DODConstants.LABEL_MONITORING_OVERVIEW) + "</a>");
        Label overviewWarning = new Label(Labels.getLabel(DODConstants.LABEL_MONITORING_OVERVIEW_WARNING));
        overviewBox.appendChild(overviewMessage);
        overviewBox.appendChild(overviewLink);
        overviewBox.appendChild(overviewWarning);
        mainBox.appendChild(overviewBox);

        //Load link to Lemon
        if (!lemonURL.isEmpty()) {
            Hbox lemonBox = new Hbox();
            Label lemonMessage = new Label(Labels.getLabel(DODConstants.LABEL_LEMON_MESSAGE));
            Html lemonLink = new Html();
            lemonLink.setContent("<a target=\"_blank\" class=\"z-label\" href=\""+ lemonURL +"\">" + Labels.getLabel(DODConstants.LABEL_LEMON_LINK) + "</a>");
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
        Label acceptLabel = new Label(Labels.getLabel(DODConstants.LABEL_CLOSE));
        acceptLabel.setSclass(DODConstants.STYLE_TITLE);
        acceptLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        acceptLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doAccept();
            }
        });
        Toolbarbutton acceptButton = new Toolbarbutton();
        acceptButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CLOSE));
        acceptButton.setZclass(DODConstants.STYLE_BUTTON);
        acceptButton.setImage(DODConstants.IMG_CANCEL);
        acceptButton.addEventListener(Events.ON_CLICK, new EventListener() {
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
        try {
            //Get available metrics
            List<DODMetric> metricsList = helper.getAvailableMetrics(instance.getDbType());
            //Insert items in combobox
            for (int i = 0; i < metricsList.size(); i++) {
                DODMetric metric = metricsList.get(i);
                Comboitem item = new Comboitem();
                item.setValue(metric);
                item.setLabel(metric.getDescription());
                metrics.appendChild(item);
            }
            //Selects first element
            metrics.setSelectedIndex(0);
        } catch (MalformedURLException ex) {
            Logger.getLogger(MonitoringController.class.getName()).log(Level.SEVERE, "ERROR OBTAINING AVAILABLE METRICS", ex);
            showError(DODConstants.ERROR_OBTAINING_METRICS);
        } catch (IOException ex) {
            Logger.getLogger(MonitoringController.class.getName()).log(Level.SEVERE, "ERROR OBTAINING AVAILABLE METRICS", ex);
            showError(DODConstants.ERROR_OBTAINING_METRICS);
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
        } catch (InterruptedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
