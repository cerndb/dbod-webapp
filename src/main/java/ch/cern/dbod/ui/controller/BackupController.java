/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;

import ch.cern.dbod.db.dao.JobDAO;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.ui.model.OverviewTreeModel;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.JobHelper;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.A;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Spinner;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the backup management window. Creates the window and all its components.
 * @author Daniel Gomez Blanco
 */
public class BackupController extends Window {

    /**
     * Instance being managed at the moment.
     */
    private Instance instance;
    /**
     * Helper to execute jobs.
     */
    private JobHelper jobHelper;
    /**
     * DAO for jobs.
     */
    private JobDAO jobDAO;
    /**
     * Checkbox to indicate if snapshots should be taken automatically every x hours.
     */
    private Checkbox automatic;
    /**
     * Number of hours between automatic backups.
     */
    private Spinner interval;
    /**
     * Timebox to select backup start
     */
    private Timebox backupTime;
    /**
     * Datebox to select backup start
     */
    private Datebox backupDay;
    /**
     * Date for backups to start (combination of day and time from form)
     */
    private Date backupStartDate;
    /**
     * Checkbox to indicate if backups to tape should be enabled.
     */
    private Checkbox backupToTape;
    /**
     * Timebox to select backup to tape start
     */
    private Timebox tapeTime;
    /**
     * Datebox to select backup to tape start
     */
    private Datebox tapeDay;
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
     * Indicates the start date for backups.
     */
    private Date prevBackupDate;
    /**
     * Indicates if backups to tape were enabled before clicking accept.
     */
    private boolean prevBackupToTapeEnabled;
    /**
     * Indicates the start date for backups to tape.
     */
    private Date prevBackupToTapeDate;
    /**
     * Model of the tree (null if we are in list view).
     */
    private OverviewTreeModel model;

    /**
     * Constructor for this window (coming from instance view)
     * @param inst instance to be managed.
     * @param user username for the authenticated user.
     * @param jobHelper helper to execute jobs.
     * @throws InterruptedException if the window cannot be created.
     */
    public BackupController(Instance inst, String user, JobHelper jobHelper) throws InterruptedException {
        this(inst, user, jobHelper, null);
    }
    
    /**
     * Constructor for this window (coming from list view)
     * @param inst instance to be managed.
     * @param user username for the authenticated user.
     * @param jobHelper helper to execute jobs.
     * @param model model of the tree (null if we are in instance view).
     * @throws InterruptedException if the window cannot be created.
     */
    public BackupController(Instance inst, String user, JobHelper jobHelper, OverviewTreeModel model) throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance and create job helper
        this.instance = inst;
        this.username = user;
        this.jobHelper = jobHelper;
        this.jobDAO = new JobDAO();
        
        //Initialise model
        this.model = model;

        //Basic window properties
        this.setId("backupWindow");
        this.setTitle(Labels.getLabel(CommonConstants.LABEL_BACKUP_TITLE) + " " + instance.getDbName());
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("530px");
        
        //Main box used to apply pading
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);
        
        //Groupbox containing backup now
        Groupbox backupNow = new Groupbox();
        backupNow.setClosable(false);
        Caption titleBackupNow = new Caption();
        titleBackupNow.setLabel(Labels.getLabel(CommonConstants.LABEL_BACKUP_NOW_TITLE));
        backupNow.appendChild(titleBackupNow);
        //Box containing the button to create backup now
        Hbox backupNowBox =  new Hbox();
        backupNowBox.setStyle("margin-bottom:10px;");
        backupNowBox.setHeight("24px");
        backupNowBox.setAlign("bottom");
        Toolbarbutton backupNowButton = new Toolbarbutton();
        backupNowButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_BACKUP_NOW));
        backupNowButton.setZclass(CommonConstants.STYLE_BUTTON);
        backupNowButton.setImage(CommonConstants.IMG_BACKUP);
        backupNowButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doBackupNow();
            }
        });
        backupNowBox.appendChild(backupNowButton);
        Label backupNowLabel = new Label(Labels.getLabel(CommonConstants.LABEL_BACKUP_NOW));
        backupNowBox.appendChild(backupNowLabel);
        backupNow.appendChild(backupNowBox);
        mainBox.appendChild(backupNow);
        
        //Groupbox containing backup configuration
        Groupbox backupConfig = new Groupbox();
        backupConfig.setStyle("margin-top:10px");
        backupConfig.setClosable(false);
        Caption titleConfig = new Caption();
        titleConfig.setLabel(Labels.getLabel(CommonConstants.LABEL_BACKUP_CONFIG));
        backupConfig.appendChild(titleConfig);
        Vbox configContent = new Vbox();

        //Initialise scheduled backup configuration
        initScheduledBackup();
        //Box containing the checkbox for automatic backups and the interval
        Hbox autoBox =  new Hbox();
        autoBox.setAlign("bottom");
        //Create checkbox for automatic backups
        automatic = new Checkbox();
        automatic.setLabel(Labels.getLabel(CommonConstants.LABEL_AUTOMATIC_BACKUP));
        automatic.setChecked(prevBackupEnabled);
        autoBox.appendChild(automatic);
        //Create spinner for the interval
        interval = new Spinner();
        if (prevBackupEnabled)
            interval.setValue(prevInterval);
        else
            interval.setValue(CommonConstants.DEFAULT_INTERVAL_HOURS);
        interval.setWidth("50px");
        interval.setConstraint("min " + CommonConstants.MIN_INTERVAL_HOURS);
        autoBox.appendChild(interval);
        autoBox.appendChild(new Label(Labels.getLabel(CommonConstants.LABEL_HOURS)));
        //Create date and time choosers
        backupDay = new Datebox();
        backupDay.setFormat(CommonConstants.DATE_FORMAT);
        backupDay.setWidth("90px");
        autoBox.appendChild(backupDay);
        backupTime = new Timebox();
        backupTime.setWidth("80px");
        autoBox.appendChild(backupTime);        
        configContent.appendChild(autoBox);
        if (prevBackupEnabled) {
            backupDay.setValue(prevBackupDate);
            backupTime.setValue(prevBackupDate);
        }
        else {
            Date now = new Date();
            backupDay.setValue(now);
            backupTime.setValue(now);
        }
        configContent.appendChild(autoBox);
        
        
        //Box containing the checkbox for automatic backups and the interval
        Hbox tapeBox =  new Hbox();
        tapeBox.setAlign("bottom");
        //Create checkbox for backups to tape
        initBackupToTape();
        backupToTape = new Checkbox();
        backupToTape.setLabel(Labels.getLabel(CommonConstants.LABEL_BACKUP_TO_TAPE));
        backupToTape.setChecked(prevBackupToTapeEnabled);
        //Limit tape backups to admins (disable backups enabled for everyone)
        if (!jobHelper.isAdminMode() && !prevBackupToTapeEnabled) {
            backupToTape.setDisabled(true);
        }
        tapeBox.appendChild(backupToTape);
        //Create date and time choosers
        tapeDay = new Datebox();
        tapeDay.setFormat(CommonConstants.DATE_FORMAT);
        tapeDay.setWidth("90px");
        tapeDay.setStyle("margin-left:9px");
        tapeBox.appendChild(tapeDay);
        tapeTime = new Timebox();
        tapeTime.setWidth("80px");
        tapeBox.appendChild(tapeTime);        
        configContent.appendChild(tapeBox);
        if (prevBackupToTapeEnabled) {
            tapeDay.setValue(prevBackupToTapeDate);
            tapeTime.setValue(prevBackupToTapeDate);
        }
        else {
            Date now = new Date();
            tapeDay.setValue(now);
            tapeTime.setValue(now);
        }
        //Limit change tapeDay and tapeTime only to admins
        if (!jobHelper.isAdminMode()) {
            tapeDay.setDisabled(true);
            tapeTime.setDisabled(true);
        }
        //Create warning for backups to tape
        if (!prevBackupToTapeEnabled) {
            Hbox warningBox = new Hbox();
            warningBox.setWidth("100%");
            Label backupToTapeWarning = new Label(Labels.getLabel(CommonConstants.LABEL_BACKUP_TO_TAPE_WARNING));
            backupToTapeWarning.setStyle("margin-left:25px;color:red;font-size:12px");
            warningBox.appendChild(backupToTapeWarning);
            A backupToTapeLink = new A();
            backupToTapeLink.setHref("https://cern.service-now.com/service-portal/report-ticket.do?name=request&amp;se=database-on-demand");
            backupToTapeLink.setLabel(Labels.getLabel(CommonConstants.LABEL_BACKUP_TO_TAPE_LINK));
            backupToTapeLink.setTarget("_blank");
            backupToTapeLink.setStyle("font-size:12px");
            warningBox.appendChild(backupToTapeLink);
            configContent.appendChild(warningBox);
        }
        
        //Apply changes button
        Div applyChangesDiv = new Div();
        applyChangesDiv.setWidth("100%");
        Hbox applyChangesBox = new Hbox();
        applyChangesBox.setHeight("24px");
        applyChangesBox.setAlign("bottom");
        applyChangesBox.setStyle("float:right;");
        Label applyChangesLabel = new Label(Labels.getLabel(CommonConstants.LABEL_APPLY_CHANGES));
        applyChangesLabel.setSclass(CommonConstants.STYLE_TITLE);
        applyChangesLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        applyChangesLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doApplyChanges();
            }
        });
        applyChangesBox.appendChild(applyChangesLabel);
        Toolbarbutton applyChangesButton = new Toolbarbutton();
        applyChangesButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_APPLY_CHANGES));
        applyChangesButton.setZclass(CommonConstants.STYLE_BUTTON);
        applyChangesButton.setImage(CommonConstants.IMG_ACCEPT);
        applyChangesButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
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
        closeButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_CLOSE));
        closeButton.setZclass(CommonConstants.STYLE_BUTTON);
        closeButton.setImage(CommonConstants.IMG_CANCEL);
        closeButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doClose();
            }
        });
        closeBox.appendChild(closeButton);
        Label closeLabel = new Label(Labels.getLabel(CommonConstants.LABEL_CLOSE));
        closeLabel.setSclass(CommonConstants.STYLE_TITLE);
        closeLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        closeLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
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
        prevBackupDate = jobDAO.getBackupStartDate(instance);
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
        boolean result = jobHelper.doBackup(instance, username);
        if (!result) {
            showError(CommonConstants.ERROR_DISPATCHING_JOB);
        }
        else {
            //If we are in the overview page
            if (model != null) {
                //Reload the node
                model.updateInstance(instance);
            } //If we are in the instance page
            else if (interval.getRoot().getFellowIfAny("controller") != null && interval.getRoot().getFellow("controller") instanceof InstanceController) {
                InstanceController controller = (InstanceController) interval.getRoot().getFellow("controller");
                controller.refreshInfo();
            }
            interval.getFellow("backupWindow").detach();
        }
    }

    /**
     * Method executed when user applies changes. A job is created and the window is detached.
     */
    private void doApplyChanges() {
        boolean result = false;
        //If there are no previous error messages
        if (isConfigValid()) {
            //If backup to tape has changed or the date has changed
            boolean backupToTapeResult;
            if (backupToTape.isChecked() != prevBackupToTapeEnabled
                    || (backupToTape.isChecked() && !backupToTapeStartDate.equals(prevBackupToTapeDate))) {
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
                if (automatic.isChecked() != prevBackupEnabled
                        || (automatic.isChecked() && interval.getValue() != prevInterval)
                        || (automatic.isChecked() && !backupStartDate.equals(prevBackupDate))) {
                    //If automatic backups are checked
                    if (automatic.isChecked()) {
                        if (jobHelper.isAdminMode())
                            result = jobDAO.createScheduledBackup(instance, username, backupStartDate, interval.getValue(), 1);
                        else
                            result = jobDAO.createScheduledBackup(instance, username, backupStartDate, interval.getValue(), 0);
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
                if (model != null) {
                    //Reload the node
                    model.updateInstance(instance);
                } //If we are in the instance page
                else if (interval.getRoot().getFellowIfAny("controller") != null && interval.getRoot().getFellow("controller") instanceof InstanceController) {
                    InstanceController controller = (InstanceController) interval.getRoot().getFellow("controller");
                    controller.afterCompose();
                }
                interval.getFellow("backupWindow").detach();
            }
            else
                showError(CommonConstants.ERROR_DISPATCHING_JOB);
        }
    }
    
    /**
     * Checks that the configuration parameters are valid
     * @return true if the configuration is valid, false otherwise
     */
    public boolean isConfigValid() {
        boolean intervalValid = true;
        boolean tapeDateValid = true;
        boolean backupDateValid = true;
        //Check interval (restriction on component)
        if (automatic.isChecked() && (interval.getErrorMessage() != null && !interval.getErrorMessage().isEmpty())) {
            intervalValid = false;
        }
        //Check start date for backups
        if (automatic.isChecked()) {
            if (backupDay.getValue() != null) {
                if (backupTime.getValue() != null) {
                    Calendar dayPart = new GregorianCalendar();
                    dayPart.setTime(backupDay.getValue());
                    Calendar timePart = new GregorianCalendar();
                    timePart.setTime(backupTime.getValue());
                    Calendar dayTime = new GregorianCalendar();
                    dayTime.clear();
                    dayTime.set(dayPart.get(Calendar.YEAR), dayPart.get(Calendar.MONTH), dayPart.get(Calendar.DAY_OF_MONTH),
                                timePart.get(Calendar.HOUR_OF_DAY), timePart.get(Calendar.MINUTE), timePart.get(Calendar.SECOND));
                    backupStartDate = dayTime.getTime();
                    //If date has changed and it is not in the future show error
                    if (backupStartDate.compareTo(new Date()) <= 0
                            && !backupStartDate.equals(prevBackupDate)) {
                        backupTime.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_BACKUP_DATE));
                        backupDateValid = false;
                    }
                }
                else {
                    backupTime.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_BACKUP_TIME_EMPTY));
                    backupDateValid = false;
                }
            }
            else
            {
                backupDay.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_BACKUP_DAY_EMPTY));
                backupDateValid = false;
            }
        }
        //Check start date for backups to tape
        if (backupToTape.isChecked()) {
            if (tapeDay.getValue() != null) {
                if (tapeTime.getValue() != null) {
                    Calendar dayPart = new GregorianCalendar();
                    dayPart.setTime(tapeDay.getValue());
                    Calendar timePart = new GregorianCalendar();
                    timePart.setTime(tapeTime.getValue());
                    Calendar dayTime = new GregorianCalendar();
                    dayTime.clear();
                    dayTime.set(dayPart.get(Calendar.YEAR), dayPart.get(Calendar.MONTH), dayPart.get(Calendar.DAY_OF_MONTH),
                                timePart.get(Calendar.HOUR_OF_DAY), timePart.get(Calendar.MINUTE), timePart.get(Calendar.SECOND));
                    backupToTapeStartDate = dayTime.getTime();
                    //If date has changed and it is not in the future show error
                    if (backupToTapeStartDate.compareTo(new Date()) <= 0
                            && !backupToTapeStartDate.equals(prevBackupToTapeDate)) {
                        tapeTime.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_BACKUP_TO_TAPE_DATE));
                        tapeDateValid = false;
                    }
                }
                else {
                    tapeTime.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_BACKUP_TO_TAPE_TIME_EMPTY));
                    tapeDateValid = false;
                }
            }
            else
            {
                tapeDay.setErrorMessage(Labels.getLabel(CommonConstants.ERROR_BACKUP_TO_TAPE_DAY_EMPTY));
                tapeDateValid = false;
            }
        }
        return intervalValid && backupDateValid && tapeDateValid;
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
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
