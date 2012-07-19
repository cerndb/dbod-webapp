package ch.cern.dod.ui.renderer;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ui.controller.*;
import ch.cern.dod.ui.model.OverviewTreeNode;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import ch.cern.dod.util.JobHelper;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
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
import org.zkoss.zul.*;

/**
 * Renders rows in the overview tree.
 * @author Daniel Gomez Blanco
 */
public class OverviewTreeRenderer implements TreeitemRenderer{
    
    /**
     * Helper to create jobs
     */
    private JobHelper jobHelper;
    /**
     * Checked instances for group operations.
     */
     private List<DODInstance> checked;

     /**
     * Items that are closed.
     */
     private List<String> closed;
    
    /**
     * Constructor.
     * @param checkboxes boolean indicating if checkboxes should be created instances for group operations.
     */
    public OverviewTreeRenderer(boolean checkboxes) {
        Execution execution = Executions.getCurrent();
        String eGroups = execution.getHeader(DODConstants.ADFS_GROUP);
        Boolean adminMode = (Boolean) EGroupHelper.groupInList(DODConstants.ADMIN_E_GROUP, eGroups);
        this.jobHelper = new JobHelper(adminMode.booleanValue());
        this.closed = new ArrayList<String>();
        if (checkboxes)
            this.checked = new ArrayList<DODInstance>();
    }

    /**
     * Renders an instance in the tree.
     * @param item Tree item to render.
     * @param data Tree node with the instance to render.
     * @throws Exception In case instance cannot be rendered.
     */
    public void render(final Treeitem item, final Object data) throws Exception {
        //Only render instance if it's filtered
        if (data != null && filterNode ((OverviewTreeNode)data, item.getTree())) {
            
            //Render normal instance
            if (((OverviewTreeNode) data).getData() instanceof DODInstance) {
                //Obtain username
                Execution execution = Executions.getCurrent();
                final String username = execution.getHeader(DODConstants.ADFS_LOGIN);

                //Cast database object
                final DODInstance instance = (DODInstance)((OverviewTreeNode) data).getData();

                //Create date formatter
                DateFormat dateFormatter = new SimpleDateFormat(DODConstants.DATE_FORMAT);

                //Create row
                final Treerow row = new Treerow();
                
                //Remove or add item to closed items list if opened
                item.addEventListener(Events.ON_OPEN, new EventListener() {
                    public void onEvent(Event event) {
                        //Remove or add item to closed list
                        if (item.isOpen())
                            closed.remove(instance.getDbName());
                        else
                            closed.add(instance.getDbName()); 
                    }
                });

                //Render check only for admins
                if (checked != null) {
                    Treecell checkboxCell = new Treecell();
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
                                ((Toolbarbutton)row.getTree().getFellow("startupAllBtn")).setDisabled(true);
                                ((Toolbarbutton)row.getTree().getFellow("shutdownAllBtn")).setDisabled(true);
                                ((Toolbarbutton)row.getTree().getFellow("backupAllBtn")).setDisabled(true);
                                ((Toolbarbutton)row.getTree().getFellow("upgradeAllBtn")).setDisabled(true);
                            }
                            else {
                                ((Toolbarbutton)row.getTree().getFellow("startupAllBtn")).setDisabled(false);
                                ((Toolbarbutton)row.getTree().getFellow("shutdownAllBtn")).setDisabled(false);
                                ((Toolbarbutton)row.getTree().getFellow("backupAllBtn")).setDisabled(false);
                                ((Toolbarbutton)row.getTree().getFellow("upgradeAllBtn")).setDisabled(false);
                            }
                        }
                    });
                    checkboxCell.appendChild(checkbox);
                    row.appendChild(checkboxCell);
                }

                //Render DB name as a link
                Treecell dbNameCell = new Treecell();
                Html dbName = new Html();
                dbName.setContent("<a style=\"text-decoration:underline;color:blue\" class=\"z-label\" href=\""
                                    + Executions.encodeURL(DODConstants.PAGE_INSTANCE + "?" + DODConstants.INSTANCE + "=" + instance.getDbName()) 
                                    +"\">" + instance.getDbName() + "</a>");
                dbNameCell.appendChild(dbName);
                //If instance is master append (M) to name
                if (instance.getSlave() != null && !instance.getSlave().isEmpty())
                    dbNameCell.appendChild(new Label(" (M)"));
                //If instance is slave append (S) to name
                if (instance.getMaster() != null && !instance.getMaster().isEmpty())
                    dbNameCell.appendChild(new Label(" (S)"));
                row.appendChild(dbNameCell);

                //Render username
                Treecell usernameCell = new Treecell();
                usernameCell.appendChild(getFormattedLabel(instance.getUsername(), 10));
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
                categoryCell.appendChild(getFormattedLabel(Labels.getLabel(DODConstants.LABEL_CATEGORY + instance.getCategory()), 10));
                row.appendChild(categoryCell);

                //Render project (if any)
                Treecell projectCell = new Treecell();
                if (instance.getProject() != null && !instance.getProject().isEmpty()) {
                    projectCell.appendChild(getFormattedLabel(instance.getProject(), 6));
                } else {
                    projectCell.appendChild(new Label("-"));
                }
                row.appendChild(projectCell);

                //Render creation date(if any)
                Treecell creationCell = new Treecell();
                creationCell.appendChild(new Label(dateFormatter.format(instance.getCreationDate())));
                row.appendChild(creationCell);


                //Render expiry date(if any)
                Treecell expiryCell = new Treecell();
                if (instance.getExpiryDate() != null) {
                    expiryCell.appendChild(new Label(dateFormatter.format(instance.getExpiryDate())));
                } else {
                    expiryCell.appendChild(new Label("-"));
                }
                row.appendChild(expiryCell);

                //Render db type
                Treecell dbTypeCell = new Treecell();
                dbTypeCell.appendChild(new Label(Labels.getLabel(DODConstants.LABEL_DB_TYPE + instance.getDbType())));
                row.appendChild(dbTypeCell);

                //Render db size
                Treecell dbSizeCell = new Treecell();
                dbSizeCell.appendChild(getFormattedLabel(instance.getDbSize() + " GB", 10));
                row.appendChild(dbSizeCell);

                //Render number of connections (if any)
                Treecell noConnectionsCell = new Treecell();
                if (instance.getNoConnections() > 0) {
                    noConnectionsCell.appendChild(getFormattedLabel(String.valueOf(instance.getNoConnections()), 10));
                } else {
                    noConnectionsCell.appendChild(new Label("-"));
                }
                row.appendChild(noConnectionsCell);

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
                Treecell stateCell = new Treecell();
                stateCell.appendChild(state);
                row.appendChild(stateCell);

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
                            row.getTree().setModel(row.getTree().getModel());
                        else
                            showError(item, null, DODConstants.ERROR_DISPATCHING_JOB);
                    }
                });
                //Only enable button if the instance is stopped
                if (!instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
                    startupBtn.setDisabled(true);
                    startupBtn.setZclass(DODConstants.STYLE_BUTTON_DISABLED);
                } else {
                    startupBtn.setZclass(DODConstants.STYLE_BUTTON);
                }

                //Shutdown button
                final Toolbarbutton shutdownBtn = new Toolbarbutton();
                shutdownBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB + DODConstants.JOB_SHUTDOWN));
                shutdownBtn.setImage(DODConstants.IMG_SHUTDOWN);
                shutdownBtn.setParent(box);
                shutdownBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                    public void onEvent(Event event) {
                        //Show shutdown window
                        try {
                            ShutdownController shutdownController = new ShutdownController(instance, username, jobHelper);
                            //Only show window if it is not already being diplayed
                            if (row.getRoot().getFellowIfAny(shutdownController.getId()) == null) {
                                shutdownController.setParent(row.getRoot());
                                shutdownController.doModal();
                            }
                        } catch (InterruptedException ex) {
                            showError(item, ex, DODConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });
                //Only enable button if the instance is running
                if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)) {
                    shutdownBtn.setDisabled(true);
                    shutdownBtn.setZclass(DODConstants.STYLE_BUTTON_DISABLED);
                } else {
                    shutdownBtn.setZclass(DODConstants.STYLE_BUTTON);
                }

                //Config files button
                final Toolbarbutton filesButton = new Toolbarbutton();
                filesButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_FILES_POPUP));
                filesButton.setImage(DODConstants.IMG_FILES);
                filesButton.setParent(box);
                filesButton.addEventListener(Events.ON_CLICK, new EventListener() {
                    public void onEvent(Event event) {
                        try {
                            FileController fileController = new FileController(instance, username, jobHelper);
                            //Only show window if it is not already being diplayed
                            if (row.getRoot().getFellowIfAny(fileController.getId()) == null) {
                                fileController.setParent(row.getRoot());
                                fileController.doModal();
                            }
                        } catch (InterruptedException ex) {
                            showError(item, ex, DODConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });

                //Only enable button if the instance is stopped or running
                if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
                    filesButton.setDisabled(true);
                    filesButton.setZclass(DODConstants.STYLE_BUTTON_DISABLED);
                } else {
                    filesButton.setZclass(DODConstants.STYLE_BUTTON);
                }

                //Dispatch a backup button
                final Toolbarbutton backupBtn = new Toolbarbutton();
                backupBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_BACKUP_POPUP));
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
                            showError(item, ex, DODConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });

                //Only enable button if the instance is stopped or running
                if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
                    backupBtn.setDisabled(true);
                    backupBtn.setZclass(DODConstants.STYLE_BUTTON_DISABLED);
                } else {
                    backupBtn.setZclass(DODConstants.STYLE_BUTTON);
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
                            showError(item, ex, DODConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });

                //Only enable button if the instance is stopped or running
                if (!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
                    restoreBtn.setDisabled(true);
                    restoreBtn.setZclass(DODConstants.STYLE_BUTTON_DISABLED);
                } else {
                    restoreBtn.setZclass(DODConstants.STYLE_BUTTON);
                }

                //Upgrade button
                final Toolbarbutton upgradeBtn = new Toolbarbutton();
                upgradeBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_JOB + DODConstants.JOB_UPGRADE));
                upgradeBtn.setImage(DODConstants.IMG_UPGRADE);
                upgradeBtn.setParent(box);
                upgradeBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                    public void onEvent(Event event) {
                        //Show upgrade window
                        try {
                            UpgradeController upgradeController = new UpgradeController(instance, username, jobHelper);
                            //Only show window if it is not already being diplayed
                            if (row.getRoot().getFellowIfAny(upgradeController.getId()) == null) {
                                upgradeController.setParent(row.getRoot());
                                upgradeController.doModal();
                            }
                        } catch (InterruptedException ex) {
                            showError(item, ex, DODConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });
                //Only enable button if the instance is stopped or running (and there is an upgrade available)
                if ((!instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED))
                        || instance.getUpgradeTo() == null || instance.getUpgradeTo().isEmpty()) {
                    upgradeBtn.setDisabled(true);
                    upgradeBtn.setZclass(DODConstants.STYLE_BUTTON_DISABLED);
                } else {
                    upgradeBtn.setZclass(DODConstants.STYLE_BUTTON);
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
                            showError(item, ex, DODConstants.ERROR_DISPATCHING_JOB);
                        }
                    }
                });

                //Only disable button if the instance is awaiting approval
                if (instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
                    monitorBtn.setDisabled(true);
                    monitorBtn.setZclass(DODConstants.STYLE_BUTTON_DISABLED);
                } else {
                    monitorBtn.setZclass(DODConstants.STYLE_BUTTON);
                }

                //Add box to row
                Treecell actionsCell = new Treecell();
                actionsCell.appendChild(box);
                row.appendChild(actionsCell);

                //Add row to item
                item.appendChild(row);
                
                //Open item if it's not the closed items list
                item.setOpen(!closed.contains(instance.getDbName()));
            }

            //Render shared instance
            else if (((OverviewTreeNode) data).getData() instanceof String) {
                //Get data
                final String shared = (String)((OverviewTreeNode) data).getData();
                
                //Remove or add item to closed items list if opened
                item.addEventListener(Events.ON_OPEN, new EventListener() {
                    public void onEvent(Event event) {
                        //Remove or add item to closed list
                        if (item.isOpen())
                            closed.remove(shared);
                        else
                            closed.add(shared);
                    }
                });
                
                //Render row
                final Treerow row = new Treerow();
                Treecell cell = new Treecell();
                cell.setSpan(12);
                cell.appendChild(new Label(shared));
                row.appendChild(cell);
                item.appendChild(row);
                
                //Open item if it's not the closed items list
                item.setOpen(!closed.contains(shared));
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
        if (text.length() > maxLength) {
            toret.setTooltiptext(text);
        }
        toret.setStyle("hyphens:none;text-wrap:none;-webkit-hyphens:none;white-space:nowrap;");
        return toret;
    }
    
    /**
     * Filters a node.
     * @param node Node to filter.
     * @param tree Tree containing the node.
     * @return true if the node is filtered, false otherwise.
     */
    private boolean filterNode (OverviewTreeNode node, Tree tree) {        
        //Check if it is  a DOD instance
        if (node.getData() instanceof DODInstance) {
            DODInstance instance = (DODInstance) node.getData();
            if (filterInstance(instance, tree))
                return true;
        }
        //If node has children check if any of the children is filtered
        if (node.getChildren() != null && node.getChildren().size() > 0) {
            for (int i=0; i < node.getChildren().size(); i++) {
                if (filterNode((OverviewTreeNode)node.getChildAt(i), tree))
                    return true;
            }
        }
        
        return false;
    }
    
    /**
     * Filters an instance considering the information contained in the filter fields.
     * @param instance Instance to be filtered
     * @param tree Tree containing the instance
     * @return true if the instance is filtered, false otherwise
     */
    private boolean filterInstance (DODInstance instance, Tree tree) {
        //Get field values
        String dbName = ((Textbox) tree.getFellow("dbNameFilter")).getValue().trim();
        String user = ((Textbox) tree.getFellow("usernameFilter")).getValue().trim();
        String eGroup = ((Textbox) tree.getFellow("eGroupFilter")).getValue().trim();
        String category = "";
        if (((Combobox)tree.getFellow("categoryFilter")).getSelectedItem() != null)
            category = ((String)((Combobox)tree.getFellow("categoryFilter")).getSelectedItem().getValue()).trim();
        String project = ((Textbox) tree.getFellow("projectFilter")).getValue().trim();
        String dbType = "";
        if (((Combobox) tree.getFellow("dbTypeFilter")).getSelectedItem() != null)
            dbType = ((String)((Combobox) tree.getFellow("dbTypeFilter")).getSelectedItem().getValue()).trim();
        String action = "";
        if (((Combobox) tree.getFellow("actionFilter")).getSelectedItem() != null)
            action = ((String)((Combobox) tree.getFellow("actionFilter")).getSelectedItem().getValue()).trim();
        
        if (instance.getDbName().indexOf(dbName.trim()) >= 0 && instance.getUsername().indexOf(user) >= 0
                && (eGroup.isEmpty() || (instance.getEGroup() != null && instance.getEGroup().indexOf(eGroup.trim()) >= 0))
                && (project.isEmpty() || (instance.getProject() != null && instance.getProject().indexOf(project.trim()) >= 0))
                && (category.isEmpty() || instance.getCategory().equals(category))
                && (dbType.isEmpty() || instance.getDbType().equals(dbType))) {
            if (action.isEmpty()) {
                return true;
            }
            else {
                //Check actions (a bit different behaviour)
                if ((action.equals(DODConstants.JOB_STARTUP) && instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED))
                        || (action.equals(DODConstants.JOB_SHUTDOWN) && instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING))
                        || (action.equals(DODConstants.JOB_UPGRADE) && instance.getUpgradeTo() != null && !instance.getUpgradeTo().isEmpty()))
                    return true;
            }
        }
        return false;
        
    }
    
    /**
     * Checks all the instances.
     * @param instances list of instances in the view.
     */
    public void checkAll (List<DODInstance> instances, Tree tree, boolean check) {
        if (check) {
            //Add all elements to checklist
            for (int i=0; i < instances.size(); i++) {
                if (!checked.contains(instances.get(i)) && filterInstance(instances.get(i), tree)) {
                    checked.add(instances.get(i));
                }
            }
        }
        else {
            //Remove al elements to checklist
            for (int i=0; i < instances.size(); i++) {
                if (checked.contains(instances.get(i)) && filterInstance(instances.get(i), tree)) {
                    checked.remove(instances.get(i));
                }
            }
        }
    }
    
    /**
     * Update checked instances.
     * @param instances in the view.
     */
    public void updateCheckedInstances (List<DODInstance> instances) {
        for (int i=0; i < checked.size(); i++) {
            boolean found = false;
            //Search instance
            for (int j=0; j < instances.size(); j++) {
                if (checked.get(i).equals(instances.get(j))) {
                    checked.set(i, instances.get(j));
                    found = true;
                    break;
                }
            }
            //If the instance is not there remove it
            if (!found)
                checked.remove(i);
        }
    }

    /**
     * Getter for checked list.
     * @return List of checked instances.
     */
    public List<DODInstance> getChecked() {
        return checked;
    }

    /**
     * Displays an error window for the error code provided.
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
        } catch (InterruptedException ex) {
            Logger.getLogger(OverviewTreeRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(OverviewTreeRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
