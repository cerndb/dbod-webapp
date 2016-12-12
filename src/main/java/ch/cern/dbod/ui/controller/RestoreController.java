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
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Snapshot;
import ch.cern.dbod.ui.components.SnapshotCalendar;
import ch.cern.dbod.ui.model.OverviewTreeModel;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.JobHelper;
import ch.cern.dbod.util.SnapshotHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the restore window. Creates the window and all its components.
 * @author Daniel Gomez Blanco
 */
public class RestoreController extends Window {

    /**
     * Instance being managed at the moment.
     */
    private Instance instance;
    /**
     * Helper to execute jobs.
     */
    private JobHelper jobHelper;
    /**
     * Calendar to display snapshots by day.
     */
    private SnapshotCalendar snapshotCalendar;
    /**
     * Timebox to select time to restore
     */
    private Timebox time;
    /**
     * Datebox to select day to restore
     */
    private Datebox day;
    /**
     * User authenticated in the system at the moment.
     */
    private String username;
    /**
     * Date formatter for times.
     */
    private DateFormat timeFormatter;
    /**
     * Date formatter for days.
     */
    private DateFormat dateFormatter;
    /**
     * Date formatter for PITR
     */
    private DateFormat pitrFormatter;
    /**
     * List of snapshots for the current instance.
     */
    private List<Snapshot> snapshots;
    
    /**
     * Box to list calendar and snapshots for a specific day.
     */
    private Hbox snapshotsBox;
    
    /**
     * Box to list snapshots for a specific day.
     */
    private Vbox snapshotsList;
    
    /**
     * Min date for PITR
     */
    private Date minDate;
    
    /**
     * Max date for PITR
     */
    private Date maxDate;
    /**
     * Model of the tree (null if we are in list view).
     */
    private OverviewTreeModel model;

    /**
     * Creates this window, obtains the snapshots from the database and creates child components (coming from instance view).
     * @param inst current instance.
     * @param user current authenticated user.
     * @param jobHelper helper to create jobs.
     * @throws InterruptedException if the window cannot be created.
     */
    public RestoreController(Instance inst, String user, JobHelper jobHelper) throws InterruptedException {
        this(inst, user, jobHelper, null);
    }
    
    /**
     * Creates this window, obtains the snapshots from the database and creates child components (coming from list view).
     * @param inst current instance.
     * @param user current authenticated user.
     * @param jobHelper helper to create jobs.
     * @param model model of the tree (null if we are in instance view).
     * @throws InterruptedException if the window cannot be created.
     */
    public RestoreController(Instance inst, String user, JobHelper jobHelper, OverviewTreeModel model) throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance and create job helper
        this.instance = inst;
        this.username = user;
        this.jobHelper = jobHelper;
        timeFormatter = new SimpleDateFormat(CommonConstants.TIME_FORMAT);
        dateFormatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        pitrFormatter = new SimpleDateFormat(CommonConstants.DATE_TIME_FORMAT_PITR);
        
        //Initialise model and node
        this.model = model;

        //Get user and password for the web services account
        String wsUser = ConfigLoader.getProperty(CommonConstants.WS_USER);
        String wsPswd = ConfigLoader.getProperty(CommonConstants.WS_PSWD);

        //Get snapshots
        SnapshotHelper snapshotHelper = new SnapshotHelper(wsUser, wsPswd);
        snapshots = snapshotHelper.getSnapshots(instance);
        Collections.sort(snapshots);

        //Basic window properties
        this.setId("restoreWindow");
        this.setTitle(Labels.getLabel(CommonConstants.LABEL_RESTORE_TITLE) + " " + instance.getDbName());
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("470px");

        //Main box, used to apply padding
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        //Calendar label
        Label calendarLabel = new Label(Labels.getLabel(CommonConstants.LABEL_AVAILABLE_SNAPSHOTS));
        calendarLabel.setStyle("font-weight:bold");
        mainBox.appendChild(calendarLabel);

        //Box for snapshots
        snapshotsBox = new Hbox();
        
        //Create calendar and append it
        snapshotCalendar = new SnapshotCalendar();
        snapshotCalendar.setSnapshots(snapshots);
        snapshotCalendar.addEventListener(Events.ON_CHANGE, new EventListener() {
            @Override
            public void onEvent(Event event) {
                loadSnapshotsForDay(snapshotCalendar.getValue());
            }
        });
        snapshotsBox.appendChild(snapshotCalendar);

        //Load snapshots for today
        loadSnapshotsForDay(new Date());
        
        mainBox.appendChild(snapshotsBox);
        
        //Time to restore
        Hbox timeBox = new Hbox();
        timeBox.setAlign("bottom");
        Label timeLabel = new Label(Labels.getLabel(CommonConstants.LABEL_SELECT_SNAPSHOT));
        timeLabel.setStyle("font-weight:bold");
        timeBox.appendChild(timeLabel);
        day = new Datebox();
        day.setFormat(CommonConstants.DATE_FORMAT);
        day.setWidth("90px");
        day.setStyle("margin-left:30px");
        timeBox.appendChild(day);
        time = new Timebox();
        time.setWidth("80px");
        timeBox.appendChild(time);
        mainBox.appendChild(timeBox);

        //Div for accept and cancel buttons
        Div buttonsDiv = new Div();
        buttonsDiv.setWidth("100%");

        //Cancel button
        Hbox cancelBox = new Hbox();
        cancelBox.setHeight("24px");
        cancelBox.setAlign("bottom");
        cancelBox.setStyle("float:left;");
        Toolbarbutton cancelButton = new Toolbarbutton();
        cancelButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_CANCEL));
        cancelButton.setZclass(CommonConstants.STYLE_BUTTON);
        cancelButton.setImage(CommonConstants.IMG_CANCEL);
        cancelButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doCancel();
            }
        });
        cancelBox.appendChild(cancelButton);
        Label cancelLabel = new Label(Labels.getLabel(CommonConstants.LABEL_CANCEL));
        cancelLabel.setSclass(CommonConstants.STYLE_TITLE);
        cancelLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        cancelLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doCancel();
            }
        });
        cancelBox.appendChild(cancelLabel);
        buttonsDiv.appendChild(cancelBox);

        //Accept button
        Hbox acceptBox = new Hbox();
        acceptBox.setHeight("24px");
        acceptBox.setAlign("bottom");
        acceptBox.setStyle("float:right;");
        Label acceptLabel = new Label(Labels.getLabel(CommonConstants.LABEL_ACCEPT));
        acceptLabel.setSclass(CommonConstants.STYLE_TITLE);
        acceptLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        acceptLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doAccept();
            }
        });
        acceptBox.appendChild(acceptLabel);
        Toolbarbutton acceptButton = new Toolbarbutton();
        acceptButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_ACCEPT));
        acceptButton.setZclass(CommonConstants.STYLE_BUTTON);
        acceptButton.setImage(CommonConstants.IMG_ACCEPT);
        acceptButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doAccept();
            }
        });
        acceptBox.appendChild(acceptButton);
        buttonsDiv.appendChild(acceptBox);
        this.appendChild(buttonsDiv);
    }

    /**
     * Creates a new job to restore the selected snapshot
     */
    private void doAccept() {
        //If there is a snapshot selected
        if (day.getValue() != null && time.getValue() != null) {
            Calendar dayPart = Calendar.getInstance();
            dayPart.setTime(day.getValue());
            Calendar timePart = Calendar.getInstance();
            timePart.setTime(time.getValue());
            Calendar dayTime = Calendar.getInstance();
            dayTime.clear();
            dayTime.set(dayPart.get(Calendar.YEAR), dayPart.get(Calendar.MONTH), dayPart.get(Calendar.DAY_OF_MONTH),
                        timePart.get(Calendar.HOUR_OF_DAY), timePart.get(Calendar.MINUTE), timePart.get(Calendar.SECOND));
            Date dateToRestore = dayTime.getTime();
            //If it is a date in the past
            if (dateToRestore.compareTo(new Date()) < 0) {
                Snapshot snapshotToRestore = getSnapshotToRestore(dateToRestore);
                //If there is an available snapshot
                if (snapshotToRestore != null) {
                    //If the time is different and more than 1 minute in the future
                    if (!dateToRestore.equals(snapshotToRestore.getCreationDate())
                            && dateToRestore.getTime() - snapshotToRestore.getCreationDate().getTime() < 60000) {
                        time.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_PIT_ONE_MINUTE));
                        return;
                    }
                    try {
                        RestoreConfirmWindow confirmWindow = new RestoreConfirmWindow(snapshotToRestore, dateToRestore);
                        //Only show window if it is not already being diplayed
                        if (this.getFellowIfAny(confirmWindow.getId()) == null) {
                            confirmWindow.setParent(this);
                            confirmWindow.doModal();
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR DISPLAYING RESTORE CONFIRM WINDOW", ex);
                        showError(CommonConstants.ERROR_DISPLAYING_CONFIRM_WINDOW);
                    }
                }
                else {
                    time.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_NO_SNAPSHOT)); 
                }
            }
            else {
               time.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_SNAPSHOT_PAST)); 
            }
        } else {
            time.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_SELECT_SNAPSHOT));
        }
    }

    /**
     * Detaches the windows from the page.
     */
    private void doCancel() {
        time.getFellow("restoreWindow").detach();
    }

    /**
     * Loads the snapshots for a selected day.
     * @param date day when the snapshots were taken.
     */
    private void loadSnapshotsForDay(Date date) {
        //Remove previous items
        if (snapshotsList != null)
            snapshotsList.detach();
        snapshotsList = new Vbox();
        snapshotsList.setStyle("margin-left:10px");

        //Insert label in vbox
        Label title = new Label(Labels.getLabel(CommonConstants.LABEL_SNAPSHOTS_FOR_DAY));
        snapshotsList.appendChild(title);
        if (snapshots != null && snapshots.size() > 0) {
            for (int i = 0; i < snapshots.size(); i++) {
                final Snapshot snapshot = snapshots.get(i);
                if (dateFormatter.format(snapshot.getCreationDate()).equals(dateFormatter.format(date))) {
                    Label label = new Label(timeFormatter.format(snapshot.getCreationDate()));
                    label.setStyle("margin-left:12px;hyphens:none;text-wrap:none;-webkit-hyphens:none;white-space:nowrap;color:blue;cursor:pointer;text-decoration:underline");
                    label.addEventListener(Events.ON_CLICK, new EventListener() {
                        @Override
                        public void onEvent(Event event) throws Exception {
                            //Load instance on day and time
                            day.setValue(snapshot.getCreationDate());
                            time.setValue(snapshot.getCreationDate());
                        }
                    });
                    snapshotsList.appendChild(label);
                }
            }
        }
        if (snapshotsList.getChildren().size() == 1) {
            Label noSnapshots = new Label(Labels.getLabel(CommonConstants.LABEL_SNAPSHOTS_FOR_DAY_EMPTY));
            noSnapshots.setStyle("font-style:italic");
            snapshotsList.appendChild(noSnapshots);
        }
        snapshotsBox.appendChild(snapshotsList);
    }
    
    /**
     * Returns the closest snapshot to the given date.
     * @param dateToRestore date to restore.
     * @return closest snapshot to the given date, or null if there is no snapshot taken.
     */
    private Snapshot getSnapshotToRestore (Date dateToRestore) {
        Snapshot toret = null;
        if (snapshots != null && snapshots.size() > 0) {
            for (int i = 0; i < snapshots.size(); i++) {
                Snapshot snapshot = snapshots.get(i);
                //If there is a previous snapshot
                if (snapshot.getCreationDate().compareTo(dateToRestore) <= 0) {
                    toret = snapshot;
                }
                //If the snapshot is in the future there will be no more snapshots (array is ordered)
                else
                {
                    break;
                }
            }
        }
        return toret;
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
    
    /**
     * Confirm window to warn the user about the restore operation.
     * @author Daniel Gomez Blanco
     */
    private class RestoreConfirmWindow extends Window {
        
        /**
         * Snaphsot to restore.
         */
        private Snapshot snapshotToRestore;
        
        /**
         * Day to restore
         */
        private Date dateToRestore;

        /**
         * Constructor for this window.
         * @throws InterruptedException if the window cannot be created.
         */
        public RestoreConfirmWindow(Snapshot snap, Date date) throws InterruptedException {
            //Call super constructor
            super();
            
            //Instantiate variables
            snapshotToRestore = snap;
            dateToRestore = date;

            //Basic window properties
            this.setId("restoreConfirmWindow");
            this.setTitle(Labels.getLabel(CommonConstants.LABEL_RESTORE_CONFIRM_TITLE));
            this.setBorder("normal");
            this.setMode(Window.OVERLAPPED);
            this.setPosition("center");
            this.setClosable(false);
            this.setWidth("350px");

            //Main box used to apply pading
            Vbox mainBox = new Vbox();
            mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
            this.appendChild(mainBox);

            //Box for message
            Hbox messageBox = new Hbox();
            messageBox.appendChild(new Image(CommonConstants.IMG_WARNING));
            //Main message
            Label message = new Label(Labels.getLabel(CommonConstants.LABEL_RESTORE_CONFIRM_MESSAGE));
            messageBox.appendChild(message);
            mainBox.appendChild(messageBox);

            //Div for accept and cancel buttons
            Div buttonsDiv = new Div();
            buttonsDiv.setWidth("100%");

            //Cancel button
            Hbox cancelBox = new Hbox();
            cancelBox.setHeight("24px");
            cancelBox.setAlign("bottom");
            cancelBox.setStyle("float:left;");
            Toolbarbutton cancelButton = new Toolbarbutton();
            cancelButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_CANCEL));
            cancelButton.setZclass(CommonConstants.STYLE_BUTTON);
            cancelButton.setImage(CommonConstants.IMG_CANCEL);
            cancelButton.addEventListener(Events.ON_CLICK, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    doCancel();
                }
            });
            cancelBox.appendChild(cancelButton);
            Label cancelLabel = new Label(Labels.getLabel(CommonConstants.LABEL_CANCEL));
            cancelLabel.setSclass(CommonConstants.STYLE_TITLE);
            cancelLabel.setStyle("font-size:10pt !important;cursor:pointer;");
            cancelLabel.addEventListener(Events.ON_CLICK, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    doCancel();
                }
            });
            cancelBox.appendChild(cancelLabel);
            buttonsDiv.appendChild(cancelBox);

            //Accept button
            Hbox acceptBox = new Hbox();
            acceptBox.setHeight("24px");
            acceptBox.setAlign("bottom");
            acceptBox.setStyle("float:right;");
            Label acceptLabel = new Label(Labels.getLabel(CommonConstants.LABEL_ACCEPT));
            acceptLabel.setSclass(CommonConstants.STYLE_TITLE);
            acceptLabel.setStyle("font-size:10pt !important;cursor:pointer;");
            acceptLabel.addEventListener(Events.ON_CLICK, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    doAccept();
                }
            });
            acceptBox.appendChild(acceptLabel);
            Toolbarbutton acceptButton = new Toolbarbutton();
            acceptButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_ACCEPT));
            acceptButton.setZclass(CommonConstants.STYLE_BUTTON);
            acceptButton.setImage(CommonConstants.IMG_ACCEPT);
            acceptButton.addEventListener(Events.ON_CLICK, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    doAccept();
                }
            });
            acceptBox.appendChild(acceptButton);
            buttonsDiv.appendChild(acceptBox);
            this.appendChild(buttonsDiv);
        }


        /**
         * Method executed when user accepts the form. A job is created and the window is detached.
         */
        private void doAccept() {
            //Create new job and update instance status
            if (jobHelper.doRestore(instance, username, snapshotToRestore, dateToRestore)) {
                if (model != null) {
                //Reload the node
                model.updateInstance(instance);
            } //If we are in the instance page
                else if (time.getRoot().getFellowIfAny("controller") != null && time.getRoot().getFellow("controller") instanceof InstanceController) {
                    InstanceController controller = (InstanceController) time.getRoot().getFellow("controller");
                    controller.afterCompose();
                }
            } else {
                showError(CommonConstants.ERROR_DISPATCHING_JOB);
            }
            this.detach();
            RestoreController.this.detach();
        }

        /**
         * Method executed when the user cancels the form. The window is detached from the page.
         */
        private void doCancel() {
            this.detach();
        }
    }
}
