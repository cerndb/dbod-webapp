package ch.cern.dod.ui.controller;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODSnapshot;
import ch.cern.dod.ui.components.SnapshotCalendar;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import ch.cern.dod.util.SnapshotHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
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
     * Combo to select a snapshot to restore
     */
    private Combobox snapshotsCombo;
    /**
     * User athenticated in the system at the moment.
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
     * List of snapshots for the current instance.
     */
    private List<DODSnapshot> snapshots;

    

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

        //Get user and password for the web services account
        String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_USER);
        String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_PSWD);

        //Get snapshots
        SnapshotHelper snapshotHelper = new SnapshotHelper(wsUser, wsPswd);
        snapshots = snapshotHelper.getSnapshots(instance);

        //Basic window properties
        this.setId("restoreWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_RESTORE_TITLE));
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("250px");

        //Main box, used to apply padding
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        //Calendar label
        Label calendarLabel = new Label(Labels.getLabel(DODConstants.LABEL_SELECT_SNAPSHOT_DATE) + ":");
        calendarLabel.setStyle("font-weight:bold");
        mainBox.appendChild(calendarLabel);

        //Create calendar and append it
        snapshotCalendar = new SnapshotCalendar();
        snapshotCalendar.setSnapshots(snapshots);
        snapshotCalendar.addEventListener(Events.ON_CHANGE, new EventListener() {
            public void onEvent(Event event) throws Exception {
                loadSnapshotsForDay(snapshotCalendar.getValue());
            }
        });
        mainBox.appendChild(snapshotCalendar);

        //Snapshots
        snapshotsCombo = new Combobox();
        snapshotsCombo.setMold("rounded");
        snapshotsCombo.setReadonly(true);
        loadSnapshotsForDay(new Date());
        mainBox.appendChild(snapshotsCombo);

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
        cancelButton.setSclass(DODConstants.STYLE_BUTTON);
        cancelButton.setImage(DODConstants.IMG_CANCEL);
        cancelButton.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doCancel();
            }
        });
        cancelBox.appendChild(cancelButton);
        Label cancelLabel = new Label(Labels.getLabel(DODConstants.LABEL_CANCEL));
        cancelLabel.setSclass(DODConstants.STYLE_TITLE);
        cancelLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        cancelLabel.addEventListener(Events.ON_CLICK, new EventListener() {
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

            public void onEvent(Event event) {
                doAccept();
            }
        });
        acceptBox.appendChild(acceptLabel);
        Toolbarbutton acceptButton = new Toolbarbutton();
        acceptButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_ACCEPT));
        acceptButton.setSclass(DODConstants.STYLE_BUTTON);
        acceptButton.setImage(DODConstants.IMG_ACCEPT);
        acceptButton.addEventListener(Events.ON_CLICK, new EventListener() {

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
        if (snapshotsCombo.getSelectedItem().getValue() != null) {
            //Create new job and update instance status
            if (jobHelper.doRestore(instance, username, (DODSnapshot) snapshotsCombo.getSelectedItem().getValue())) {
                //If we are in the overview page
                if (snapshotsCombo.getRoot().getFellowIfAny("overviewGrid") != null) {
                    Grid grid = (Grid) snapshotsCombo.getRoot().getFellow("overviewGrid");
                    grid.setModel(grid.getListModel());
                } //If we are in the instance page
                else if (snapshotsCombo.getRoot().getFellowIfAny("controller") != null && snapshotsCombo.getRoot().getFellow("controller") instanceof InstanceController) {
                    InstanceController controller = (InstanceController) snapshotsCombo.getRoot().getFellow("controller");
                    controller.afterCompose();
                }
            } else {
                showError(DODConstants.ERROR_DISPATCHING_JOB);
            }
            snapshotsCombo.getFellow("restoreWindow").detach();
        } else {
            snapshotsCombo.setErrorMessage(Labels.getLabel(DODConstants.ERROR_SELECT_SNAPSHOT));
        }
    }

    /**
     * Detachs the windows from the page.
     */
    private void doCancel() {
        snapshotsCombo.getFellow("restoreWindow").detach();
    }

    /**
     * Loads the snapshots for a selected day.
     * @param date day when the snapshots were taken.
     */
    private void loadSnapshotsForDay(Date date) {
        //Remove previous items
        snapshotsCombo.getItems().removeAll(snapshotsCombo.getItems());

        //Insert items in combobox
        Comboitem selectOne = new Comboitem();
        selectOne.setValue(null);
        selectOne.setLabel(Labels.getLabel(DODConstants.LABEL_SELECT_ONE));
        snapshotsCombo.appendChild(selectOne);
        if (snapshots != null && snapshots.size() > 0) {
            for (int i = 0; i < snapshots.size(); i++) {
                DODSnapshot snapshot = snapshots.get(i);
                if (dateFormatter.format(snapshot.getCreationDate()).equals(dateFormatter.format(date))) {
                    Comboitem item = new Comboitem();
                    item.setValue(snapshot);
                    String label = timeFormatter.format(snapshot.getCreationDate());
                    item.setLabel(label);
                    snapshotsCombo.appendChild(item);
                }
            }
        }
        
        //Select first element
        snapshotsCombo.setSelectedIndex(0);
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
