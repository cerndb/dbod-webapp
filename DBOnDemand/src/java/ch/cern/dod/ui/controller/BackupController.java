package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODJobDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Timebox;
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
     * DAO for jobs.
     */
    private DODJobDAO jobDAO;
    /**
     * Checkbox to indicate if snapshots should be taken automatically every x hours.
     */
    private Checkbox automatic;
    /**
     * Number of hours between automatic backups.
     */
    private Spinner interval;
    /**
     * Checkbox to indicate if backups to tape should be enabled.
     */
    private Checkbox backupToTape;
    /**
     * Timebox to select backup to tape start
     */
    private Timebox time;
    /**
     * Datebox to select backup to tape start
     */
    private Datebox day;
    /**
     * Date for backups to tapes to start (combination of day and time from form)
     */
    private Date backupToTapeStartDate;
    /**
     * User authenticated in the system at the moment.
     */
    private String username;
    /**
     * Indicates if scheduled backups were enabled before clicking accept.
     */
    private boolean prevBackupEnabled;
    /**
     * Indicates the interval for scheduled backups before clicking accept.
     */
    private int prevInterval;
    /**
     * Indicates if backups to tape were enabled before clicking accept.
     */
    private boolean prevBackupToTapeEnabled;
    /**
     * Indicates the start date for backups to tape.
     */
    private Date prevBackupToTapeDate;
    

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
        this.jobDAO = new DODJobDAO();

        //Basic window properties
        this.setId("backupWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_BACKUP_TITLE));
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("510px");

        //Main box used to apply pading
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);
        
        //Groupbox containing backup now
        Groupbox backupNow = new Groupbox();
        backupNow.setClosable(false);
        Caption titleBackupNow = new Caption();
        titleBackupNow.setLabel(Labels.getLabel(DODConstants.LABEL_BACKUP_NOW_TITLE));
        backupNow.appendChild(titleBackupNow);
        //Box containing the button to create backup now
        Hbox backupNowBox =  new Hbox();
        backupNowBox.setStyle("margin-bottom:10px;");
        backupNowBox.setHeight("24px");
        backupNowBox.setAlign("bottom");
        Toolbarbutton backupNowButton = new Toolbarbutton();
        backupNowButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_BACKUP_NOW));
        backupNowButton.setZclass(DODConstants.STYLE_BUTTON);
        backupNowButton.setImage(DODConstants.IMG_BACKUP);
        backupNowButton.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doBackupNow();
            }
        });
        backupNowBox.appendChild(backupNowButton);
        Label backupNowLabel = new Label(Labels.getLabel(DODConstants.LABEL_BACKUP_NOW));
        backupNowBox.appendChild(backupNowLabel);
        backupNow.appendChild(backupNowBox);
        mainBox.appendChild(backupNow);
        
        //Groupbox containing backup configuration
        Groupbox backupConfig = new Groupbox();
        backupConfig.setStyle("margin-top:10px");
        backupConfig.setClosable(false);
        Caption titleConfig = new Caption();
        titleConfig.setLabel(Labels.getLabel(DODConstants.LABEL_BACKUP_CONFIG));
        backupConfig.appendChild(titleConfig);
        Vbox configContent = new Vbox();

        //Initialise scheduled backup configuration
        initScheduledBackup();
        //Box containing the checkbox for automatic backups and the interval
        Hbox autoBox =  new Hbox();
        autoBox.setAlign("bottom");
        //Create checkbox for automatic backups
        automatic = new Checkbox();
        automatic.setLabel(Labels.getLabel(DODConstants.LABEL_AUTOMATIC_BACKUP));
        automatic.setChecked(prevBackupEnabled);
        autoBox.appendChild(automatic);
        //Create spinner for the interval
        interval = new Spinner();
        if (prevBackupEnabled)
            interval.setValue(prevInterval);
        else
            interval.setValue(DODConstants.DEFAULT_INTERVAL_HOURS);
        interval.setWidth("50px");
        interval.setConstraint("min " + DODConstants.MIN_INTERVAL_HOURS);
        autoBox.appendChild(interval);
        autoBox.appendChild(new Label(Labels.getLabel(DODConstants.LABEL_HOURS)));
        configContent.appendChild(autoBox);
        
        
        //Box containing the checkbox for automatic backups and the interval
        Hbox tapeBox =  new Hbox();
        tapeBox.setAlign("bottom");
        //Create checkbox for backups to tape
        initBackupToTape();
        backupToTape = new Checkbox();
        backupToTape.setLabel(Labels.getLabel(DODConstants.LABEL_BACKUP_TO_TAPE));
        backupToTape.setChecked(prevBackupToTapeEnabled);
        tapeBox.appendChild(backupToTape);
        //Create date and time choosers
        day = new Datebox();
        day.setFormat(DODConstants.DATE_FORMAT);
        day.setWidth("90px");
        tapeBox.appendChild(day);
        time = new Timebox();
        tapeBox.appendChild(time);        
        configContent.appendChild(tapeBox);
        if (prevBackupToTapeEnabled) {
            day.setValue(prevBackupToTapeDate);
            time.setValue(prevBackupToTapeDate);
        }
        else {
            Date now = new Date();
            day.setValue(now);
            time.setValue(now);
        }
        
        //Create warning for backups to tape
        if (!prevBackupToTapeEnabled) {
            Label backupToTapeWarning = new Label(Labels.getLabel(DODConstants.LABEL_BACKUP_TO_TAPE_WARNING));
            backupToTapeWarning.setStyle("margin-left:20px;color:red;font-size:xx-small");
            configContent.appendChild(backupToTapeWarning);
        }
        
        //Apply changes button
        Div applyChangesDiv = new Div();
        applyChangesDiv.setWidth("100%");
        Hbox applyChangesBox = new Hbox();
        applyChangesBox.setHeight("24px");
        applyChangesBox.setAlign("bottom");
        applyChangesBox.setStyle("float:right;");
        Label applyChangesLabel = new Label(Labels.getLabel(DODConstants.LABEL_APPLY_CHANGES));
        applyChangesLabel.setSclass(DODConstants.STYLE_TITLE);
        applyChangesLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        applyChangesLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doApplyChanges();
            }
        });
        applyChangesBox.appendChild(applyChangesLabel);
        Toolbarbutton applyChangesButton = new Toolbarbutton();
        applyChangesButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_APPLY_CHANGES));
        applyChangesButton.setZclass(DODConstants.STYLE_BUTTON);
        applyChangesButton.setImage(DODConstants.IMG_ACCEPT);
        applyChangesButton.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doApplyChanges();
            }
        });
        applyChangesBox.appendChild(applyChangesButton);
        applyChangesDiv.appendChild(applyChangesBox);
        configContent.appendChild(applyChangesDiv);
        
        //Append groupbox to mainbox
        backupConfig.appendChild(configContent);
        mainBox.appendChild(backupConfig);

        //Div for accept and cancel buttons
        Div closeDiv = new Div();
        closeDiv.setWidth("100%");
        //Cancel button
        Hbox closeBox = new Hbox();
        closeBox.setHeight("24px");
        closeBox.setAlign("bottom");
        closeBox.setStyle("float:left;");
        Toolbarbutton closeButton = new Toolbarbutton();
        closeButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CLOSE));
        closeButton.setZclass(DODConstants.STYLE_BUTTON);
        closeButton.setImage(DODConstants.IMG_CANCEL);
        closeButton.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doClose();
            }
        });
        closeBox.appendChild(closeButton);
        Label closeLabel = new Label(Labels.getLabel(DODConstants.LABEL_CLOSE));
        closeLabel.setSclass(DODConstants.STYLE_TITLE);
        closeLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        closeLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doClose();
            }
        });
        closeBox.appendChild(closeLabel);
        closeDiv.appendChild(closeBox);
        mainBox.appendChild(closeDiv);
    }
    
    /**
     * Instatiates the fields for the current scheduled backup configuration.
     */
    private void initScheduledBackup () {
        prevInterval = jobDAO.getBackupInterval(instance);
        if (prevInterval > 0)
            prevBackupEnabled = true;
        else
            prevBackupEnabled = false;
    }
    
    /**
     * Instatiates the fields for the current backup to tape configuration.
     */
    private void initBackupToTape () {
        prevBackupToTapeDate = jobDAO.getBackupToTapeStartDate(instance);
        if (prevBackupToTapeDate != null)
            prevBackupToTapeEnabled = true;
        else
            prevBackupToTapeEnabled = false;
    }
    
    /**
     * Disables the automatic backups created in a previous job.
     */
    private void doBackupNow() {
        boolean result = jobHelper.doBackup(instance, username, 0);
        if (!result) {
            showError(DODConstants.ERROR_DISPATCHING_JOB);
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
     * Method executed when user applies changes. A job is created and the window is detached.
     */
    private void doApplyChanges() {
        boolean result = false;
        //If there are no previous error messages on the interval component
        if (isConfigValid()) {
            //If backup to tape has changed or the date has changed
            boolean backupToTapeResult = false;
            if (backupToTape.isChecked() != prevBackupToTapeEnabled || (backupToTape.isChecked() && !backupToTapeStartDate.equals(prevBackupToTapeDate))) {
                //If automatic backups to tape are checked
                if (backupToTape.isChecked()) {
                    if (jobHelper.isAdminMode())
                        backupToTapeResult = jobDAO.createScheduledBackupToTape(instance, username, backupToTapeStartDate, 1);
                    else
                        backupToTapeResult = jobDAO.createScheduledBackupToTape(instance, username, backupToTapeStartDate, 0); 
                }
                //If automatic backups to tape are not checked disable them
                else {
                    if (jobHelper.isAdminMode())
                        backupToTapeResult = jobDAO.deleteScheduledBackupToTape(instance, username, 1);
                    else
                        backupToTapeResult = jobDAO.deleteScheduledBackupToTape(instance, username, 0);
                }
            }
            else {
                backupToTapeResult = true;
            }
            //If the operation was succesful continue with automatic backups
            if (backupToTapeResult) {
                //If scheduled backups changed
                if (automatic.isChecked() != prevBackupEnabled || (automatic.isChecked() && interval.getValue() != prevInterval)) {
                    //If automatic backups are checked
                    if (automatic.isChecked()) {
                        //If the interval has changed (if they just enable it, it will always change because the previous value was 0)
                        if ((interval.getValue() != prevInterval) && interval.getValue() != null && interval.getValue() > 0)
                            result = jobHelper.doBackup(instance, username, interval.getValue());
                    }
                    //If automatic backups are not checked disable them
                    else {
                        if (jobHelper.isAdminMode())
                            result = jobDAO.deleteScheduledBackup(instance, username, 1);
                        else
                            result = jobDAO.deleteScheduledBackup(instance, username, 0);
                    }
                }
                else {
                    result = true;
                }
            }
            //If the operation was successful update instance status
            if (backupToTapeResult && result) {
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
     * Checks that the configuration parameters are valid
     * @return 
     */
    public boolean isConfigValid() {
        boolean intervalValid = true;
        boolean dateValid = true;
        //Check interval (restriction on component)
        if (automatic.isChecked() && (interval.getErrorMessage() != null && !interval.getErrorMessage().isEmpty())) {
            intervalValid = false;
        }
        //Check start date for backups to tape
        if (backupToTape.isChecked()) {
            if (day.getValue() != null) {
                if (time.getValue() != null) {
                    Calendar dayPart = Calendar.getInstance();
                    dayPart.setTime(day.getValue());
                    Calendar timePart = Calendar.getInstance();
                    timePart.setTime(time.getValue());
                    Calendar dayTime = Calendar.getInstance();
                    dayTime.set(dayPart.get(Calendar.YEAR), dayPart.get(Calendar.MONTH), dayPart.get(Calendar.DAY_OF_MONTH),
                                timePart.get(Calendar.HOUR_OF_DAY), timePart.get(Calendar.MINUTE), timePart.get(Calendar.SECOND));
                    backupToTapeStartDate = dayTime.getTime();
                    if (backupToTapeStartDate.compareTo(new Date()) <= 0) {
                        time.setErrorMessage(Labels.getLabel(DODConstants.ERROR_BACKUP_TO_TAPE_DATE));
                        dateValid = false;
                    }
                }
                else {
                    time.setErrorMessage(Labels.getLabel(DODConstants.ERROR_BACKUP_TO_TAPE_TIME_EMPTY));
                    dateValid = false;
                }
            }
            else
            {
                day.setErrorMessage(Labels.getLabel(DODConstants.ERROR_BACKUP_TO_TAPE_DAY_EMPTY));
                dateValid = false;
            }
        }
        return intervalValid && dateValid;
    }

    /**
     * Method executed when the user cancels the form. The window is detached from the page.
     */
    private void doClose() {
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
