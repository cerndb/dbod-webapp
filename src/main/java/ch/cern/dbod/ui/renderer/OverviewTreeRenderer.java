/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.renderer;

import ch.cern.dbod.util.ConfigLoader;
import ch.cern.dbod.db.dao.InstanceDAO;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.ui.controller.BackupController;
import ch.cern.dbod.ui.controller.FileController;
import ch.cern.dbod.ui.controller.RestoreController;
import ch.cern.dbod.ui.controller.ShutdownController;
import ch.cern.dbod.ui.controller.UpgradeController;
import ch.cern.dbod.ui.model.OverviewTreeModel;
import ch.cern.dbod.ui.model.OverviewTreeNode;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.EGroupHelper;
import ch.cern.dbod.util.JobHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

/**
 * Renders rows in the overview tree. It displays all the info and creates
 * the action buttons. It also manages events in case an instance is checked
 * or unchecked and the collective action buttons have to be enabled or disabled.
 * @author Daniel Gomez Blanco
 */
public class OverviewTreeRenderer implements TreeitemRenderer{
    
    /**
     * Instance DAO
     */
    private InstanceDAO instanceDAO;
    /**
     * Helper to create jobs
     */
    private JobHelper jobHelper;
     
     /**
      * Indicates if ceckboxes should be created or not
      */
     private boolean checkboxes;
    
    /**
     * Constructor.
     * @param checkboxes boolean indicating if checkboxes should be created instances for group operations.
     */
    public OverviewTreeRenderer(boolean checkboxes) {
        Execution execution = Executions.getCurrent();
        String eGroups = execution.getHeader(CommonConstants.ADFS_GROUP);
        Boolean adminMode = (Boolean) EGroupHelper.groupInList(ConfigLoader.getProperty(CommonConstants.ADMIN_E_GROUP), eGroups);
        this.instanceDAO = new InstanceDAO();
        this.jobHelper = new JobHelper(adminMode.booleanValue());
        this.checkboxes = checkboxes;
    }

    /**
     * Renders an instance in the tree.
     * @param item Tree item to render components into.
     * @param node Tree node with the instance to render.
     * @param i index of the node
     * @throws Exception In case instance cannot be rendered.
     */
    @Override
    public void render(final Treeitem item, final Object node, int i) throws Exception {
        //Only render instance if it's filtered
        if (node != null) {
            //Render normal instance
            if (((OverviewTreeNode) node).getData() instanceof Instance) {                
                //Obtain username
                Execution execution = Executions.getCurrent();
                final String username = execution.getHeader(CommonConstants.ADFS_LOGIN);

                //Cast database object
                final Instance instance = (Instance)((OverviewTreeNode) node).getData();

                //Create row
                final Treerow row = new Treerow();

                //Render check only in admin page
                if (checkboxes) {
                    Treecell checkboxCell = new Treecell();
                    Checkbox checkbox = new Checkbox();
                    //Check the instance if it was already checked
                    if (instance.isChecked()) {
                        checkbox.setChecked(true);
                    }
                    checkbox.addEventListener(Events.ON_CHECK, new EventListener() {
                        @Override
                        public void onEvent(Event event) {
                            //Update arraylist
                            CheckEvent checkEvent = (CheckEvent) event;
                            if (checkEvent.isChecked()) {
                                instance.setChecked(true);
                            }
                            else {
                                instance.setChecked(false);
                            }
                            //If there are no checked instnces disable button   
                            if (((OverviewTreeModel)row.getTree().getModel()).getChecked((OverviewTreeNode)row.getTree().getModel().getRoot()).isEmpty()) {
                                ((Toolbarbutton)row.getTree().getFellow("startupAllBtn")).setDisabled(true);
                                ((Toolbarbutton)row.getTree().getFellow("shutdownAllBtn")).setDisabled(true);
                                ((Toolbarbutton)row.getTree().getFellow("backupAllBtn")).setDisabled(true);
                                ((Toolbarbutton)row.getTree().getFellow("upgradeAllBtn")).setDisabled(true);
                                ((Toolbarbutton)row.getTree().getFellow("maintainAllBtn")).setDisabled(true);
                                ((Toolbarbutton)row.getTree().getFellow("startupAllBtn")).setZclass("buttonDisabled");
                                ((Toolbarbutton)row.getTree().getFellow("shutdownAllBtn")).setZclass("buttonDisabled");
                                ((Toolbarbutton)row.getTree().getFellow("backupAllBtn")).setZclass("buttonDisabled");
                                ((Toolbarbutton)row.getTree().getFellow("upgradeAllBtn")).setZclass("buttonDisabled");
                                ((Toolbarbutton)row.getTree().getFellow("maintainAllBtn")).setZclass("buttonDisabled");
                                ((Checkbox)row.getTree().getFellow("checkAll")).setChecked(false);
                            }
                            else {
                                ((Toolbarbutton)row.getTree().getFellow("startupAllBtn")).setDisabled(false);
                                ((Toolbarbutton)row.getTree().getFellow("shutdownAllBtn")).setDisabled(false);
                                ((Toolbarbutton)row.getTree().getFellow("backupAllBtn")).setDisabled(false);
                                ((Toolbarbutton)row.getTree().getFellow("upgradeAllBtn")).setDisabled(false);
                                ((Toolbarbutton)row.getTree().getFellow("maintainAllBtn")).setDisabled(false);
                                ((Toolbarbutton)row.getTree().getFellow("startupAllBtn")).setZclass("button");
                                ((Toolbarbutton)row.getTree().getFellow("shutdownAllBtn")).setZclass("button");
                                ((Toolbarbutton)row.getTree().getFellow("backupAllBtn")).setZclass("button");
                                ((Toolbarbutton)row.getTree().getFellow("upgradeAllBtn")).setZclass("button");
                                ((Toolbarbutton)row.getTree().getFellow("maintainAllBtn")).setZclass("button");
                            }
                        }
                    });
                    checkboxCell.appendChild(checkbox);
                    row.appendChild(checkboxCell);
                }

                //Render DB name as a link
                Treecell dbNameCell = new Treecell();
                Html dbName = new Html();
                
                //Admin view and the instance is not in FIM
                //if (checkboxes && instance.getUser() == null)
                //    dbName.setContent("<a style=\"text-decoration:none;color:red\" class=\"z-label\" href=\""
                //                    + Executions.encodeURL(CommonConstants.PAGE_INSTANCE + "?" + CommonConstants.INSTANCE + "=" + instance.getDbName()) 
                //                    +"\">" + instance.getDbName() + "</a>");
                //else
                    dbName.setContent("<a style=\"text-decoration:none;color:blue\" class=\"z-label\" href=\""
                                    + Executions.encodeURL(CommonConstants.PAGE_INSTANCE + "?" + CommonConstants.INSTANCE + "=" + instance.getDbName()) 
                                    +"\">" + instance.getDbName() + "</a>");
                
                dbNameCell.appendChild(dbName);
                //If instance is master append (M) to name
                if (((OverviewTreeNode) node).getChildCount() > 0)
                    dbNameCell.appendChild(new Label(" (M)"));
                //If instance is slave append (S) to name
                if ( ((OverviewTreeNode) node).getParent().getData() instanceof Instance)
                    dbNameCell.appendChild(new Label(" (S)"));
                row.appendChild(dbNameCell);
                
                //Render host
                Treecell hostCell = new Treecell();
                hostCell.appendChild(getFormattedLabel(instance.getHost(), 15));
                row.appendChild(hostCell);

                //Render username
                Treecell usernameCell = new Treecell();
                usernameCell.appendChild(getFormattedLabel(instance.getOwner(), 10));
                //Admin view and the instance's user doesn't match with FIM
                if (checkboxes && instance.getUser() != null && !instance.getOwner().equals(instance.getUser().getLogin()))
                    usernameCell.setStyle("color:red;");
                row.appendChild(usernameCell);

                //Render e-group (if any)
                Treecell eGroupCell = new Treecell();
                if (instance.getEGroup() != null && !instance.getEGroup().isEmpty()) {
                    eGroupCell.appendChild(getFormattedLabel(instance.getEGroup(), 20));
                } else {
                    eGroupCell.appendChild(new Label("-"));
                }
                row.appendChild(eGroupCell);

                //Render category
                Treecell categoryCell = new Treecell();
                categoryCell.appendChild(getFormattedLabel(Labels.getLabel(CommonConstants.LABEL_CATEGORY + instance.getCategory()), 10));
                row.appendChild(categoryCell);

                //Render project (if any)
                Treecell projectCell = new Treecell();
                if (instance.getProject() != null && !instance.getProject().isEmpty()) {
                    projectCell.appendChild(getFormattedLabel(instance.getProject(), 20));
                } else {
                    projectCell.appendChild(new Label("-"));
                }
                row.appendChild(projectCell);

                //Render db type
                Treecell dbTypeCell = new Treecell();
                dbTypeCell.appendChild(new Label(Labels.getLabel(CommonConstants.LABEL_DB_TYPE + instance.getDbType())));
                row.appendChild(dbTypeCell);

                //Render state as an image with tooltiptext
                Image state = new Image();
                state.setWidth("20px");
                state.setHeight("20px");
                switch (instance.getState()) {
                    case CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL:
                        state.setSrc(CommonConstants.IMG_AWAITING_APPROVAL);
                        break;
                    case CommonConstants.INSTANCE_STATE_JOB_PENDING:
                        state.setSrc(CommonConstants.IMG_PENDING);
                        break;
                    case CommonConstants.INSTANCE_STATE_RUNNING:
                        state.setSrc(CommonConstants.IMG_RUNNING);
                        break;
                    case CommonConstants.INSTANCE_STATE_STOPPED:
                        state.setSrc(CommonConstants.IMG_STOPPED);
                        break;
                    case CommonConstants.INSTANCE_STATE_MAINTENANCE:
                        state.setSrc(CommonConstants.IMG_MAINTENANCE);
                        break;
                    case CommonConstants.INSTANCE_STATE_BUSY:
                        state.setSrc(CommonConstants.IMG_BUSY);
                        break;
                    case CommonConstants.INSTANCE_STATE_UNKNOWN:
                        state.setSrc(CommonConstants.IMG_UNKNOWN);
                        break;
                }
                state.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_STATE + instance.getState()));
                Treecell stateCell = new Treecell();
                stateCell.appendChild(state);
                row.appendChild(stateCell);

                //Append buttons to row
                final Hbox box = new Hbox();

                //Startup button
                final Toolbarbutton startupBtn = new Toolbarbutton();
                startupBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB + CommonConstants.JOB_STARTUP));
                startupBtn.setImage(CommonConstants.IMG_STARTUP);
                startupBtn.setParent(box);
                startupBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        //Create new job and update instance status
                        if (jobHelper.doStartup(instance, username)) {
                            //Reload the row
                            ((OverviewTreeModel)row.getTree().getModel()).updateNode((OverviewTreeNode)node);
                        }
                        else
                            showError(item, null, CommonConstants.ERROR_DISPATCHING_JOB);
                    }
                });
                //Only enable button if the instance is stopped
                if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)) {
                    startupBtn.setDisabled(true);
                    startupBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                } else {
                    startupBtn.setZclass(CommonConstants.STYLE_BUTTON);
                }

                //Shutdown button
                final Toolbarbutton shutdownBtn = new Toolbarbutton();
                shutdownBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB + CommonConstants.JOB_SHUTDOWN));
                shutdownBtn.setImage(CommonConstants.IMG_SHUTDOWN);
                shutdownBtn.setParent(box);
                shutdownBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        //Show shutdown window
                        try {
                            ShutdownController shutdownController = new ShutdownController(instance, username, jobHelper, (OverviewTreeModel)row.getTree().getModel());
                            //Only show window if it is not already being diplayed
                            if (row.getRoot().getFellowIfAny(shutdownController.getId()) == null) {
                                shutdownController.setParent(row.getRoot());
                                shutdownController.doModal();
                            }
                        } catch (InterruptedException ex) {
                            showError(item, ex, CommonConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });
                //Only enable button if the instance is running
                if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
                    shutdownBtn.setDisabled(true);
                    shutdownBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                } else {
                    shutdownBtn.setZclass(CommonConstants.STYLE_BUTTON);
                }

                //Config files button
                final Toolbarbutton filesButton = new Toolbarbutton();
                filesButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_FILES_POPUP));
                filesButton.setImage(CommonConstants.IMG_FILES);
                filesButton.setParent(box);
                filesButton.addEventListener(Events.ON_CLICK, new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        try {
                            FileController fileController = new FileController(instance, username, jobHelper, (OverviewTreeModel)row.getTree().getModel());
                            //Only show window if it is not already being diplayed
                            if (row.getRoot().getFellowIfAny(fileController.getId()) == null) {
                                fileController.setParent(row.getRoot());
                                fileController.doModal();
                            }
                        } catch (InterruptedException ex) {
                            showError(item, ex, CommonConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });

                //Only enable button if the instance is stopped or running
                if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
                    filesButton.setDisabled(true);
                    filesButton.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                } else {
                    filesButton.setZclass(CommonConstants.STYLE_BUTTON);
                }

                //Dispatch a backup button
                final Toolbarbutton backupBtn = new Toolbarbutton();
                backupBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_BACKUP_POPUP));
                backupBtn.setImage(CommonConstants.IMG_BACKUP);
                backupBtn.setParent(box);
                backupBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        try {
                            BackupController backupController = new BackupController(instance, username, jobHelper, (OverviewTreeModel)row.getTree().getModel());
                            //Only show window if it is not already being diplayed
                            if (row.getRoot().getFellowIfAny(backupController.getId()) == null) {
                                backupController.setParent(row.getRoot());
                                backupController.doModal();
                            }
                        } catch (InterruptedException ex) {
                            showError(item, ex, CommonConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });

                //Only enable button if the instance is stopped or running
                if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
                    backupBtn.setDisabled(true);
                    backupBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                } else {
                    backupBtn.setZclass(CommonConstants.STYLE_BUTTON);
                }

                //Dispatch a restore button
                final Toolbarbutton restoreBtn = new Toolbarbutton();
                restoreBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB + CommonConstants.JOB_RESTORE));
                restoreBtn.setImage(CommonConstants.IMG_RESTORE);
                restoreBtn.setParent(box);
                restoreBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        try {
                            RestoreController restoreController = new RestoreController(instance, username, jobHelper, (OverviewTreeModel)row.getTree().getModel());
                            //Only show window if it is not already being diplayed
                            if (row.getRoot().getFellowIfAny(restoreController.getId()) == null) {
                                restoreController.setParent(row.getRoot());
                                restoreController.doModal();
                            }
                        } catch (InterruptedException ex) {
                            showError(item, ex, CommonConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });

                //Only enable button if the instance is stopped or running
                if (!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN)) {
                    restoreBtn.setDisabled(true);
                    restoreBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                } else {
                    restoreBtn.setZclass(CommonConstants.STYLE_BUTTON);
                }
                
                //At the moment not available for Influx instances
                if (instance.getDbType().equals(CommonConstants.DB_TYPE_INFLUX)) {
                    restoreBtn.setDisabled(true);
                    restoreBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                }

                //Upgrade button
                final Toolbarbutton upgradeBtn = new Toolbarbutton();
                upgradeBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB + CommonConstants.JOB_UPGRADE));
                upgradeBtn.setImage(CommonConstants.IMG_UPGRADE);
                upgradeBtn.setParent(box);
                upgradeBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                    @Override
                    public void onEvent(Event event) {
                        //Show upgrade window
                        try {
                            UpgradeController upgradeController = new UpgradeController(instance, username, jobHelper, (OverviewTreeModel)row.getTree().getModel());
                            //Only show window if it is not already being diplayed
                            if (row.getRoot().getFellowIfAny(upgradeController.getId()) == null) {
                                upgradeController.setParent(row.getRoot());
                                upgradeController.doModal();
                            }
                        } catch (InterruptedException ex) {
                            showError(item, ex, CommonConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });
                
                //Only enable button if the instance is not shared stopped or running (and there is an upgrade available)
                if ((!instance.getState().equals(CommonConstants.INSTANCE_STATE_RUNNING)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_STOPPED)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_BUSY)
                        && !instance.getState().equals(CommonConstants.INSTANCE_STATE_UNKNOWN))
                        || instance.getUpgradeTo() == null || instance.getUpgradeTo().isEmpty()) {
                    upgradeBtn.setDisabled(true);
                    upgradeBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                } else {
                    upgradeBtn.setZclass(CommonConstants.STYLE_BUTTON);
                }

                //Access monitoring button
                final Toolbarbutton monitorBtn = new Toolbarbutton();
                monitorBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB + CommonConstants.JOB_MONITOR));
                monitorBtn.setImage(CommonConstants.IMG_MONITOR);
                monitorBtn.setParent(box);
                
                //Disable button if the instance is awaiting approval
                if (instance.getState().equals(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
                    monitorBtn.setDisabled(true);
                    monitorBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                } else {
                    monitorBtn.setZclass(CommonConstants.STYLE_BUTTON);
                }
                
                switch (instance.getDbType()) {
                    case CommonConstants.DB_TYPE_ORACLE_12:
                        //If it is an oracle instance, send to OEM
                        monitorBtn.setTarget("_blank");
                        monitorBtn.setHref(CommonConstants.OEM_URL + instance.getHost().toUpperCase() + ".cern.ch_" + instance.getDbName().toString().toUpperCase());
                        break;
                    case CommonConstants.DB_TYPE_INFLUX:
                        monitorBtn.setTarget("_blank");
                        monitorBtn.setHref(String.format(ConfigLoader.getProperty(CommonConstants.INFLUX_MONITORING), instance.getHost(), instance.getDbName()));
                        break;
                    case CommonConstants.DB_TYPE_PG:
                        //if (instance.getCategory().equals(CommonConstants.CATEGORY_OFFICIAL)) {
                        //    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        //    String date = dateFormat.format(new Date());
                        //    String sec_token = DigestUtils.sha256Hex(ConfigLoader.getProperty(CommonConstants.APPDYN_TOKEN) + ":" + instance.getDbName() + ":" + date);
                        //    String appdynURL = ConfigLoader.getProperty(CommonConstants.APPDYN_DBTUNA4PG) + instance.getDbName() + "&sec_token=" + sec_token;
                        //    monitorBtn.setTarget("_blank");
                        //    monitorBtn.setHref(appdynURL);
                        //} else {
                            monitorBtn.setTarget("_blank");
                            monitorBtn.setHref(String.format(ConfigLoader.getProperty(CommonConstants.POSTGRES_MONITORING), instance.getHost(), instance.getDbName()));
                        //}
                        break;
                    case CommonConstants.DB_TYPE_MYSQL:
                    //    if (instance.getCategory().equals(CommonConstants.CATEGORY_OFFICIAL)) {
                    //        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    //        String date = dateFormat.format(new Date());
                    //        String sec_token = DigestUtils.sha256Hex(ConfigLoader.getProperty(CommonConstants.APPDYN_TOKEN) + ":" + instance.getDbName() + ":" + date);
                    //        String appdynURL = ConfigLoader.getProperty(CommonConstants.APPDYN_DBTUNA) + instance.getDbName() + "&sec_token=" + sec_token;
                    //        monitorBtn.setTarget("_blank");
                    //        monitorBtn.setHref(appdynURL);
                    //    } else {
                            monitorBtn.setTarget("_blank");
                            monitorBtn.setHref(String.format(ConfigLoader.getProperty(CommonConstants.MYSQL_MONITORING), instance.getHost(), instance.getDbName()));
                    //    }
                        break;
                    default:
                        monitorBtn.setDisabled(true);
                        monitorBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                }
                
                //Host monitoring button
                final Toolbarbutton hostMonitorBtn = new Toolbarbutton();
                hostMonitorBtn.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_JOB + CommonConstants.JOB_HOSTMONITOR));
                hostMonitorBtn.setImage(CommonConstants.IMG_HOSTMONITOR);
                hostMonitorBtn.setParent(box);
                
                String kibanaURL = ConfigLoader.getProperty(CommonConstants.KIBANA_DASHBOARD) + instance.getHost();

                hostMonitorBtn.setTarget("_blank");
                hostMonitorBtn.setHref(kibanaURL);

                //Only disable button if the instance is awaiting approval
                if (instance.getState().equals(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
                    hostMonitorBtn.setDisabled(true);
                    hostMonitorBtn.setZclass(CommonConstants.STYLE_BUTTON_DISABLED);
                } else {
                    hostMonitorBtn.setZclass(CommonConstants.STYLE_BUTTON);
                }

                //Add box to row
                Treecell actionsCell = new Treecell();
                actionsCell.appendChild(box);
                row.appendChild(actionsCell);

                //Add row to item
                item.appendChild(row);
            }
        }
    }
    
    /**
     * Formats a label to fit in the grid columns, with no wrap.
     * @param text text to format
     * @param maxLength maximum length of the text
     * @return Label object with the formated text
     */
    private Label getFormattedLabel(String text, int maxLength) {
        Label toret = new Label(text);
        toret.setMaxlength(maxLength);
        if (text != null && text.length() > maxLength) {
            toret.setTooltiptext(text);
        }
        toret.setStyle("hyphens:none;text-wrap:none;-webkit-hyphens:none;white-space:nowrap;");
        return toret;
    }

    /**
     * Displays an error window for the error code provided.
     * @param item item where the error was generated
     * @param exception exception to log
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(Treeitem item, Exception exception, String errorCode) {
        if (exception != null) {
            Logger.getLogger(OverviewTreeRenderer.class.getName()).log(Level.SEVERE, "ERROR DISPATCHING JOB", exception);
        }
        Window errorWindow = (Window) item.getTree().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(OverviewTreeRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
