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
import ch.cern.dbod.db.dao.InstanceDAO;
import ch.cern.dbod.db.dao.JobDAO;
import ch.cern.dbod.db.dao.UpgradeDAO;
import ch.cern.dbod.db.entity.*;
import ch.cern.dbod.ui.renderer.InstanceChangesRenderer;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.EGroupHelper;
import ch.cern.dbod.util.FormValidations;
import ch.cern.dbod.util.JobHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.apache.commons.codec.digest.DigestUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.*;

/**
 * Controller for the instance page.
 * @author Daniel Gomez Blanco
 * @author Jose Andres Cordero Benitez
 */
public class InstanceController extends Vbox implements AfterCompose, BeforeCompose {
    /**
     * Upgrade DAO
     */
    private UpgradeDAO upgradeDAO;
    /**
     * Instance DAO
     */
    private InstanceDAO instanceDAO;
    /**
     * DAO to load jobs
     */
    JobDAO jobDAO;
    /**
     * Username for the authenticated user.
     */
    String username;
    /**
     * List of upgrades.
     */
    private List<Upgrade> upgrades;
    /**
     * Instance being managed at the moment.
     */
    Instance instance;
    /**
     * Master (in case this instance is a slave).
     */
    Instance master;
    /**
     * Slave (in case this instance is a master).
     */
    Instance slave;
    /**
     * List of jobs performed on this instance.
     */
    List<Job> jobs;
    /**
     * List of changes.
     */
    List<InstanceChange> changes;
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
    @Override
    public void beforeCompose() {
        //Get instance
        String dbName = (String) Executions.getCurrent().getParameter(CommonConstants.INSTANCE);
        if (dbName != null && !dbName.isEmpty()) {
            upgradeDAO = new UpgradeDAO();
            upgrades = upgradeDAO.selectAll();
            instanceDAO = new InstanceDAO();
            instance = instanceDAO.selectByDbName(dbName, upgrades);
            if (instance != null) {
                //Get username and adminMode from headers
                Execution execution = Executions.getCurrent();
                username = execution.getHeader(CommonConstants.ADFS_LOGIN);
                String eGroups = execution.getHeader(CommonConstants.ADFS_GROUP);
                Boolean adminMode = (Boolean) EGroupHelper.groupInList(CommonConstants.ADMIN_E_GROUP, eGroups);
                admin = adminMode.booleanValue();
                
                //Get user and password for the web services account
                String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(CommonConstants.WS_USER);
                String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(CommonConstants.WS_PSWD);
                String userCCIDText = execution.getHeader(CommonConstants.ADFS_CCID);
                if (userCCIDText != null && !userCCIDText.isEmpty())
                    userCCID = Long.parseLong(execution.getHeader(CommonConstants.ADFS_CCID));
                else
                    userCCID = new Long(0);

                //Get instance and amdmin mode from session attributes
                jobHelper = new JobHelper(admin);
                dateFormatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
                dateTimeFormatter = new SimpleDateFormat(CommonConstants.DATE_TIME_FORMAT);     
                jobDAO = new JobDAO();
                eGroupHelper = new EGroupHelper(wsUser, wsPswd);

                //Load instance
                getInstanceInfo();
            }
        }
    }
    
    /**
     * Gets the information of the current instance from the DB.
     */
    private void getInstanceInfo () {
        //Select upgrades
        upgrades = upgradeDAO.selectAll();
        //Query the database for the most recent version of this instance
        instance = instanceDAO.selectByDbName(instance.getDbName(), upgrades);
        
        //Load master and slave
        if (instance != null) {
            if (instance.getMaster() != null && !instance.getMaster().isEmpty())
                master = instanceDAO.selectByDbName(instance.getMaster(), upgrades);
            else
                master = null;
            if (instance.getSlave() != null && !instance.getSlave().isEmpty())
                slave = instanceDAO.selectByDbName(instance.getSlave(), upgrades);
            else
                slave = null;
        }
        else {
            master = null;
            slave = null;
        }
    }

    /**
     * Method executed after composing the page. It loads instance info, buttons and jobs.
     */
    @Override
    public void afterCompose() {
        //Maximum date to set the expiry date for the instance (6 months)
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 6);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String errorMsg = Labels.getLabel(CommonConstants.ERROR_EXPIRY_DATE_FUTURE);
        
        //Configure input fields
        ((Textbox) getFellow("eGroupEdit")).setMaxlength(CommonConstants.MAX_E_GROUP_LENGTH);
        ((Datebox) getFellow("expiryDateEdit")).setFormat(CommonConstants.DATE_FORMAT);
        ((Datebox) getFellow("expiryDateEdit")).setTimeZonesReadonly(true);
        ((Datebox) getFellow("expiryDateEdit")).setConstraint("no past, before " + dateFormat.format(cal.getTime()) + ": " + errorMsg);
        ((Textbox) getFellow("projectEdit")).setMaxlength(CommonConstants.MAX_PROJECT_LENGTH);
        ((Textbox) getFellow("descriptionEdit")).setMaxlength(CommonConstants.MAX_DESCRIPTION_LENGTH);
        if (admin) {
            ((Textbox) getFellow("noConnectionsEdit")).setMaxlength(String.valueOf(CommonConstants.MAX_NO_CONNECTIONS).length());
            ((Textbox) getFellow("dbSizeEdit")).setMaxlength(String.valueOf(CommonConstants.MAX_DB_SIZE).length());
            ((Textbox) getFellow("versionEdit")).setMaxlength(CommonConstants.MAX_VERSION_LENGTH);
            ((Textbox) getFellow("hostEdit")).setMaxlength(CommonConstants.MAX_HOST_LENGTH);
        }
        
        //Load instance info if necessary
        if (instance != null) {
            //Display or hide areas
            displayOrHideAreas();
            //Load information for this instance
            loadInstanceInfo();
            //Load buttons
            loadButtons();
            //Load jobs
            loadJobs();
            //Load changes
            loadChanges();
        }
    }
    
    /**
     * Displays or hides areas in the page depending on the instance information.
     */
    private void displayOrHideAreas () {
        //Master area
        if (master != null)
            ((Hbox) getFellow("masterArea")).setVisible(true);
        else
            ((Hbox) getFellow("masterArea")).setVisible(false);
        //Slave area
        if (slave != null)
            ((Hbox) getFellow("slaveArea")).setVisible(true);
        else
            ((Hbox) getFellow("slaveArea")).setVisible(false);
        //Empty area
        if (master == null && slave == null)
            ((Hbox) getFellow("emptyArea")).setVisible(true);
        else
            ((Hbox) getFellow("emptyArea")).setVisible(false);
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
        //Refresh instance
        getInstanceInfo();
        
        //Refresh information
        if (instance != null) {
            //Display or hide areas
            displayOrHideAreas();
            //Load information for this instance
            loadInstanceInfo();
            //Load buttons
            loadButtons();
            //Load jobs
            loadJobs();
            //Load changes
            loadChanges();
        }
        //Instance has been marked for deletion
        else {
            Executions.sendRedirect(CommonConstants.PAGE_INSTANCE_NOT_FOUND);
        }
    }

    /**
     * Loads information related to the current instance.
     */
    private void loadInstanceInfo() {
        //Title
        ((Label) getFellow("instanceTitle")).setValue(Labels.getLabel(CommonConstants.LABEL_INSTANCE_TITLE) + " " + instance.getDbName());
        ((Label) getFellow("username")).setValue(instance.getUsername());

        //e-group (if any)
        if (instance.getEGroup() != null && !instance.getEGroup().isEmpty()) {
            ((Label) getFellow("eGroup")).setValue(instance.getEGroup());
            ((Textbox) getFellow("eGroupEdit")).setValue(instance.getEGroup());
        } else {
            ((Label) getFellow("eGroup")).setValue("-");
        }

        //category
        ((Label) getFellow("category")).setValue(Labels.getLabel(CommonConstants.LABEL_CATEGORY + instance.getCategory()));

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
            try {
                ((Datebox) getFellow("expiryDateEdit")).setValue(instance.getExpiryDate());
            }
            catch (WrongValueException ex) {
                ((Label) getFellow("expiryDate")).setValue("Incorrect date");
            }
        } else {
            ((Label) getFellow("expiryDate")).setValue("-");
        }

        //DB type and size
        ((Label) getFellow("dbType")).setValue(Labels.getLabel(CommonConstants.LABEL_DB_TYPE + instance.getDbType()));
        ((Label) getFellow("dbSize")).setValue(instance.getDbSize() + " GB");
            
        //DB connections (if any)
        if (instance.getNoConnections() > 0) {
            ((Label) getFellow("noConnections")).setValue(String.valueOf(instance.getNoConnections()));
        } else {
            ((Label) getFellow("noConnections")).setValue("-");
        }
        
        //Version (if any)
        if (instance.getVersion() != null && !instance.getVersion().isEmpty()) {
            ((Label) getFellow("version")).setValue(instance.getVersion());
        } else {
            ((Label) getFellow("version")).setValue("-");
        }
        
        //Master (if any)
        if (master != null) {
            ((Label) getFellow("master")).setValue(master.getDbName());
        }
        //Slave (if any)
        if (slave != null) {
            ((Label) getFellow("slave")).setValue(slave.getDbName());
        }
        
        //Host
        ((Label) getFellow("host")).setValue(instance.getHost());
            
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
        stateLabel.setValue(Labels.getLabel(CommonConstants.LABEL_STATE + instance.getState()));
        stateImage.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_STATE + instance.getState()));
        switch (instance.getState()) {
            case CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL:
                stateImage.setSrc(CommonConstants.IMG_AWAITING_APPROVAL);
                break;
            case CommonConstants.INSTANCE_STATE_JOB_PENDING:
                stateImage.setSrc(CommonConstants.IMG_PENDING);
                break;
            case CommonConstants.INSTANCE_STATE_RUNNING:
                stateImage.setSrc(CommonConstants.IMG_RUNNING);
                break;
            case CommonConstants.INSTANCE_STATE_STOPPED:
                stateImage.setSrc(CommonConstants.IMG_STOPPED);
                break;
            case CommonConstants.INSTANCE_STATE_MAINTENANCE:
                stateImage.setSrc(CommonConstants.IMG_MAINTENANCE);
                break;
            case CommonConstants.INSTANCE_STATE_BUSY:
                stateImage.setSrc(CommonConstants.IMG_BUSY);
                break;
            case CommonConstants.INSTANCE_STATE_UNKNOWN:
                stateImage.setSrc(CommonConstants.IMG_UNKNOWN);
                break;
        }
        
        //User information extracted from FIM
        User user = instance.getUser();
        if (user != null)
        {
            ((Label) getFellow("email")).setValue(user.getEmail());
            ((Label) getFellow("fullname")).setValue(user.getFirstName() + " " + user.getLastName());
            String telephone = "";
            String phoneLabel = "";
            if (user.getPhone1() != null)
            {
                phoneLabel = Labels.getLabel(CommonConstants.LABEL_PHONE);
                telephone += user.getPhone1();
                if (user.getPhone2() != null)
                {
                    phoneLabel += " / " + Labels.getLabel(CommonConstants.LABEL_PHONE) + " 2";
                    telephone += " / " + user.getPhone2();
                }
                if (user.getPortable() != null)
                {
                    phoneLabel += " (" + Labels.getLabel(CommonConstants.LABEL_PORTABLE) + ")";
                    telephone += " (" + user.getPortable() + ")";
                }
            }
            else if (user.getPortable() != null)
            {
                phoneLabel = Labels.getLabel(CommonConstants.LABEL_PORTABLE);
                telephone += user.getPortable();
            }
            if (!phoneLabel.isEmpty())
            {
                ((Label) getFellow("phoneLabel")).setValue(phoneLabel + ":");
                ((Label) getFellow("telephone")).setValue(telephone);
            }
            String orgunit = "-";
            if (user.getDepartment() != null)
            {
                orgunit = user.getDepartment();
                if (user.getGroup() != null)
                {
                    orgunit += "-" + user.getGroup();
                    if (user.getSection() != null)
                        orgunit += "-" + user.getSection();
                }
            }
            ((Label) getFellow("orgunit")).setValue(orgunit);
        }
        
        //If the user is an admin
        if (admin) {
            //Maintenance button
            if (instance != null && instance.getState().equals(CommonConstants.INSTANCE_STATE_MAINTENANCE)){
                ((Toolbarbutton) getFellow("setMaintenanceBtn")).setStyle("display:none");
                ((Toolbarbutton) getFellow("unsetMaintenanceBtn")).setStyle("display:block");
            }
            else{
                ((Toolbarbutton) getFellow("setMaintenanceBtn")).setStyle("display:block");
                ((Toolbarbutton) getFellow("unsetMaintenanceBtn")).setStyle("display:none");
            }
            //Values for edit boxes
            ((Combobox) getFellow("categoryEdit")).getItemAtIndex(0).setValue(CommonConstants.CATEGORY_OFFICIAL);
            ((Combobox) getFellow("categoryEdit")).getItemAtIndex(1).setValue(CommonConstants.CATEGORY_REFERENCE);
            ((Combobox) getFellow("categoryEdit")).getItemAtIndex(2).setValue(CommonConstants.CATEGORY_TEST);
            switch (instance.getCategory()) {
                case CommonConstants.CATEGORY_OFFICIAL:
                    ((Combobox) getFellow("categoryEdit")).setSelectedIndex(0);
                    break;
                case CommonConstants.CATEGORY_REFERENCE:
                    ((Combobox) getFellow("categoryEdit")).setSelectedIndex(1);
                    break;
                case CommonConstants.CATEGORY_TEST:
                    ((Combobox) getFellow("categoryEdit")).setSelectedIndex(2);
                    break;
            }
            ((Textbox) getFellow("dbSizeEdit")).setValue(String.valueOf(instance.getDbSize()));
            if (instance.getNoConnections() > 0)
                ((Textbox) getFellow("noConnectionsEdit")).setValue(String.valueOf(instance.getNoConnections()));
            if (instance.getVersion() != null && !instance.getVersion().isEmpty())
                ((Textbox) getFellow("versionEdit")).setValue(String.valueOf(instance.getVersion()));
            ((Textbox) getFellow("hostEdit")).setValue(String.valueOf(instance.getHost()));
            
            //If the user couldn't be found, the instance is not in FIM
            //if (user == null)
            //{
            //    ((Caption) getFellow("instanceCaption")).setImage("/img/warning-small.png");
            //    ((Label) getFellow("instanceTitle")).setTooltiptext(Labels.getLabel(CommonConstants.ERROR_NO_INSTANCE_ON_FIM));
            //    ((Label) getFellow("instanceTitle")).setStyle("text-decoration-line:underline; text-decoration-style:dashed; text-decoration-color:red;");
            //}
            //If the username saved in the instance doesn't match the user saved in FIM
            //else if (!instance.getUsername().equalsIgnoreCase(user.getLogin()))
            //{
            //    ((Label) getFellow("username")).setValue(instance.getUsername() + " (" + user.getLogin() + ")");
            //    ((Label) getFellow("username")).setStyle("color:red !important;text-decoration-line:underline;text-decoration-style:dashed;text-decoration-color:red;");
            //    ((Label) getFellow("username")).setTooltiptext(Labels.getLabel(CommonConstants.ERROR_NO_USER_ON_FIM));
            //}
        }
    }

    /**
     * Loads the buttons (enabled or disabled depending on the instance state).
     */
    private void loadButtons() {
        //Startup button
        Toolbarbutton startupBtn = (Toolbarbutton) getFellow("startup");
        //Only enable button if the instance is stopped
        if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)) {
            startupBtn.setDisabled(true);
            startupBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            startupBtn.setDisabled(false);
            startupBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON);
        }

        //Shutdown button
        final Toolbarbutton shutdownBtn = (Toolbarbutton) getFellow("shutdown");
        //Only enable button if the instance is running
        if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
            shutdownBtn.setDisabled(true);
            shutdownBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            shutdownBtn.setDisabled(false);
            shutdownBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON);
        }

        //config files button
        final Toolbarbutton configBtn = (Toolbarbutton) getFellow("config");
        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
            configBtn.setDisabled(true);
            configBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            configBtn.setDisabled(false);
            configBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON);
        }

        //Dispatch a backup button
        final Toolbarbutton backupBtn = (Toolbarbutton) getFellow("backup");
        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
            backupBtn.setDisabled(true);
            backupBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            backupBtn.setDisabled(false);
            backupBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON);
        }

        //Dispatch a restore button
        final Toolbarbutton restoreBtn = (Toolbarbutton) getFellow("restore");
        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
            restoreBtn.setDisabled(true);
            restoreBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            restoreBtn.setDisabled(false);
            restoreBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON);
        }
        
        //Upgrade a database button
        final Toolbarbutton upgradeBtn = (Toolbarbutton) getFellow("upgrade");
        //Only enable button if the instance is stopped or running (and there is an upgrade available)
        if ((!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN))
                || instance.getUpgradeTo() == null || instance.getUpgradeTo().isEmpty()) {
            upgradeBtn.setDisabled(true);
            upgradeBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON_DISABLED);
            upgradeBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB + CommonConstants.JOB_UPGRADE));
        } else {
            upgradeBtn.setDisabled(false);
            upgradeBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON);
            upgradeBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB + CommonConstants.JOB_UPGRADE));
        }

        //Access monitoring button
        final Toolbarbutton monitorBtn = (Toolbarbutton) getFellow("monitor");
        //Only enable button if the instance is not awaiting approval
        if (instance.getState().equals(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
            monitorBtn.setDisabled(true);
            monitorBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            monitorBtn.setDisabled(false);
            monitorBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON);
        }
        //If it is an oracle instance, send to OEM
        if (instance.getDbType().equals(CommonConstants.DB_TYPE_ORA)) {
            monitorBtn.setTarget("_blank");
            monitorBtn.setHref(CommonConstants.OEM_URL + instance.getHost().toUpperCase() + ".cern.ch_" + instance.getDbName().toString().toUpperCase());
        }
        else if (instance.getDbType().equals(CommonConstants.DB_TYPE_ORACLE)) {
            monitorBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                        @Override
                        public void onEvent(Event event) {
                            doMonitor();
                        }
                    });
        }
        else if (instance.getDbType().equals(CommonConstants.DB_TYPE_INFLUX)) {
            monitorBtn.setDisabled(true);
        }
        else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            String date = dateFormat.format(new Date());
            String sec_token = DigestUtils.sha256Hex(ConfigLoader.getProxyPassword() + ":" + instance.getDbName() + ":" + date);
            String appdynURL; 
            if (instance.getDbType().equals(CommonConstants.DB_TYPE_PG))
                appdynURL = ConfigLoader.getDBTuna4PgPath() + instance.getDbName() + "&sec_token=" + sec_token;
            else
                appdynURL = ConfigLoader.getDBTunaPath() + instance.getDbName() + "&sec_token=" + sec_token;
  
            monitorBtn.setTarget("_blank");
            monitorBtn.setHref(appdynURL);
        }
        
        //Host monitoring button
        final Toolbarbutton hostMonitorBtn = (Toolbarbutton) getFellow("hostmonitor");
        //Only enable button if the instance is not awaiting approval
        if (instance.getState().equals(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
            hostMonitorBtn.setDisabled(true);
            hostMonitorBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON_DISABLED);
        } else {
            hostMonitorBtn.setDisabled(false);
            hostMonitorBtn.setZclass(CommonConstants.STYLE_BIG_BUTTON);
        }

        String kibanaURL = ConfigLoader.getKibanaDashboard() + instance.getHost();

        hostMonitorBtn.setTarget("_blank");
        hostMonitorBtn.setHref(kibanaURL);
    }

     /**
      * Load the jobs carried out (or pending) for the current instance.
      */
    private void loadJobs() {
        //Get jobs from database
        jobs = jobDAO.selectByInstance(instance);

        //Get selected job (if any)
        Listbox jobSelector = (Listbox) getFellow("jobGridSelector");
        Job selected = null;
        if (jobSelector.getSelectedItem() != null) {
            selected = (Job) jobSelector.getSelectedItem().getValue();
        }

        //Remove previous jobs
        jobSelector.getItems().removeAll(jobSelector.getItems());

        //If there are jobs
        if (jobs != null && jobs.size() > 0) {
            //Insert items in listbox
            for (int i = 0; i < jobs.size(); i++) {
                Listitem item = new Listitem();
                Listcell statecell = new Listcell();
                Listcell requestercell = new Listcell();
                Listcell commandcell = new Listcell();
                Listcell datecell = new Listcell();
                Listcell completioncell = new Listcell();
                
                Job job = jobs.get(i);
                item.setValue(job);

                statecell.setSclass(CommonConstants.STYLE_JOB_STATE);
                requestercell.setLabel(job.getRequester());
                commandcell.setLabel(Labels.getLabel(CommonConstants.LABEL_JOB + job.getCommandName()));
                datecell.setLabel(dateTimeFormatter.format(job.getCreationDate()));
                if (job.getCompletionDate() != null)
                    completioncell.setLabel(dateTimeFormatter.format(job.getCompletionDate()));
                else
                    completioncell.setLabel("-");
                
                switch (job.getState()) {
                    case CommonConstants.JOB_STATE_FINISHED_OK:
                        statecell.setImage(CommonConstants.IMG_RUNNING);
                        break;
                    case CommonConstants.JOB_STATE_RUNNING:
                        statecell.setImage(CommonConstants.IMG_PENDING);
                        break;
                    case CommonConstants.JOB_STATE_FINISHED_FAIL:
                        statecell.setImage(CommonConstants.IMG_STOPPED);
                        break;
                    case CommonConstants.JOB_STATE_FINISHED_WARNING: 
                        statecell.setImage(CommonConstants.IMG_BUSY);
                        break;
                    case CommonConstants.JOB_STATE_PENDING:
                        statecell.setImage(CommonConstants.IMG_AWAITING_APPROVAL);
                        break;
                }
                
                item.appendChild(statecell);
                item.appendChild(commandcell);
                item.appendChild(requestercell);
                item.appendChild(datecell);
                item.appendChild(completioncell);
                jobSelector.appendChild(item);
                
                //If it was the selected one, select it again
                if (selected != null && job.getUsername().equals(selected.getUsername()) && job.getDbName().equals(selected.getDbName())
                        && job.getCommandName().equals(selected.getCommandName()) && job.getType().equals(selected.getType())
                        && job.getCreationDate().getTime() == selected.getCreationDate().getTime()) {
                    jobSelector.setSelectedItem(item);
                }
            }
        }

        //Load information for the selected item
        loadJob();
    }
    
    /**
     * Loads the information for the selected job
     */
    private void loadJob() {
        //Get job
        Listbox jobSelector = (Listbox) getFellow("jobGridSelector");
        if (jobSelector == null || jobSelector.getSelectedItem() == null)
            return;
        
        Job job = (Job) jobSelector.getSelectedItem().getValue();

        //If a job is selected
        if (job != null) {
            Window window = ((Window) getFellow("jobInfoWindow"));
            
            //Load job general info
            ((Label) window.getFellow("jobRequester")).setValue(job.getRequester());
            Image stateImage = (Image) window.getFellow("jobStateImage");
            Label stateLabel = (Label) window.getFellow("jobStateLabel");
            stateImage.setWidth("20px");
            stateImage.setHeight("20px");
            stateLabel.setValue(Labels.getLabel(CommonConstants.LABEL_JOB_STATE + job.getState()));
            stateImage.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB_STATE + job.getState()));
            switch (job.getState()) {
                case CommonConstants.JOB_STATE_PENDING:
                    stateImage.setSrc(CommonConstants.IMG_AWAITING_APPROVAL);
                    break;
                case CommonConstants.JOB_STATE_RUNNING:
                    stateImage.setSrc(CommonConstants.IMG_PENDING);
                    break;
                case CommonConstants.JOB_STATE_FINISHED_OK:
                    stateImage.setSrc(CommonConstants.IMG_RUNNING);
                    break;
                case CommonConstants.JOB_STATE_FINISHED_FAIL:
                    stateImage.setSrc(CommonConstants.IMG_STOPPED);
                    break;
                case CommonConstants.JOB_STATE_FINISHED_WARNING:
                    stateImage.setSrc(CommonConstants.IMG_BUSY);
                    break;
            }
            ((Label) window.getFellow("jobCreationDate")).setValue(dateTimeFormatter.format(job.getCreationDate()));
            if (job.getCompletionDate() != null) {
                ((Label) window.getFellow("jobCompletionDate")).setValue(dateTimeFormatter.format(job.getCompletionDate()));
            } else {
                ((Label) window.getFellow("jobCompletionDate")).setValue("-");
            }

            //Load log
            ((Textbox) window.getFellow("log")).setRawValue(jobDAO.selectLogByJob(job));

            //Show the popup with the information
            window.setVisible(true);
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
            showError(null, CommonConstants.ERROR_DISPATCHING_JOB);
        }
    }

    /**
     * Creates job to shutdown the instance.
     */
    public void doShutdown() {
        try {
            ShutdownController shutdownController = new ShutdownController(instance, username, jobHelper);
            //Only show window if it is not already being diplayed
            if (this.getRoot().getFellowIfAny(shutdownController.getId()) == null) {
                shutdownController.setParent(this.getRoot());
                shutdownController.doModal();
            }
        } catch (InterruptedException ex) {
            showError(ex, CommonConstants.ERROR_DISPATCHING_JOB);
        }
    }

    /**
     * Opens the files window.
     */
    public void doFiles() {
        try {
            FileController fileController = new FileController(instance, username, jobHelper);
            //Only show window if it is not already being diplayed
            if (this.getRoot().getFellowIfAny(fileController.getId()) == null) {
                fileController.setParent(this.getRoot());
                fileController.doModal();
            }
        } catch (InterruptedException ex) {
            showError(ex, CommonConstants.ERROR_DISPATCHING_JOB);
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
            showError(ex, CommonConstants.ERROR_DISPATCHING_JOB);
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
            showError(ex, CommonConstants.ERROR_DISPATCHING_JOB);
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
            showError(ex, CommonConstants.ERROR_DISPATCHING_JOB);
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
            showError(ex, CommonConstants.ERROR_DISPATCHING_JOB);
        }
    }
    
    /**
     * Sets the state of the machine to under maintenance.
     */
    public void setMaintenance(boolean maintenance) {
        //Clone the instance and override project
        Instance clone = instance.clone();
        if (maintenance) {
            clone.setState(CommonConstants.INSTANCE_STATE_MAINTENANCE);
        }
        else {
            clone.setState(CommonConstants.INSTANCE_STATE_RUNNING);
        }
        if (instanceDAO.update(instance, clone, username) > 0) {
            instance = clone;
            loadInstanceInfo();
            loadButtons();
            loadJobs();
            loadChanges();
        }
        else {
            showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
        }
    } 

    /**
     * Checks if an e-group exists, and if it does continues with the editing.
     * This method was modified to not allow users to create an e-group using
     * this functionality (the e-group must exist). The functionality is still
     * there in case needed in the future.
     */
    public void checkAndEditEGroup() {
        //Check for errors in form
        if (FormValidations.isEGroupValid(((Textbox) getFellow("eGroupEdit")), instance.getDbType(), eGroupHelper)) {
            //If there is an egroup
            if(((Textbox) getFellow("eGroupEdit")).getValue() != null && !((Textbox) getFellow("eGroupEdit")).getValue().isEmpty()) {
                //Check if e-group exists
                boolean eGroupExists = eGroupHelper.eGroupExists(((Textbox) getFellow("eGroupEdit")).getValue());
                //If the egroup does not exist show error window and return (the creation of e-groups is not allowed when editing)
                if (!eGroupExists) {
                    try {
                        ((Window) getFellow("eGroupConfirm")).doModal();
                    } catch (SuspendNotAllowedException ex) {
                        Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR OPENING EGROUP CONFIRM WINDOW", ex);
                    }
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
        if (FormValidations.isEGroupValid(((Textbox) getFellow("eGroupEdit")), instance.getDbType(), eGroupHelper)) {
            boolean eGroupCreated = false;
            //If the egroup does not exist create it
            if (!eGroupExists) {
                //If the egroup was successfully created store the instance in the DB
                eGroupCreated = eGroupHelper.createEGroup(((Textbox) getFellow("eGroupEdit")).getValue(),
                        instance.getDbName(), userCCID, true);
            }
            //If the egroup exists or it was successfully created
            if (eGroupExists || eGroupCreated) {
                //If instance is Oracle 12 change it in OEM
                boolean changedInOEM = true;
                if (CommonConstants.DB_TYPE_ORA.equals(instance.getDbType())) {
                    changedInOEM = eGroupHelper.changeEgroupInOEM(instance.getDbName(),
                                                    instance.getEGroup(),
                                                    ((Textbox) getFellow("eGroupEdit")).getValue());
                }
                
                if (changedInOEM) {
                    //Clone the instance and override eGroup
                    Instance clone = instance.clone();
                    clone.setEGroup(((Textbox) getFellow("eGroupEdit")).getValue());
                    if (instanceDAO.update(instance, clone, username) > 0) {
                        instance = clone;
                        if (instance.getEGroup() != null && !instance.getEGroup().isEmpty())
                            ((Label) getFellow("eGroup")).setValue(instance.getEGroup());
                        else
                            ((Label) getFellow("eGroup")).setValue("-");
                        ((Hbox) getFellow("eGroupEditBox")).setVisible(false);
                        ((Hbox) getFellow("eGroupBox")).setVisible(true);
                        //Reload changes
                        refreshInfo();
                        loadChanges();
                    }
                    else {
                        showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
                    }
                }
                else {
                    ((Textbox) getFellow("eGroupEdit")).setErrorMessage(Labels.getLabel(CommonConstants.ERROR_E_GROUP_EDITING));
                }
            }
            else {
                ((Textbox) getFellow("eGroupEdit")).setErrorMessage(Labels.getLabel(CommonConstants.ERROR_E_GROUP_CREATION));
            }
        }
    }

    /**
     * Edits the project of this instance.
     */
    public void editProject() {
        if (FormValidations.isProjectValid(((Textbox) getFellow("projectEdit")))) {
            //Clone the instance and override project
            Instance clone = instance.clone();
            clone.setProject(((Textbox) getFellow("projectEdit")).getValue());
            if (instanceDAO.update(instance, clone, username) > 0) {
                instance = clone;
                if (instance.getProject() != null && !instance.getProject().isEmpty())
                    ((Label) getFellow("project")).setValue(instance.getProject());
                else
                    ((Label) getFellow("project")).setValue("-");
                ((Hbox) getFellow("projectEditBox")).setVisible(false);
                ((Hbox) getFellow("projectBox")).setVisible(true);
                //Reload changes
                refreshInfo();
                loadChanges();
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }

    /**
     * Edits the expiry date of this instance.
     */
    public void editExpiryDate() {
        if (FormValidations.isExpiryDateValid((Datebox) getFellow("expiryDateEdit"))) {
            //Clone the instance and override expiry date
            Instance clone = instance.clone();
            clone.setExpiryDate(((Datebox) getFellow("expiryDateEdit")).getValue());
            if (instanceDAO.update(instance, clone, username) > 0) {
                instance = clone;
                if (instance.getExpiryDate() != null)
                    ((Label) getFellow("expiryDate")).setValue(dateFormatter.format(instance.getExpiryDate()));
                else
                    ((Label) getFellow("expiryDate")).setValue("-");
                ((Hbox) getFellow("expiryDateEditBox")).setVisible(false);
                ((Hbox) getFellow("expiryDateBox")).setVisible(true);
                //Reload changes
                refreshInfo();
                loadChanges();
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }

    /**
     * Edits the description of this instance.
     */
    public void editDescription() {
        if (FormValidations.isDescriptionValid((Textbox) getFellow("descriptionEdit"))) {
            //Clone the instance and override description
            Instance clone = instance.clone();
            clone.setDescription(((Textbox) getFellow("descriptionEdit")).getValue());
            if (instanceDAO.update(instance, clone, username) > 0) {
                instance = clone;
                if (instance.getDescription() != null && !instance.getDescription().isEmpty())
                    ((Label) getFellow("description")).setValue(instance.getDescription());
                else
                    ((Label) getFellow("description")).setValue("-");
                ((Hbox) getFellow("descriptionEditBox")).setVisible(false);
                ((Hbox) getFellow("descriptionBox")).setVisible(true);
                //Reload changes
                refreshInfo();
                loadChanges();
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Edits the dbSize of this instance.
     */
    public void editDbSize() {
        if (FormValidations.isDbSizeValid((Textbox) getFellow("dbSizeEdit"))) {
            //Clone the instance and override dbSize
            Instance clone = instance.clone();
            clone.setDbSize(Integer.valueOf(((Textbox) getFellow("dbSizeEdit")).getValue()));
            if (instanceDAO.update(instance, clone, username) > 0) {
                instance = clone;
                ((Label) getFellow("dbSize")).setValue(String.valueOf(instance.getDbSize()) + " GB");
                ((Hbox) getFellow("dbSizeEditBox")).setVisible(false);
                ((Hbox) getFellow("dbSizeBox")).setVisible(true);
                refreshInfo();
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Edits the noConnections of this instance.
     */
    public void editNoConnections() {
        if (FormValidations.isNOConnectionsValid((Textbox) getFellow("noConnectionsEdit"))) {
            //Clone the instance and override noConnections
            Instance clone = instance.clone();
            if (!((Textbox) getFellow("noConnectionsEdit")).getValue().isEmpty())
                clone.setNoConnections(Integer.valueOf(((Textbox) getFellow("noConnectionsEdit")).getValue()));
            else
                clone.setNoConnections(0);
            if (instanceDAO.update(instance, clone, username) > 0) {
                instance = clone;
                if (instance.getNoConnections() > 0)
                    ((Label) getFellow("noConnections")).setValue(String.valueOf(instance.getNoConnections()));
                else
                    ((Label) getFellow("noConnections")).setValue("-");
                ((Hbox) getFellow("noConnectionsEditBox")).setVisible(false);
                ((Hbox) getFellow("noConnectionsBox")).setVisible(true);
                refreshInfo();
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Edits the category of this instance.
     */
    public void editCategory() {
        if (FormValidations.isCategoryValid((Combobox) getFellow("categoryEdit"))) {
            //Clone the instance and override dbSize
            Instance clone = instance.clone();
            clone.setCategory((String)((Combobox) getFellow("categoryEdit")).getSelectedItem().getValue());
            if (instanceDAO.update(instance, clone, username) > 0) {
                instance = clone;
                ((Label) getFellow("category")).setValue(Labels.getLabel(CommonConstants.LABEL_CATEGORY + instance.getCategory()));
                ((Hbox) getFellow("categoryEditBox")).setVisible(false);
                ((Hbox) getFellow("categoryBox")).setVisible(true);
                refreshInfo();
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Edits the database version.
     */
    public void editVersion() {
        if (FormValidations.isVersionValid((Textbox) getFellow("versionEdit"))) {
            //Clone the instance and override version
            Instance clone = instance.clone();
            clone.setVersion(((Textbox) getFellow("versionEdit")).getValue());
            if (instanceDAO.update(instance, clone, username) > 0) {
                instance = clone;
                if (instance.getVersion() != null && !instance.getVersion().isEmpty())
                    ((Label) getFellow("version")).setValue(instance.getVersion());
                else
                    ((Label) getFellow("version")).setValue("-");
                ((Hbox) getFellow("versionEditBox")).setVisible(false);
                ((Hbox) getFellow("versionBox")).setVisible(true);
                refreshInfo();
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Edits the host.
     */
    public void editHost() {
        if (FormValidations.isHostValid((Textbox) getFellow("hostEdit"))) {
            //Clone the instance and override host
            Instance clone = instance.clone();
            clone.setHost(((Textbox) getFellow("hostEdit")).getValue());
            if (instanceDAO.update(instance, clone, username) > 0) {
                instance = clone;
                ((Label) getFellow("host")).setValue(instance.getHost());
                ((Hbox) getFellow("hostEditBox")).setVisible(false);
                ((Hbox) getFellow("hostBox")).setVisible(true);
                refreshInfo();
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Swaps master and slave instances.
     */
    public void swapMasterSlave() {
        //If instance is a master
        if (slave != null) {
            if (instanceDAO.swapMasterSlave(slave.getDbName(), instance.getDbName()) > 0) {
                master = slave;
                slave = null;
                instance.setMaster(master.getDbName());
                instance.setSlave(null);
                ((Hbox) getFellow("masterArea")).setVisible(true);
                ((Hbox) getFellow("emptyArea")).setVisible(false);
                ((Hbox) getFellow("slaveArea")).setVisible(false);
                ((Label) getFellow("master")).setValue(master.getDbName());
                ((Label) getFellow("slave")).setValue(null);
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
        //If instance is a slave
        else if (master != null) {
            if (instanceDAO.swapMasterSlave(instance.getDbName(), master.getDbName()) > 0) {
                slave = master;
                master = null;
                instance.setMaster(null);
                instance.setSlave(slave.getDbName());
                ((Hbox) getFellow("masterArea")).setVisible(false);
                ((Hbox) getFellow("emptyArea")).setVisible(false);
                ((Hbox) getFellow("slaveArea")).setVisible(true);
                ((Label) getFellow("master")).setValue(null);
                ((Label) getFellow("slave")).setValue(slave.getDbName());
            }
            else {
                showError(null, CommonConstants.ERROR_UPDATING_INSTANCE);
            }
        }
    }
    
    /**
     * Loads instance changes.
     */
    private void loadChanges () {
        //Get changes
        changes = instanceDAO.selectInstanceChanges(instance);
        
        //Load grid
        Grid changesGrid = (Grid) getFellow("changesGrid");
        changesGrid.setModel(new SimpleListModel(changes));
        changesGrid.setRowRenderer(new InstanceChangesRenderer());
        
        //Display or hide aready
        if (changes != null && changes.size() > 0) {
            ((Div) getFellow("emptyChangesMsg")).setStyle("display:none");
            ((Grid) getFellow("changesGrid")).setStyle("display:block");
        }
        else {
            ((Div) getFellow("emptyChangesMsg")).setStyle("display:block");
            ((Grid) getFellow("changesGrid")).setStyle("display:none");
        }
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
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(InstanceController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
