package ch.cern.dod.ui.controller;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODSnapshot;
import ch.cern.dod.ui.components.SnapshotCalendar;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import ch.cern.dod.util.SnapshotHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
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
import org.zkoss.zul.Tree;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the backup window. Creates the window and all its components.
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class RestoreController extends Window {

    /**
     * Instance being managed at the moment.
     */
    private DODInstance instance;
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
    private List<DODSnapshot> snapshots;
    
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
     * Creates this window, obtains the snapshots from the database and creates child components.
     * @param inst current instance.
     * @param user current authenticated user.
     * @param jobHelper helper to create jobs.
     * @throws InterruptedException if the window cannot be created.
     */
    public RestoreController(DODInstance inst, String user, JobHelper jobHelper) throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance and create job helper
        this.instance = inst;
        this.username = user;
        this.jobHelper = jobHelper;
        timeFormatter = new SimpleDateFormat(DODConstants.TIME_FORMAT);
        dateFormatter = new SimpleDateFormat(DODConstants.DATE_FORMAT);
        pitrFormatter = new SimpleDateFormat(DODConstants.DATE_TIME_FORMAT_PITR);

        //Get user and password for the web services account
        String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(DODConstants.WS_USER);
        String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(DODConstants.WS_PSWD);

        //Get snapshots
        SnapshotHelper snapshotHelper = new SnapshotHelper(wsUser, wsPswd);
        snapshots = snapshotHelper.getSnapshots(instance);
        Collections.sort(snapshots);

        //Basic window properties
        this.setId("restoreWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_RESTORE_TITLE) + " " + instance.getDbName());
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
        Label calendarLabel = new Label(Labels.getLabel(DODConstants.LABEL_AVAILABLE_SNAPSHOTS));
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
        Label timeLabel = new Label(Labels.getLabel(DODConstants.LABEL_SELECT_SNAPSHOT));
        timeLabel.setStyle("font-weight:bold");
        timeBox.appendChild(timeLabel);
        day = new Datebox();
        day.setFormat(DODConstants.DATE_FORMAT);
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
        cancelButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CANCEL));
        cancelButton.setZclass(DODConstants.STYLE_BUTTON);
        cancelButton.setImage(DODConstants.IMG_CANCEL);
        cancelButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doCancel();
            }
        });
        cancelBox.appendChild(cancelButton);
        Label cancelLabel = new Label(Labels.getLabel(DODConstants.LABEL_CANCEL));
        cancelLabel.setSclass(DODConstants.STYLE_TITLE);
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
        Label acceptLabel = new Label(Labels.getLabel(DODConstants.LABEL_ACCEPT));
        acceptLabel.setSclass(DODConstants.STYLE_TITLE);
        acceptLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        acceptLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doAccept();
            }
        });
        acceptBox.appendChild(acceptLabel);
        Toolbarbutton acceptButton = new Toolbarbutton();
        acceptButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_ACCEPT));
        acceptButton.setZclass(DODConstants.STYLE_BUTTON);
        acceptButton.setImage(DODConstants.IMG_ACCEPT);
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
                DODSnapshot snapshotToRestore = getSnapshotToRestore(dateToRestore);
                //If there is an available snapshot
                if (snapshotToRestore != null) {
                    //If the time is different and more than 1 minute in the future
                    if (!dateToRestore.equals(snapshotToRestore.getCreationDate())
                            && dateToRestore.getTime() - snapshotToRestore.getCreationDate().getTime() < 60000) {
                        time.setErrorMessage(Labels.getLabel(DODConstants.ERROR_PIT_ONE_MINUTE));
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
                        showError(DODConstants.ERROR_DISPLAYING_CONFIRM_WINDOW);
                    }
                }
                else {
                    time.setErrorMessage(Labels.getLabel(DODConstants.ERROR_NO_SNAPSHOT)); 
                }
            }
            else {
               time.setErrorMessage(Labels.getLabel(DODConstants.ERROR_SNAPSHOT_PAST)); 
            }
        } else {
            time.setErrorMessage(Labels.getLabel(DODConstants.ERROR_SELECT_SNAPSHOT));
        }
    }

    /**
     * Detachs the windows from the page.
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
        Label title = new Label(Labels.getLabel(DODConstants.LABEL_SNAPSHOTS_FOR_DAY));
        snapshotsList.appendChild(title);
        if (snapshots != null && snapshots.size() > 0) {
            for (int i = 0; i < snapshots.size(); i++) {
                final DODSnapshot snapshot = snapshots.get(i);
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
            Label noSnapshots = new Label(Labels.getLabel(DODConstants.LABEL_SNAPSHOTS_FOR_DAY_EMPTY));
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
    private DODSnapshot getSnapshotToRestore (Date dateToRestore) {
        DODSnapshot toret = null;
        if (snapshots != null && snapshots.size() > 0) {
            for (int i = 0; i < snapshots.size(); i++) {
                DODSnapshot snapshot = snapshots.get(i);
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
     * Confirm window
     * @author Daniel Gomez Blanco
     * @version 02/12/2011
     */
    private class RestoreConfirmWindow extends Window {
        
        /**
         * Snaphsot to restore.
         */
        private DODSnapshot snapshotToRestore;
        
        /**
         * Day to restore
         */
        private Date dateToRestore;

        /**
         * Constructor for this window.
         * @throws InterruptedException if the window cannot be created.
         */
        public RestoreConfirmWindow(DODSnapshot snap, Date date) throws InterruptedException {
            //Call super constructor
            super();
            
            //Instantiate variables
            snapshotToRestore = snap;
            dateToRestore = date;

            //Basic window properties
            this.setId("restoreConfirmWindow");
            this.setTitle(Labels.getLabel(DODConstants.LABEL_RESTORE_CONFIRM_TITLE));
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
            messageBox.appendChild(new Image(DODConstants.IMG_WARNING));
            //Main message
            Label message = new Label(Labels.getLabel(DODConstants.LABEL_RESTORE_CONFIRM_MESSAGE));
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
            cancelButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CANCEL));
            cancelButton.setZclass(DODConstants.STYLE_BUTTON);
            cancelButton.setImage(DODConstants.IMG_CANCEL);
            cancelButton.addEventListener(Events.ON_CLICK, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    doCancel();
                }
            });
            cancelBox.appendChild(cancelButton);
            Label cancelLabel = new Label(Labels.getLabel(DODConstants.LABEL_CANCEL));
            cancelLabel.setSclass(DODConstants.STYLE_TITLE);
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
            Label acceptLabel = new Label(Labels.getLabel(DODConstants.LABEL_ACCEPT));
            acceptLabel.setSclass(DODConstants.STYLE_TITLE);
            acceptLabel.setStyle("font-size:10pt !important;cursor:pointer;");
            acceptLabel.addEventListener(Events.ON_CLICK, new EventListener() {
                @Override
                public void onEvent(Event event) {
                    doAccept();
                }
            });
            acceptBox.appendChild(acceptLabel);
            Toolbarbutton acceptButton = new Toolbarbutton();
            acceptButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_ACCEPT));
            acceptButton.setZclass(DODConstants.STYLE_BUTTON);
            acceptButton.setImage(DODConstants.IMG_ACCEPT);
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
                //If we are in the overview page
                if (time.getRoot().getFellowIfAny("overviewTree") != null) {
                    //Reload the tree
                    Tree tree = (Tree) time.getRoot().getFellow("overviewTree");
                    int activePage = 0;
                    if (tree.getMold().equals("paging")) {
                        activePage = tree.getActivePage();
                    }
                    tree.setModel(tree.getModel());
                    try {
                        if (tree.getMold().equals("paging")) {
                            tree.setActivePage(activePage);
                        }
                    }
                    catch (WrongValueException ex) {}
                } //If we are in the instance page
                else if (time.getRoot().getFellowIfAny("controller") != null && time.getRoot().getFellow("controller") instanceof InstanceController) {
                    InstanceController controller = (InstanceController) time.getRoot().getFellow("controller");
                    controller.afterCompose();
                }
            } else {
                showError(DODConstants.ERROR_DISPATCHING_JOB);
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
