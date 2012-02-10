package ch.cern.dod.ui.renderer;

import ch.cern.dod.ui.controller.BackupController;
import ch.cern.dod.ui.controller.MonitoringController;
import ch.cern.dod.ui.controller.RestoreController;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ui.controller.ConfigController;
import ch.cern.dod.ui.controller.UpgradeController;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import ch.cern.dod.util.JobHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * Renderer for grid in databases overview
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class OverviewGridRenderer implements RowRenderer {

    /**
     * Helper to create jobs
     */
    private JobHelper jobHelper;
    /**
     * Checked instances for group operations.
     */
     private ArrayList<DODInstance> checked;

    /**
     * Constructor for this renderer. Only for users, not admins.
     */
    public OverviewGridRenderer() {
        //Get username and adminMode from headers
        Execution execution = Executions.getCurrent();
        String username = execution.getHeader(DODConstants.ADFS_LOGIN);
        String eGroups = execution.getHeader(DODConstants.ADFS_GROUP);
        Boolean adminMode = (Boolean) EGroupHelper.groupInList(DODConstants.ADMIN_E_GROUP, eGroups);
        
        this.jobHelper = new JobHelper(adminMode.booleanValue());
    }
    
    /**
     * Constructor for admins.
     * @param checked checked instances for group operations.
     */
    public OverviewGridRenderer(ArrayList<DODInstance> checked) {
        this.jobHelper = new JobHelper(true);
        this.checked = checked;
    }

    /**
     * Render a given row with the given instance data.
     * @param row row to render
     * @param data DODInstance object to render
     */
    public void render(final Row row, Object data) {
        //Obtain username
        Execution execution = Executions.getCurrent();
        final String username = execution.getHeader(DODConstants.ADFS_LOGIN);

        //Cast database object
        final DODInstance instance = (DODInstance) data;

        //Create date formatter
        DateFormat dateFormatter = new SimpleDateFormat(DODConstants.DATE_FORMAT);
        
        //Render check only for admins
        if (checked != null) {
            Checkbox checkbox = new Checkbox();
            //Check the instance if it was already checked
            if (checked.contains(instance))
                checkbox.setChecked(true);
            checkbox.addEventListener(Events.ON_CHECK, new EventListener() {
                public void onEvent(Event event) {
                    //Update arraylist
                    CheckEvent checkEvent = (CheckEvent) event;
                    if (checkEvent.isChecked()) {
                        if (!checked.contains(instance))
                            checked.add(instance);
                    }
                    else {
                        checked.remove(instance);
                    }
                    //Update buttons
                    if (checked.isEmpty()) {
                        ((Toolbarbutton)row.getGrid().getFellow("startupAllBtn")).setDisabled(true);
                        ((Toolbarbutton)row.getGrid().getFellow("shutdownAllBtn")).setDisabled(true);
                        ((Toolbarbutton)row.getGrid().getFellow("backupAllBtn")).setDisabled(true);
                        ((Toolbarbutton)row.getGrid().getFellow("upgradeAllBtn")).setDisabled(true);
                    }
                    else {
                        ((Toolbarbutton)row.getGrid().getFellow("startupAllBtn")).setDisabled(false);
                        ((Toolbarbutton)row.getGrid().getFellow("shutdownAllBtn")).setDisabled(false);
                        ((Toolbarbutton)row.getGrid().getFellow("backupAllBtn")).setDisabled(false);
                        ((Toolbarbutton)row.getGrid().getFellow("upgradeAllBtn")).setDisabled(false);
                    }
                }
            });
            row.appendChild(checkbox);
        }

        //Render DB name as a link
        Label dbName = getFormattedLabel(instance.getDbName(), 20);
        dbName.setStyle("hyphens:none;text-wrap:none;-webkit-hyphens:none;white-space:nowrap;color:blue;cursor:pointer;text-decoration:underline");
        dbName.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) throws Exception {
                //Create new job and update instance status
                Sessions.getCurrent().setAttribute(DODConstants.INSTANCE, instance);
                Executions.sendRedirect(DODConstants.PAGE_INSTANCE);
            }
        });
        row.appendChild(dbName);

        //Render username
        row.appendChild(getFormattedLabel(instance.getUsername(), 10));

        //Render e-group (if any)
        if (instance.getEGroup() != null && !instance.getEGroup().isEmpty()) {
            row.appendChild(getFormattedLabel(instance.getEGroup(), 20));
        } else {
            row.appendChild(new Label("-"));
        }
        
        //Render category
        row.appendChild(getFormattedLabel(Labels.getLabel(DODConstants.LABEL_CATEGORY + instance.getCategory()), 10));

        //Render project (if any)
        if (instance.getProject() != null && !instance.getProject().isEmpty()) {
            row.appendChild(getFormattedLabel(instance.getProject(), 6));
        } else {
            row.appendChild(new Label("-"));
        }

        //Render creation date(if any)
        row.appendChild(new Label(dateFormatter.format(instance.getCreationDate())));

        //Render expiry date(if any)
        if (instance.getExpiryDate() != null) {
            row.appendChild(new Label(dateFormatter.format(instance.getExpiryDate())));
        } else {
            row.appendChild(new Label("-"));
        }

        //Render db type
        row.appendChild(new Label(Labels.getLabel(DODConstants.LABEL_DB_TYPE + instance.getDbType())));

        //Render db size
        row.appendChild(getFormattedLabel(instance.getDbSize() + " GB", 10));

        //Render number of connections (if any)
        if (instance.getNoConnections() > 0) {
            row.appendChild(getFormattedLabel(String.valueOf(instance.getNoConnections()), 10));
        } else {
            row.appendChild(new Label("-"));
        }

        //Render state as an image with tooltiptext
        Image state = new Image();
        state.setWidth("20px");
        state.setHeight("20px");
        if (instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
            state.setTooltiptext(Labels.getLabel(DODConstants.LABEL_STATE + instance.getState()));
            state.setSrc(DODConstants.IMG_AWAITING_APPROVAL);
        } else if (instance.getState().equals(DODConstants.INSTANCE_STATE_JOB_PENDING)) {
            state.setTooltiptext(Labels.getLabel(DODConstants.LABEL_STATE + instance.getState()));
            state.setSrc(DODConstants.IMG_PENDING);
        } else if (instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)) {
            state.setTooltiptext(Labels.getLabel(DODConstants.LABEL_STATE + instance.getState()));
            state.setSrc(DODConstants.IMG_RUNNING);
        } else if (instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            state.setTooltiptext(Labels.getLabel(DODConstants.LABEL_STATE + instance.getState()));
            state.setSrc(DODConstants.IMG_STOPPED);
        } else if (instance.getState().equals(DODConstants.INSTANCE_STATE_MAINTENANCE)) {
            state.setTooltiptext(Labels.getLabel(DODConstants.LABEL_STATE + instance.getState()));
            state.setSrc(DODConstants.IMG_MAINTENANCE);
        }
        row.appendChild(state);

        //Append buttons to row
        final Hbox box = new Hbox();

        //Startup button
        final Toolbarbutton startupBtn = new Toolbarbutton();
        startupBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB + DODConstants.JOB_STARTUP));
        startupBtn.setImage(DODConstants.IMG_STARTUP);
        startupBtn.setParent(box);
        startupBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                //Create new job and update instance status
                if (jobHelper.doStartup(instance, username))
                    row.getGrid().setModel(row.getGrid().getListModel());
                else
                    showError(row, null, DODConstants.ERROR_DISPATCHING_JOB);
            }
        });
        //Only enable button if the instance is stopped
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            startupBtn.setDisabled(true);
            startupBtn.setSclass(DODConstants.STYLE_BUTTON_DISABLED);
        } else {
            startupBtn.setSclass(DODConstants.STYLE_BUTTON);
        }

        //Shutdown button
        final Toolbarbutton shutdownBtn = new Toolbarbutton();
        shutdownBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB + DODConstants.JOB_SHUTDOWN));
        shutdownBtn.setImage(DODConstants.IMG_SHUTDOWN);
        shutdownBtn.setParent(box);
        shutdownBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                //Create new job and update instance status
                if (jobHelper.doShutdown(instance, username))
                    row.getGrid().setModel(row.getGrid().getListModel());
                else
                    showError(row, null, DODConstants.ERROR_DISPATCHING_JOB);
            }
        });
        //Only enable button if the instance is running
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)) {
            shutdownBtn.setDisabled(true);
            shutdownBtn.setSclass(DODConstants.STYLE_BUTTON_DISABLED);
        } else {
            shutdownBtn.setSclass(DODConstants.STYLE_BUTTON);
        }

        //Config files button
        final Toolbarbutton configButton = new Toolbarbutton();
        configButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CONFIG_TITLE));
        configButton.setImage(DODConstants.IMG_CONFIG);
        configButton.setParent(box);
        configButton.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                try {
                    ConfigController configController = new ConfigController(instance, username, jobHelper);
                    //Only show window if it is not already being diplayed
                    if (row.getRoot().getFellowIfAny(configController.getId()) == null) {
                        configController.setParent(row.getRoot());
                        configController.doModal();
                    }
                } catch (InterruptedException ex) {
                    showError(row, ex, DODConstants.ERROR_DISPATCHING_JOB);
                }
            }
        });

        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            configButton.setDisabled(true);
            configButton.setSclass(DODConstants.STYLE_BUTTON_DISABLED);
        } else {
            configButton.setSclass(DODConstants.STYLE_BUTTON);
        }

        //Dispatch a backup button
        final Toolbarbutton backupBtn = new Toolbarbutton();
        backupBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB + DODConstants.JOB_BACKUP));
        backupBtn.setImage(DODConstants.IMG_BACKUP);
        backupBtn.setParent(box);
        backupBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                try {
                    BackupController backupController = new BackupController(instance, username, jobHelper);
                    //Only show window if it is not already being diplayed
                    if (row.getRoot().getFellowIfAny(backupController.getId()) == null) {
                        backupController.setParent(row.getRoot());
                        backupController.doModal();
                    }
                } catch (InterruptedException ex) {
                    showError(row, ex, DODConstants.ERROR_DISPATCHING_JOB);
                }
            }
        });

        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            backupBtn.setDisabled(true);
            backupBtn.setSclass(DODConstants.STYLE_BUTTON_DISABLED);
        } else {
            backupBtn.setSclass(DODConstants.STYLE_BUTTON);
        }

        //Dispatch a restore button
        final Toolbarbutton restoreBtn = new Toolbarbutton();
        restoreBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB + DODConstants.JOB_RESTORE));
        restoreBtn.setImage(DODConstants.IMG_RESTORE);
        restoreBtn.setParent(box);
        restoreBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                try {
                    RestoreController restoreController = new RestoreController(instance, username, jobHelper);
                    //Only show window if it is not already being diplayed
                    if (row.getRoot().getFellowIfAny(restoreController.getId()) == null) {
                        restoreController.setParent(row.getRoot());
                        restoreController.doModal();
                    }
                } catch (InterruptedException ex) {
                    showError(row, ex, DODConstants.ERROR_DISPATCHING_JOB);
                }
            }
        });

        //Only enable button if the instance is stopped or running
        if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
            restoreBtn.setDisabled(true);
            restoreBtn.setSclass(DODConstants.STYLE_BUTTON_DISABLED);
        } else {
            restoreBtn.setSclass(DODConstants.STYLE_BUTTON);
        }
        
        //Upgrade button
        final Toolbarbutton upgradeBtn = new Toolbarbutton();
        upgradeBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB + DODConstants.JOB_UPGRADE));
        upgradeBtn.setImage(DODConstants.IMG_UPGRADE);
        upgradeBtn.setParent(box);
        upgradeBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                //Show destroy window
                try {
                    UpgradeController upgradeController = new UpgradeController(instance, username, jobHelper);
                    //Only show window if it is not already being diplayed
                    if (row.getRoot().getFellowIfAny(upgradeController.getId()) == null) {
                        upgradeController.setParent(row.getRoot());
                        upgradeController.doModal();
                    }
                } catch (InterruptedException ex) {
                    showError(row, ex, DODConstants.ERROR_DISPATCHING_JOB);
                }
            }
        });
        //Only enable button if the instance is stopped or running (and there is an upgrade available)
        if ((!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED))
                || instance.getUpgradeTo() == null || instance.getUpgradeTo().isEmpty()) {
            upgradeBtn.setDisabled(true);
            upgradeBtn.setSclass(DODConstants.STYLE_BUTTON_DISABLED);
        } else {
            upgradeBtn.setSclass(DODConstants.STYLE_BUTTON);
        }

        //Access monitoring button
        final Toolbarbutton monitorBtn = new Toolbarbutton();
        monitorBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB + DODConstants.JOB_MONITOR));
        monitorBtn.setImage(DODConstants.IMG_MONITOR);
        monitorBtn.setParent(box);
        monitorBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                try {
                    MonitoringController monitoringController = new MonitoringController(instance);
                    //Only show window if it is not already being diplayed
                    if (row.getRoot().getFellowIfAny(monitoringController.getId()) == null) {
                        monitoringController.setParent(row.getRoot());
                        monitoringController.doModal();
                    }
                } catch (InterruptedException ex) {
                    showError(row, ex, DODConstants.ERROR_DISPATCHING_JOB);
                }
            }
        });

        //Only disable button if the instance is awaiting approval
        if (instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
            monitorBtn.setDisabled(true);
            monitorBtn.setSclass(DODConstants.STYLE_BUTTON_DISABLED);
        } else {
            monitorBtn.setSclass(DODConstants.STYLE_BUTTON);
        }

        //Add box to row
        row.appendChild(box);
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
        if (text.length() > maxLength) {
            toret.setTooltiptext(text);
        }
        toret.setStyle("hyphens:none;text-wrap:none;-webkit-hyphens:none;white-space:nowrap;");
        return toret;
    }

    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(Row row, Exception exception, String errorCode) {
        if (exception != null) {
            Logger.getLogger(OverviewGridRenderer.class.getName()).log(Level.SEVERE, "ERROR DISPATCHING JOB", exception);
        }
        Window errorWindow = (Window) row.getGrid().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(OverviewGridRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(OverviewGridRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
