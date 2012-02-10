package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODJobDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODJob;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import ch.cern.dod.util.JobHelper;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.xml.rpc.ServiceException;
import org.apache.axis.AxisFault;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * Controller for the instance page.
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class InstanceController extends Hbox implements AfterCompose, BeforeCompose {
    /**
     * Upgrade DAO
     */
    private DODUpgradeDAO upgradeDAO;
    /**
     * Instance DAO
     */
    private DODInstanceDAO instanceDAO;
    /**
     * DAO to load jobs
     */
    DODJobDAO jobDAO;
    /**
     * Username for the authenticated user.
     */
    String username;
    /**
     * List of upgrades.
     */
    private List<DODUpgrade> upgrades;
    /**
     * Instance being managed at the moment.
     */
    DODInstance instance;
    /**
     * List of jobs performed on this instance.
     */
    List<DODJob> jobs;
    /**
     * Helper to execute jobs.
     */
    JobHelper jobHelper;
    /**
     * Date formatter for days.
     */
    DateFormat dateFormatter;
    /**
     * Date formatter for times.
     */
    DateFormat dateTimeFormatter;
    /**
     * Helper to manage e-groups
     */
    EGroupHelper eGroupHelper;

    /**
     * User creating the instance.
     */
    private String fullName;
    /**
     * CCID of the user creating the instance.
     */
    private long userCCID;
    /**
     * Indicates if the user is admin
     */
    private boolean admin;
    
    /**
     * Method executed before composing the page. It instantiates the necessary attributes.
     */
    public void beforeCompose() {
        //Get username and adminMode from headers
        Execution execution = Executions.getCurrent();
        username = execution.getHeader(DODConstants.ADFS_LOGIN);
        String eGroups = execution.getHeader(DODConstants.ADFS_GROUP);
        Boolean adminMode = (Boolean) EGroupHelper.groupInList(DODConstants.ADMIN_E_GROUP, eGroups);
        admin = adminMode.booleanValue();
        
        //Get user and password for the web services account
        String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_USER);
        String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_PSWD);
        fullName = execution.getHeader(DODConstants.ADFS_FULLNAME);
        String userCCIDText = execution.getHeader(DODConstants.ADFS_CCID);
        if (userCCIDText != null && !userCCIDText.isEmpty())
            userCCID = Long.parseLong(execution.getHeader(DODConstants.ADFS_CCID));
        else
            userCCID = new Long(0);
        
        //Get instance and amdmin mode from session attributes
        instance = (DODInstance) Sessions.getCurrent().getAttribute(DODConstants.INSTANCE);
        jobHelper = new JobHelper(admin);
        dateFormatter = new SimpleDateFormat(DODConstants.DATE_FORMAT);
        dateTimeFormatter = new SimpleDateFormat(DODConstants.DATE_TIME_FORMAT);
        upgradeDAO = new DODUpgradeDAO();
        instanceDAO = new DODInstanceDAO();
        jobDAO = new DODJobDAO();
        eGroupHelper = new EGroupHelper(wsUser, wsPswd);
        
        //Select upgrades
        upgrades = upgradeDAO.selectAll();
        //Query the database for the most recent version of this instance
        instance = instanceDAO.selectById(instance.getUsername(), instance.getDbName(), upgrades);
    }

    /**
     * Method executed after composing the page. It loads instance info, buttons and jobs.
     */
    public void afterCompose() {
        //Configure input fields
        ((Textbox) getFellow("eGroupEdit")).setMaxlength(DODConstants.MAX_E_GROUP_LENGTH);
        ((Datebox) getFellow("expiryDateEdit")).setFormat(DODConstants.DATE_FORMAT);
        ((Datebox) getFellow("expiryDateEdit")).setTimeZonesReadonly(true);
        ((Textbox) getFellow("projectEdit")).setMaxlength(DODConstants.MAX_PROJECT_LENGTH);
        ((Textbox) getFellow("descriptionEdit")).setMaxlength(DODConstants.MAX_DESCRIPTION_LENGTH);

        //Load instance info if necessary
        if (instance != null) {
            //Load information for this instance
            loadInstanceInfo();
            //Load buttons
            loadButtons();
            //Load jobs
            loadJobs();
        }
    }
    
    /**
     * Getter for the admin attribute.
     * @return true if the user is an admin, false otherwise.
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Refreshes the info for this instance.
     */
    public void refreshInfo () {
        //Select upgrades
        upgrades = upgradeDAO.selectAll();
        //Query the database for the most recent version of this instance
        instance = instanceDAO.selectById(instance.getUsername(), instance.getDbName(), upgrades);
        if (instance != null) {
            //Load information for this instance
            loadInstanceInfo();
            //Load buttons
            loadButtons();
            //Load jobs
            loadJobs();
        }
    }

    /**
     * Loads information related to the current instance.
     */
    private void loadInstanceInfo() {
        //Title
        ((Label) getFellow("instanceTitle")).setValue(Labels.getLabel(DODConstants.LABEL_INSTANCE_TITLE) + " " + instance.getDbName());
        ((Label) getFellow("username")).setValue(instance.getUsername());

        //e-group (if any)
        if (instance.getEGroup() != null && !instance.getEGroup().isEmpty()) {
            ((Label) getFellow("eGroup")).setValue(instance.getEGroup());
            ((Textbox) getFellow("eGroupEdit")).setValue(instance.getEGroup());
        } else {
            ((Label) getFellow("eGroup")).setValue("-");
        }

        //category
        ((Label) getFellow("category")).setValue(Labels.getLabel(DODConstants.LABEL_CATEGORY + instance.getCategory()));

        //e-group (if any)
        if (instance.getProject() != null && !instance.getProject().isEmpty()) {
            ((Label) getFellow("project")).setValue(instance.getProject());
            ((Textbox) getFellow("projectEdit")).setValue(instance.getProject());
        } else {
            ((Label) getFellow("project")).setValue("-");
        }

        //Creation date
        ((Label) getFellow("creationDate")).setValue(dateFormatter.format(instance.getCreationDate()));

        //Expiry date (if any)
        if (instance.getExpiryDate() != null) {
            ((Label) getFellow("expiryDate")).setValue(dateFormatter.format(instance.getExpiryDate()));
            ((Datebox) getFellow("expiryDateEdit")).setValue(instance.getExpiryDate());
        } else {
            ((Label) getFellow("expiryDate")).setValue("-");
        }

        //DB type and size
        ((Label) getFellow("dbType")).setValue(Labels.getLabel(DODConstants.LABEL_DB_TYPE + instance.getDbType()));
        ((Label) getFellow("dbSize")).setValue(instance.getDbSize() + " GB");
            
        //DB connections (if any)
        if (instance.getNoConnections() > 0) {
            ((Label) getFellow("noConnections")).setValue(String.valueOf(instance.getNoConnections()));
        } else {
            ((Label) getFellow("noConnections")).setValue("-");
        }

        //Description (if any)
        if (instance.getDescription() != null && !instance.getDescription().isEmpty()) {
            ((Label) getFellow("description")).setValue(instance.getDescription());
            ((Textbox) getFellow("descriptionEdit")).setValue(instance.getDescription());
        } else {
            ((Label) getFellow("description")).setValue("-");
        }

        //State
        Image stateImage = (Image) getFellow("stateImage");
        Label stateLabel = (Label) getFellow("stateLabel");
        stateImage.setWidth("20px");
        stateImage.setHeight("20px");
        stateLabel.setValue(Labels.getLabel(DODConstants.LABEL_STATE + instance.getState()));
        stateImage.setTooltiptext(Labels.getLabel(DODConstants.LABEL_STATE + instance.getState()));
        if (instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
            stateImage.setSrc(DODConstants.IMG_AWAITING_APPROVAL);
        } else if (instance.getState().equals(DODConstants.INSTANCE_STATE_JOB_PENDING)) {
            stateImage.setSrc(DODConstants.IMG_PENDING);
        } else if (instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)) {
            stateImage.setSrc(DODConstants.IMG_RUNNING);
        } else if (instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            stateImage.setSrc(DODConstants.IMG_STOPPED);
        } else if (instance.getState().equals(DODConstants.INSTANCE_STATE_MAINTENANCE)) {
            stateImage.setSrc(DODConstants.IMG_MAINTENANCE);
        }
        
        //If the user is an admin
        if (admin) {
            //Maintenance button
            if (instance != null && instance.getState().equals(DODConstants.INSTANCE_STATE_MAINTENANCE)){
                ((Toolbarbutton) getFellow("setMaintenanceBtn")).setStyle("display:none");
                ((Toolbarbutton) getFellow("unsetMaintenanceBtn")).setStyle("display:block");
            }
            else{
                ((Toolbarbutton) getFellow("setMaintenanceBtn")).setStyle("display:block");
                ((Toolbarbutton) getFellow("unsetMaintenanceBtn")).setStyle("display:none");
            }
            //Values for edit boxes
            ((Combobox) getFellow("categoryEdit")).getItemAtIndex(0).setValue(DODConstants.CATEGORY_OFFICIAL);
            ((Combobox) getFellow("categoryEdit")).getItemAtIndex(1).setValue(DODConstants.CATEGORY_PERSONAL);
            ((Combobox) getFellow("categoryEdit")).getItemAtIndex(2).setValue(DODConstants.CATEGORY_TEST);
            if (DODConstants.CATEGORY_OFFICIAL.equals(instance.getCategory()))
                ((Combobox) getFellow("categoryEdit")).setSelectedIndex(0);
            else if (DODConstants.CATEGORY_PERSONAL.equals(instance.getCategory()))
                ((Combobox) getFellow("categoryEdit")).setSelectedIndex(1);
            else if (DODConstants.CATEGORY_TEST.equals(instance.getCategory()))
                ((Combobox) getFellow("categoryEdit")).setSelectedIndex(2);
            ((Textbox) getFellow("dbSizeEdit")).setValue(String.valueOf(instance.getDbSize()));
            if (instance.getNoConnections() > 0)
                ((Textbox) getFellow("noConnectionsEdit")).setValue(String.valueOf(instance.getNoConnections()));
        }
    }

    /**
     * Loads the buttons (enabled or disabled depending on the instance state).
     */
    private void loadButtons() {
        //Startup button
        Toolbarbutton startupBtn = (Toolbarbutton) getFellow("startup");
        //Only enable button if the instance is stopped
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            startupBtn.setDisabled(true);
            startupBtn.setSclass(DODConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            startupBtn.setDisabled(false);
            startupBtn.setSclass(DODConstants.STYLE_BIG_BUTTON);
        }

        //Shutdown button
        final Toolbarbutton shutdownBtn = (Toolbarbutton) getFellow("shutdown");
        //Only enable button if the instance is running
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)) {
            shutdownBtn.setDisabled(true);
            shutdownBtn.setSclass(DODConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            shutdownBtn.setDisabled(false);
            shutdownBtn.setSclass(DODConstants.STYLE_BIG_BUTTON);
        }

        //config files button
        final Toolbarbutton configBtn = (Toolbarbutton) getFellow("config");
        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            configBtn.setDisabled(true);
            configBtn.setSclass(DODConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            configBtn.setDisabled(false);
            configBtn.setSclass(DODConstants.STYLE_BIG_BUTTON);
        }

        //Dispatch a backup button
        final Toolbarbutton backupBtn = (Toolbarbutton) getFellow("backup");
        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            backupBtn.setDisabled(true);
            backupBtn.setSclass(DODConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            backupBtn.setDisabled(false);
            backupBtn.setSclass(DODConstants.STYLE_BIG_BUTTON);
        }

        //Dispatch a restore button
        final Toolbarbutton restoreBtn = (Toolbarbutton) getFellow("restore");
        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            restoreBtn.setDisabled(true);
            restoreBtn.setSclass(DODConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            restoreBtn.setDisabled(false);
            restoreBtn.setSclass(DODConstants.STYLE_BIG_BUTTON);
        }
        
        //Upgrade a database button
        final Toolbarbutton upgradeBtn = (Toolbarbutton) getFellow("upgrade");
        //Only enable button if the instance is stopped or running (and there is an upgrade available)
        if ((!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED))
                || instance.getUpgradeTo() == null || instance.getUpgradeTo().isEmpty()) {
            upgradeBtn.setDisabled(true);
            upgradeBtn.setSclass(DODConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            upgradeBtn.setDisabled(false);
            upgradeBtn.setSclass(DODConstants.STYLE_BIG_BUTTON);
        }

        //Access monitoring button
        final Toolbarbutton monitorBtn = (Toolbarbutton) getFellow("monitor");
        //Only enable button if the instance is not awaiting approval
        if (instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
            monitorBtn.setDisabled(true);
            monitorBtn.setSclass(DODConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            monitorBtn.setDisabled(false);
            monitorBtn.setSclass(DODConstants.STYLE_BIG_BUTTON);
        }      
    }

     /**
      * Load the jobs carried out (or pending) for the current instance.
      */
    private void loadJobs() {
        //Get jobs from database
        jobs = jobDAO.selectByInstance(instance);

        //Get selected job (if any)
        Combobox jobSelector = (Combobox) getFellow("jobSelector");
        DODJob selected = null;
        if (jobSelector.getSelectedItem() != null) {
            selected = (DODJob) jobSelector.getSelectedItem().getValue();
        }

        //Remove previous jobs
        jobSelector.getItems().removeAll(jobSelector.getItems());

        //If there are jobs
        if (jobs != null && jobs.size() > 0) {
            //Insert items in combobox
            Comboitem selectOne = new Comboitem();
            selectOne.setValue(null);
            selectOne.setLabel(Labels.getLabel(DODConstants.LABEL_SELECT_ONE));
            jobSelector.appendChild(selectOne);
            for (int i = 0; i < jobs.size(); i++) {
                DODJob job = jobs.get(i);
                Comboitem item = new Comboitem();
                item.setValue(job);
                String label = Labels.getLabel(DODConstants.LABEL_JOB + job.getCommandName()) + " " + dateTimeFormatter.format(job.getCreationDate());
                item.setLabel(label);
                item.setSclass(DODConstants.STYLE_JOB_STATE);
                if (job.getState().equals(DODConstants.JOB_STATE_FINISHED_OK)) {
                    item.setImage(DODConstants.IMG_RUNNING);
                } else if (job.getState().equals(DODConstants.JOB_STATE_RUNNING)) {
                    item.setImage(DODConstants.IMG_PENDING);
                } else if (job.getState().equals(DODConstants.JOB_STATE_FINISHED_FAIL)) {
                    item.setImage(DODConstants.IMG_STOPPED);
                } else if (job.getState().equals(DODConstants.JOB_STATE_PENDING)) {
                    item.setImage(DODConstants.IMG_AWAITING_APPROVAL);
                }
                jobSelector.appendChild(item);
                //If it was the selected one, select it again
                if (selected != null && job.getUsername().equals(selected.getUsername()) && job.getDbName().equals(selected.getDbName())
                        && job.getCommandName().equals(selected.getCommandName()) && job.getType().equals(selected.getType())
                        && job.getCreationDate().getTime() == selected.getCreationDate().getTime()) {
                    jobSelector.setSelectedItem(item);
                }
            }
        }
        else {
            Comboitem selectOne = new Comboitem();
            selectOne.setValue(null);
            selectOne.setLabel(Labels.getLabel(DODConstants.LABEL_NO_JOBS));
            jobSelector.appendChild(selectOne);
        }

        //If there is not a selected item select the first
        if (selected == null) {
            jobSelector.setSelectedIndex(0);
        }

        //Load information for the selected item
        loadJob();
    }

    /**
     * Loads the information for the selected job
     */
    private void loadJob() {
        //Get job
        Combobox jobSelector = (Combobox) getFellow("jobSelector");
        DODJob job = (DODJob) jobSelector.getSelectedItem().getValue();

        //If a job is selected
        if (job != null) {
            //Load job general info
            ((Label) getFellow("jobRequester")).setValue(job.getRequester());
            Image stateImage = (Image) getFellow("jobStateImage");
            Label stateLabel = (Label) getFellow("jobStateLabel");
            stateImage.setWidth("20px");
            stateImage.setHeight("20px");
            stateLabel.setValue(Labels.getLabel(DODConstants.LABEL_JOB_STATE + job.getState()));
            stateImage.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB_STATE + job.getState()));
            if (job.getState().equals(DODConstants.JOB_STATE_PENDING)) {
                stateImage.setSrc(DODConstants.IMG_AWAITING_APPROVAL);
            } else if (job.getState().equals(DODConstants.JOB_STATE_RUNNING)) {
                stateImage.setSrc(DODConstants.IMG_PENDING);
            } else if (job.getState().equals(DODConstants.JOB_STATE_FINISHED_OK)) {
                stateImage.setSrc(DODConstants.IMG_RUNNING);
            } else if (job.getState().equals(DODConstants.JOB_STATE_FINISHED_FAIL)) {
                stateImage.setSrc(DODConstants.IMG_STOPPED);
            }
            ((Label) getFellow("jobCreationDate")).setValue(dateTimeFormatter.format(job.getCreationDate()));
            if (job.getCompletionDate() != null) {
                ((Label) getFellow("jobCompletionDate")).setValue(dateTimeFormatter.format(job.getCompletionDate()));
            } else {
                ((Label) getFellow("jobCompletionDate")).setValue("-");
            }

            //Load log
            DODJobDAO jobDAO = new DODJobDAO();
            ((Textbox) getFellow("log")).setRawValue(jobDAO.selectLogByJob(job));

            //Update groupbox properties
            Groupbox jobInfo = (Groupbox) getFellow("jobInfo");
            jobInfo.setClosable(true);
            jobInfo.setOpen(true);
        }
        //Clean the interface if no job is selected
        else {
            ((Label) getFellow("jobRequester")).setValue("-");
            ((Image) getFellow("jobStateImage")).setSrc("");
            ((Image) getFellow("jobStateImage")).setTooltiptext("");
            ((Label) getFellow("jobStateLabel")).setValue("-");
            ((Label) getFellow("jobCreationDate")).setValue("-");
            ((Label) getFellow("jobCompletionDate")).setValue("-");
            ((Textbox) getFellow("log")).setValue("-");

            //Update groupbox properties
            Groupbox jobInfo = (Groupbox) getFellow("jobInfo");
            jobInfo.setOpen(false);
            jobInfo.setClosable(false);
        }
    }

    /**
     * Creates job to startup the instance.
     */
    public void doStartup() {
        //Create new job and update instance status
        if (jobHelper.doStartup(instance, username)) {
            afterCompose();
        } else {
            showError(null, DODConstants.ERROR_DISPATCHING_JOB);
        }
    }

    /**
     * Creates job to shutdown the instance.
     */
    public void doShutdown() {
        //Create new job and update instance status
        if (jobHelper.doShutdown(instance, username)) {
            afterCompose();
        } else {
            showError(null, DODConstants.ERROR_DISPATCHING_JOB);
        }
    }

    /**
     * Opens the config files window.
     */
    public void doConfig() {
        try {
            ConfigController configController = new ConfigController(instance, username, jobHelper);
            //Only show window if it is not already being diplayed
            if (this.getRoot().getFellowIfAny(configController.getId()) == null) {
                configController.setParent(this.getRoot());
                configController.doModal();
            }
        } catch (InterruptedException ex) {
            showError(ex, DODConstants.ERROR_DISPATCHING_JOB);
        }
    }

    /**
     * Opens the backup window to create a backup job.
     */
    public void doBackup() {
        try {
            BackupController backupController = new BackupController(instance, username, jobHelper);
            //Only show window if it is not already being diplayed
            if (this.getRoot().getFellowIfAny(backupController.getId()) == null) {
                backupController.setParent(this.getRoot());
                backupController.doModal();
            }
        } catch (InterruptedException ex) {
            showError(ex, DODConstants.ERROR_DISPATCHING_JOB);
        }
    }

    /**
     * Opens the restore window to create a restore job.
     */
    public void doRestore() {
        try {
            RestoreController restoreController = new RestoreController(instance, username, jobHelper);
            //Only show window if it is not already being diplayed
            if (this.getRoot().getFellowIfAny(restoreController.getId()) == null) {
                restoreController.setParent(this.getRoot());
                restoreController.doModal();
            }
        } catch (InterruptedException ex) {
            showError(ex, DODConstants.ERROR_DISPATCHING_JOB);
        }
    }

    /**
     * Opens the monitor window.
     */
    public void doMonitor() {
        try {
            MonitoringController monitoringController = new MonitoringController(instance);
            //Only show window if it is not already being diplayed
            if (this.getRoot().getFellowIfAny(monitoringController.getId()) == null) {
                monitoringController.setParent(this.getRoot());
                monitoringController.doModal();
            }
        } catch (InterruptedException ex) {
            showError(ex, DODConstants.ERROR_DISPATCHING_JOB);
        }
    }

    /**
     * Creates a job to destroy this instance.
     * @deprecated Instances are destroyed through FIM.
     */
    public void doDestroy() {
        try {
            DestroyController destroyController = new DestroyController(instance, jobHelper);
            //Only show window if it is not already being diplayed
            if (this.getRoot().getFellowIfAny(destroyController.getId()) == null) {
                destroyController.setParent(this.getRoot());
                destroyController.doModal();
            }
        } catch (InterruptedException ex) {
            showError(ex, DODConstants.ERROR_DISPATCHING_JOB);
        }
    }
    
    /**
     * Creates a job to upgrade this instance.
     */
    public void doUpgrade() {
        try {
            UpgradeController upgradeController = new UpgradeController(instance, username, jobHelper);
            //Only show window if it is not already being diplayed
            if (this.getRoot().getFellowIfAny(upgradeController.getId()) == null) {
                upgradeController.setParent(this.getRoot());
                upgradeController.doModal();
            }
        } catch (InterruptedException ex) {
            showError(ex, DODConstants.ERROR_DISPATCHING_JOB);
        }
    }
    
    /**
     * Sets the state of the machine to under maintenance.
     */
    public void setMaintenance(boolean maintenance) {
        //Clone the instance and override project
        DODInstance clone = instance.clone();
        if (maintenance) {
            clone.setState(DODConstants.INSTANCE_STATE_MAINTENANCE);
        }
        else {
            clone.setState(DODConstants.INSTANCE_STATE_RUNNING);
        }
        if (instanceDAO.update(instance, clone) > 0) {
            instance = clone;
            loadInstanceInfo();
            loadButtons();
            loadJobs();
        }
        else {
            showError(null, DODConstants.ERROR_UPDATING_INSTANCE);
        }
    } 

    /**
     * Checks if an e-group exists, and if it does continues with the editing.
     */
    public void checkAndEditEGroup() {
        //Check for errors in form
        if (isEGroupValid()) {
            //If there is an egroup
            if(((Textbox) getFellow("eGroupEdit")).getValue() != null && !((Textbox) getFellow("eGroupEdit")).getValue().isEmpty()) {
                //Check if e-group exists
                boolean eGroupExists = false;
                try {
                    eGroupExists = eGroupHelper.eGroupExists(((Textbox) getFellow("eGroupEdit")).getValue());
                } catch (AxisFault ex) {
                    eGroupExists = false;
                } catch (ServiceException ex) {
                    ((Textbox) getFellow("eGroupEdit")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_SEARCH));
                    return;
                } catch (RemoteException ex) {
                    ((Textbox) getFellow("eGroupEdit")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_SEARCH));
                    return;
                }
                //If the egroup does not exist show confimation window and return (the creation of e-groups is not allowed when editing)
                if (!eGroupExists) {
                    try {
                        ((Window) getFellow("eGroupConfirm")).doModal();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR OPENING EGROUP CONFIRM WINDOW", ex);
                    } catch (SuspendNotAllowedException ex) {
                        Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR OPENING EGROUP CONFIRM WINDOW", ex);
                    }
                    return;
                }
                //Create instance with this values
                else
                    editEGroup(true);
            }
            //If the egroup is not specified we create the instance as if it already existed
            else
                editEGroup(true);
        }
    }

    /**
     * Edits the egroup of this instance (now always called when egroup exists, the creation of e-groups is not allowed when editing).
     * @param eGroupExists indicates if an egroup has to be created or not.
     */
    public void editEGroup(boolean eGroupExists) {
        //Check for errors in form
        if (isEGroupValid()) {
            try {
                boolean eGroupCreated = false;
                //If the egroup does not exist create it
                if (!eGroupExists) {
                    //If the egroup was successfully created store the instance in the DB
                    eGroupCreated = eGroupHelper.createEGroup(((Textbox) getFellow("eGroupEdit")).getValue(),
                            instance.getDbName(), userCCID, fullName);
                }
                //If the egroup exists or it was successfully created
                if (eGroupExists || eGroupCreated) {
                    //Clone the instance and override eGroup
                    DODInstance clone = instance.clone();
                    clone.setEGroup(((Textbox) getFellow("eGroupEdit")).getValue());
                    if (instanceDAO.update(instance, clone) > 0) {
                        instance = clone;
                        if (instance.getEGroup() != null && !instance.getEGroup().isEmpty())
                            ((Label) getFellow("eGroup")).setValue(instance.getEGroup());
                        else
                            ((Label) getFellow("eGroup")).setValue("-");
                        ((Hbox) getFellow("eGroupEditBox")).setVisible(false);
                        ((Hbox) getFellow("eGroupBox")).setVisible(true);
                    }
                    else {
                        showError(null, DODConstants.ERROR_UPDATING_INSTANCE);
                    }
                }
                else {
                    ((Textbox) getFellow("eGroupEdit")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_CREATION));
                }
            }
            catch (ServiceException ex) {
                ((Textbox) getFellow("eGroupEdit")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_CREATION));
                Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR CREATING EGROUP " + ((Textbox) getFellow("eGroupEdit")).getValue(), ex);
            }
            catch (RemoteException ex) {
                ((Textbox) getFellow("eGroupEdit")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_CREATION));
                Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR CREATING EGROUP " + ((Textbox) getFellow("eGroupEdit")).getValue(), ex);
            }
        }
    }

    /**
     * Edits the project of this instance.
     */
    public void editProject() {
        if (isProjectValid()) {
            //Clone the instance and override project
            DODInstance clone = instance.clone();
            clone.setProject(((Textbox) getFellow("projectEdit")).getValue());
            if (instanceDAO.update(instance, clone) > 0) {
                instance = clone;
                if (instance.getProject() != null && !instance.getProject().isEmpty())
                    ((Label) getFellow("project")).setValue(instance.getProject());
                else
                    ((Label) getFellow("project")).setValue("-");
                ((Hbox) getFellow("projectEditBox")).setVisible(false);
                ((Hbox) getFellow("projectBox")).setVisible(true);
            }
            else {
                showError(null, DODConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }

    /**
     * Edits the expiry date of this instance.
     */
    public void editExpiryDate() {
        if (isExpiryDateValid()) {
            //Clone the instance and override expiry date
            DODInstance clone = instance.clone();
            clone.setExpiryDate(((Datebox) getFellow("expiryDateEdit")).getValue());
            if (instanceDAO.update(instance, clone) > 0) {
                instance = clone;
                if (instance.getExpiryDate() != null)
                    ((Label) getFellow("expiryDate")).setValue(dateFormatter.format(instance.getExpiryDate()));
                else
                    ((Label) getFellow("expiryDate")).setValue("-");
                ((Hbox) getFellow("expiryDateEditBox")).setVisible(false);
                ((Hbox) getFellow("expiryDateBox")).setVisible(true);
            }
            else {
                showError(null, DODConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }

    /**
     * Edits the description of this instance.
     */
    public void editDescription() {
        if (isDescriptionValid()) {
            //Clone the instance and override description
            DODInstance clone = instance.clone();
            clone.setDescription(((Textbox) getFellow("descriptionEdit")).getValue());
            if (instanceDAO.update(instance, clone) > 0) {
                instance = clone;
                if (instance.getDescription() != null && !instance.getDescription().isEmpty())
                    ((Label) getFellow("description")).setValue(instance.getDescription());
                else
                    ((Label) getFellow("description")).setValue("-");
                ((Hbox) getFellow("descriptionEditBox")).setVisible(false);
                ((Hbox) getFellow("descriptionBox")).setVisible(true);
            }
            else {
                showError(null, DODConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Edits the dbSize of this instance.
     */
    public void editDbSize() {
        if (isDbSizeValid()) {
            //Clone the instance and override dbSize
            DODInstance clone = instance.clone();
            clone.setDbSize(Integer.valueOf(((Textbox) getFellow("dbSizeEdit")).getValue()));
            if (instanceDAO.update(instance, clone) > 0) {
                instance = clone;
                ((Label) getFellow("dbSize")).setValue(String.valueOf(instance.getDbSize()) + " GB");
                ((Hbox) getFellow("dbSizeEditBox")).setVisible(false);
                ((Hbox) getFellow("dbSizeBox")).setVisible(true);
            }
            else {
                showError(null, DODConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Edits the noConnections of this instance.
     */
    public void editNoConnections() {
        if (isNoConnectionsValid()) {
            //Clone the instance and override noConnections
            DODInstance clone = instance.clone();
            if (!((Textbox) getFellow("noConnectionsEdit")).getValue().isEmpty())
                clone.setNoConnections(Integer.valueOf(((Textbox) getFellow("noConnectionsEdit")).getValue()));
            else
                clone.setNoConnections(0);
            if (instanceDAO.update(instance, clone) > 0) {
                instance = clone;
                if (instance.getNoConnections() > 0)
                    ((Label) getFellow("noConnections")).setValue(String.valueOf(instance.getNoConnections()));
                else
                    ((Label) getFellow("noConnections")).setValue("-");
                ((Hbox) getFellow("noConnectionsEditBox")).setVisible(false);
                ((Hbox) getFellow("noConnectionsBox")).setVisible(true);
            }
            else {
                showError(null, DODConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Edits the category of this instance.
     */
    public void editCategory() {
        if (isCategoryValid()) {
            //Clone the instance and override dbSize
            DODInstance clone = instance.clone();
            clone.setCategory((String)((Combobox) getFellow("categoryEdit")).getSelectedItem().getValue());
            if (instanceDAO.update(instance, clone) > 0) {
                instance = clone;
                ((Label) getFellow("category")).setValue(Labels.getLabel(DODConstants.LABEL_CATEGORY + instance.getCategory()));
                ((Hbox) getFellow("categoryEditBox")).setVisible(false);
                ((Hbox) getFellow("categoryBox")).setVisible(true);
            }
            else {
                showError(null, DODConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }

    /**
     * Validates e-Group name
     * @return true if e-Group name is valid, false otherwise
     */
    private boolean isEGroupValid() {
        Textbox eGroup = (Textbox) getFellow("eGroupEdit");
        //If there are no previous errors
        if (eGroup.getErrorMessage() == null || eGroup.getErrorMessage().isEmpty()) {
            if (eGroup.getText().length() > 0) {
                //Trim and lowercase
                eGroup.setValue(eGroup.getValue().trim().toLowerCase());
                //Check eGroup length
                if (eGroup.getText().length() > DODConstants.MAX_E_GROUP_LENGTH) {
                    eGroup.setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_LENGTH));
                    return false;
                }
                //Check if egroup name contains a dash
                if (!eGroup.getValue().contains("-")) {
                    eGroup.setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_DASH));
                    return false;
                }
                //Only upppercase and lowercase ASCII letters, numbers, dashes, dots and underscores are allowed
                if (!Pattern.matches("[\\da-zA-Z\\.\\-_]*", eGroup.getValue())) {
                    eGroup.setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_CHARS));
                    return false;
                }
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates expiry date
     * @return true if expiry date is valid, false otherwise
     */
    private boolean isExpiryDateValid() {
        Datebox expiryDate = (Datebox) getFellow("expiryDateEdit");
        //If there are no previous errors
        if (expiryDate.getErrorMessage() == null || expiryDate.getErrorMessage().isEmpty()) {
            //If the user has entered a value
            if (!expiryDate.getText().isEmpty()) {
                //Check valid date
                if (expiryDate.getValue() == null) {
                    expiryDate.setErrorMessage(Labels.getLabel(DODConstants.ERROR_EXPIRY_DATE_FORMAT));
                    return false;
                }
                //Check if it is a future date
                Date now = new Date();
                if (expiryDate.getValue().compareTo(now) <= 0) {
                    expiryDate.setErrorMessage(Labels.getLabel(DODConstants.ERROR_EXPIRY_DATE_FUTURE));
                    return false;
                }
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates project
     * @return true if project is valid, false otherwise
     */
    private boolean isProjectValid() {
        Textbox project = (Textbox) getFellow("projectEdit");
        //If there are no previous errors
        if (project.getErrorMessage() == null || project.getErrorMessage().isEmpty()) {
            //Trim
            project.setValue(project.getValue().trim());
            //Check description length
            if (project.getValue().length() > DODConstants.MAX_PROJECT_LENGTH) {
                project.setErrorMessage(Labels.getLabel(DODConstants.ERROR_PROJECT_LENGTH));
                return false;
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates description
     * @return true if description is valid, false otherwise
     */
    private boolean isDescriptionValid() {
        Textbox description = (Textbox) getFellow("descriptionEdit");
        //If there are no previous errors
        if (description.getErrorMessage() == null || description.getErrorMessage().isEmpty()) {
            //Trim
            description.setValue(description.getValue().trim());
            //Check description length
            if (description.getValue().length() > DODConstants.MAX_DESCRIPTION_LENGTH) {
                description.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DESCRIPTION_LENGTH));
                return false;
            }
        }
        else
            return false;
        return true;
    }
    
    /**
     * Validates category
     * @return true if category is valid, false otherwise
     */
    private boolean isCategoryValid() {
        Combobox category = (Combobox) getFellow("categoryEdit");
        //If there are no previous errors
        if (category.getErrorMessage() == null || category.getErrorMessage().isEmpty()) {
            //Check if user has selected a value
            if (category.getSelectedItem() == null) {
                category.setErrorMessage(Labels.getLabel(DODConstants.ERROR_CATEGORY_EMPTY));
                return false;
            }
            //Check dbtype Oracle or MySQL
            if (!category.getSelectedItem().getValue().equals(DODConstants.CATEGORY_OFFICIAL)
                    && !category.getSelectedItem().getValue().equals(DODConstants.CATEGORY_PERSONAL)
                    && !category.getSelectedItem().getValue().equals(DODConstants.CATEGORY_TEST)) {
                category.setErrorMessage(Labels.getLabel(DODConstants.ERROR_CATEGORY_LIST));
                return false;
            }
        }
        else
            return false;
        return true;
    }
    
    /**
     * Validates DB size
     * @return true if DB size is valid, false otherwise
     */
    private boolean isDbSizeValid() {
        Textbox dbSize = (Textbox) getFellow("dbSizeEdit");
        //If there are no previous errors
        if (dbSize.getErrorMessage() == null || dbSize.getErrorMessage().isEmpty()) {
            //Trim
            dbSize.setValue(dbSize.getValue().trim());
            //Check if user has entered a value
            if (dbSize.getValue().isEmpty()) {
                dbSize.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_SIZE_EMPTY));
                return false;
            }
            try {
                Integer size = Integer.valueOf(dbSize.getText());
                //Check dbName length
                if (size <= 0 || size > DODConstants.MAX_DB_SIZE) {
                    dbSize.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_SIZE_RANGE));
                    return false;
                }
            } catch (NumberFormatException ex) {
                dbSize.setErrorMessage(Labels.getLabel(DODConstants.ERROR_INTEGER_FORMAT));
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates NO Connections
     * @return true if NO Connections is valid, false otherwise
     */
    private boolean isNoConnectionsValid() {
        Textbox noConnections = (Textbox) getFellow("noConnectionsEdit");
        //If there are no previous errors
        if (noConnections.getErrorMessage() == null || noConnections.getErrorMessage().isEmpty()) {
            //Trim
            noConnections.setValue(noConnections.getValue().trim());
            //Check only if user has entered a value
            if (!noConnections.getValue().isEmpty()) {
                try {
                    int noConn = Integer.valueOf(noConnections.getText()).intValue();
                    //Check dbName length
                    if (noConn <= 0 || noConn > DODConstants.MAX_NO_CONNECTIONS) {
                        noConnections.setErrorMessage(Labels.getLabel(DODConstants.ERROR_NO_CONNECTIONS_RANGE));
                        return false;
                    }
                } catch (NumberFormatException ex) {
                    noConnections.setErrorMessage(Labels.getLabel(DODConstants.ERROR_INTEGER_FORMAT));
                }
            }
        }
        else
            return false;
        return true;
    }


    /**
     * Displays an error window for the error code provided.
     * @param exception exception to be logged (can be null).
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(Exception exception, String errorCode) {
        if (exception != null) {
            Logger.getLogger(InstanceController.class.getName()).log(Level.SEVERE, "ERROR DISPATCHING JOB", exception);
        }
        Window errorWindow = (Window) getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(InstanceController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(InstanceController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
