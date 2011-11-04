package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODJobDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the backup window. Creates the window and all its components.
 * @author Daniel Gomez Blanco
 * @version 24/10/2011
 */
public class BackupController extends Window {

    /**
     * Instance being managed at the moment.
     */
    private DODInstance instance;
    /**
     * Helper to execute jobs.
     */
    private JobHelper jobHelper;
    /**
     * Checkbox to indicate if snapshots should be taken automatically every x hours.
     */
    private Checkbox automatic;
    /**
     * Number of hours between automatic backups.
     */
    private Spinner interval;
    /**
     * User authenticated in the system at the moment.
     */
    private String username;

    /**
     * Constructor for this window.
     * @param inst instance to be managed.
     * @param user username for the authenticated user.
     * @param jobHelper helper to execute jobs.
     * @throws InterruptedException if the window cannot be created.
     */
    public BackupController(DODInstance inst, String user, JobHelper jobHelper) throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance and create job helper
        this.instance = inst;
        this.username = user;
        this.jobHelper = jobHelper;

        //Basic window properties
        this.setId("backupWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_BACKUP_TITLE));
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("350px");

        //Main box used to apply pading
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        //Main message
        Label message = new Label(Labels.getLabel(DODConstants.LABEL_BACKUP_MESSAGE));
        mainBox.appendChild(message);

        //Box containing the checkbox for automatic backups and the interval
        Hbox autoBox =  new Hbox();
        autoBox.setStyle("margin-left:20px");
        autoBox.setAlign("bottom");
        //Create checkbox for automatic backups
        automatic = new Checkbox();
        automatic.setLabel(Labels.getLabel(DODConstants.LABEL_AUTOMATIC_BACKUP));
        autoBox.appendChild(automatic);
        //Create spinner for the interval
        interval = new Spinner();
        interval.setValue(DODConstants.DEFAULT_INTERVAL_HOURS);
        interval.setWidth("50px");
        interval.setConstraint("min " + DODConstants.MIN_INTERVAL_HOURS);
        autoBox.appendChild(interval);
        autoBox.appendChild(new Label(Labels.getLabel(DODConstants.LABEL_HOURS)));
        mainBox.appendChild(autoBox);
        
        //Box containing the button to disable automatic backups
        Hbox disableBox =  new Hbox();
        disableBox.setStyle("margin-bottom:10px;margin-left:20px");
        disableBox.setHeight("24px");
        disableBox.setAlign("bottom");
        Toolbarbutton disableButton = new Toolbarbutton();
        disableButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_DISABLE_AUTOMATIC_BACKUP));
        disableButton.setSclass(DODConstants.STYLE_BUTTON);
        disableButton.setImage(DODConstants.IMG_CANCEL);
        disableButton.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doDisable();
            }
        });
        disableBox.appendChild(disableButton);
        Label disableLabel = new Label(Labels.getLabel(DODConstants.LABEL_DISABLE_AUTOMATIC_BACKUP));
        disableBox.appendChild(disableLabel);
        mainBox.appendChild(disableBox);

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
     * Disables the automatic backups created in a previous job.
     */
    public void doDisable() {
        DODJobDAO jobDAO = new DODJobDAO();
        int result = 0;
        if (jobHelper.isAdminMode())
             result = jobDAO.deleteScheduledBackup(instance, username, 1);
        else
            result = jobDAO.deleteScheduledBackup(instance, username, 0);
        if (result <= 0) {
            showError(DODConstants.ERROR_DISABLING_AUTO_BACKUPS);
        }
        else {
            //If we are in the overview page
            if (interval.getRoot().getFellowIfAny("overviewGrid") != null) {
                Grid grid = (Grid) interval.getRoot().getFellow("overviewGrid");
                grid.setModel(grid.getListModel());
            } //If we are in the instance page
            else if (interval.getRoot().getFellowIfAny("controller") != null && interval.getRoot().getFellow("controller") instanceof InstanceController) {
                InstanceController controller = (InstanceController) interval.getRoot().getFellow("controller");
                controller.afterCompose();
            }
            interval.getFellow("backupWindow").detach();
        }
        
    }

    /**
     * Method executed when user accepts the form. A job is created and the window is detached.
     */
    private void doAccept() {
        boolean result = false;
        //If there are no previous error messages on the interval component
        if (interval.getErrorMessage() == null || interval.getErrorMessage().isEmpty()) {
            //If automatic updates are checked create job
            if (automatic.isChecked()) {
                if (interval.getValue() != null && interval.getValue() > 0)
                    result = jobHelper.doBackup(instance, username, interval.getValue());
            }
            //If automatic updates are not checked set interval to 0
            else {
                result = jobHelper.doBackup(instance, username, 0);
            }
            //If the operation was successful update instance status
            if (result) {
                //If we are in the overview page
                if (interval.getRoot().getFellowIfAny("overviewGrid") != null) {
                    Grid grid = (Grid) interval.getRoot().getFellow("overviewGrid");
                    grid.setModel(grid.getListModel());
                } //If we are in the instance page
                else if (interval.getRoot().getFellowIfAny("controller") != null && interval.getRoot().getFellow("controller") instanceof InstanceController) {
                    InstanceController controller = (InstanceController) interval.getRoot().getFellow("controller");
                    controller.afterCompose();
                }
                interval.getFellow("backupWindow").detach();
            }
            else
                showError(DODConstants.ERROR_DISPATCHING_JOB);
            
        }
    }

    /**
     * Method executed when the user cancels the form. The window is detached from the page.
     */
    private void doCancel() {
        automatic.getFellow("backupWindow").detach();
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
