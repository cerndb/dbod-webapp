package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.ui.model.InstanceListModel;
import ch.cern.dod.ui.model.UpgradesListModel;
import ch.cern.dod.ui.renderer.OverviewGridRenderer;
import ch.cern.dod.ui.renderer.UpgradesGridRenderer;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Foot;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the admin overview of instances. It allows the admins to manage every instance.
 * @author Daniel Gomez Blanco
 */
public class AdminController extends Vbox implements BeforeCompose, AfterCompose{
    /**
     * Upgrade DAO
     */
    private DODUpgradeDAO upgradeDAO;
    /**
     * Instance DAO
     */
    private DODInstanceDAO instanceDAO;
    /**
     * List of instances. In this case, all the instances in the database.
     */
    private List<DODInstance> instances;
    /**
     * Number of instances, defining a public method to show the grid or not.
     */
    private int instancesSize;
    /**
     * Instances checked on the grid.
     */
    private ArrayList<DODInstance> checked;
    /**
     * List of upgrades.
     */
    private List<DODUpgrade> upgrades;
    /**
     * Number of upgrades, defining a public method to show the grid or not.
     */
    private int upgradesSize;
    /**
     * Job helper to perform admin actions.
     */
    private JobHelper jobHelper;
    /**
     * User authenticated in the system.
     */
    private String username;

    /**
     * Method executed before the page is composed. Obtains instances from DB.
     */
    public void beforeCompose() {
        Execution execution = Executions.getCurrent();
        username = execution.getHeader(DODConstants.ADFS_LOGIN);
        jobHelper = new JobHelper(true);
        checked = new ArrayList<DODInstance>();
        
        //Select upgrades
        upgradeDAO = new DODUpgradeDAO();
        upgrades = upgradeDAO.selectAll();
        if (upgrades != null)
            upgradesSize = upgrades.size();
        else
            upgradesSize = 0;
        //Select instances
        instanceDAO = new DODInstanceDAO();
        instances = instanceDAO.selectAll(upgrades);
        if (instances != null)
            instancesSize = instances.size();
        else
            instancesSize = 0;
        
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    public void afterCompose() {
        if (instancesSize > 0) {
            Grid overviewGrid = (Grid) getFellow("overviewGrid");
            overviewGrid.setModel(new InstanceListModel(instances));
            overviewGrid.setRowRenderer(new OverviewGridRenderer(checked));
            overviewGrid.getPagingChild().setMold("os");
        }
        if (upgradesSize > 0) {
            Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
            upgradesGrid.setModel(new UpgradesListModel(upgrades));
            upgradesGrid.setRowRenderer(new UpgradesGridRenderer(upgradeDAO));
            upgradesGrid.getPagingChild().setMold("os");
        }
        
        displayOrHideAreas();
    }
    
    /**
     * Displays or hides certain areas when refreshing instances and upgrades
     */
    private void displayOrHideAreas () {
        if (instancesSize > 0) {
            ((Hbox) getFellow("collectiveBtns")).setStyle("display:block");
            ((Grid) getFellow("overviewGrid")).setStyle("display:block");
            ((Div) getFellow("emptyInstancesMsg")).setStyle("display:none");
            if (instancesSize > 10 && ((Grid) getFellow("overviewGrid")).getMold().equals("paging")) {
                ((Foot) getFellow("footer")).setStyle("display:block");
            }
            else {
                ((Foot) getFellow("footer")).setStyle("display:none");
            }
        }
        else {
            ((Hbox) getFellow("collectiveBtns")).setStyle("display:none");
            ((Grid) getFellow("overviewGrid")).setStyle("display:none");
            ((Div) getFellow("emptyInstancesMsg")).setStyle("display:block");
            ((Foot) getFellow("footer")).setStyle("display:none");
        }
        
        if (upgradesSize > 0) {
            ((Grid) getFellow("upgradesGrid")).setStyle("display:block");
            ((Div) getFellow("emptyUpgradesMsg")).setStyle("display:none");
            if (upgradesSize > 10 && ((Grid) getFellow("upgradesGrid")).getMold().equals("paging")) {
                ((Foot) getFellow("footerUpgrades")).setStyle("display:block");
            }
            else {
                ((Foot) getFellow("footerUpgrades")).setStyle("display:none");
            }
        }
        else {
            ((Grid) getFellow("upgradesGrid")).setStyle("display:none");
            ((Div) getFellow("emptyUpgradesMsg")).setStyle("display:block");
            ((Foot) getFellow("footerUpgrades")).setStyle("display:none");
        }
    }

    /**
     * Refreshes the list of instances.
     */
    public void refreshInstances(){
        //Get upgrades
        upgrades = upgradeDAO.selectAll();
        if (upgrades != null)
            upgradesSize = upgrades.size();
        else
            upgradesSize = 0;
        
        //Get instances
        instances = instanceDAO.selectAll(upgrades);
        if (instances != null)
            instancesSize = instances.size();
        else
            instancesSize = 0;
        
        //Refresh checked list
        for (int i=0; i < checked.size(); i++) {
            for (int j=0; j < instances.size(); j++) {
                if (checked.get(i).equals(instances.get(j)))
                    checked.set(i, instances.get(j));
            }
        }
        
        if (upgradesSize > 0) {
            //Set the new upgrades
            Grid upgradesGrid = (Grid) getFellow("upgradesGrid");
            ((UpgradesListModel)upgradesGrid.getModel()).setUpgrades(upgrades);
        }
        
        //Set the new instances
        if (instancesSize > 0) {
            Grid overviewGrid = (Grid) getFellow("overviewGrid");
            ((InstanceListModel)overviewGrid.getModel()).setInstances(instances);
        }
        
        displayOrHideAreas();
    }

    /**
     * Getter for the instances size.
     * @return number of instances.
     */
    public int getInstancesSize() {
        return instancesSize;
    }
    
    /**
     * Getter for the upgrades size.
     * @return number of upgrades.
     */
    public int getUpgradesSize() {
        return upgradesSize;
    }
    
    /**
     * Checks all the instances for a group operation
     */
    public void checkAll() {
        Checkbox checkAll = (Checkbox)getFellow("checkAll");
        Grid grid = (Grid) getFellow("overviewGrid");
        List<DODInstance> filtered = ((InstanceListModel)grid.getModel()).getFiltered();
        
        if (checkAll.isChecked()) {
            //Add all elements to checklist
            for (int i=0; i < instances.size(); i++) {
                if (!checked.contains(instances.get(i)) && filtered.contains(instances.get(i))) {
                    checked.add(instances.get(i));
                }
            }
            if (checked.size() > 0) {
                ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(false);
                ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(false);
                ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(false);
                ((Toolbarbutton) getFellow("upgradeAllBtn")).setDisabled(false);
                ((Toolbarbutton) getFellow("startupAllBtn")).setZclass("button");
                ((Toolbarbutton) getFellow("shutdownAllBtn")).setZclass("button");
                ((Toolbarbutton) getFellow("backupAllBtn")).setZclass("button");
                ((Toolbarbutton) getFellow("upgradeAllBtn")).setZclass("button");
            }
        }
        else {
            //Remove al elements to checklist
            for (int i=0; i < instances.size(); i++) {
                if (checked.contains(instances.get(i)) && filtered.contains(instances.get(i))) {
                    checked.remove(instances.get(i));
                }
            }
            ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("startupAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("backupAllBtn")).setZclass("buttonDisabled");
            ((Toolbarbutton) getFellow("upgradeAllBtn")).setZclass("buttonDisabled");
        }
        
        //Re-render the list
        ((InstanceListModel)grid.getModel()).setInstances(instances);
    }
    
    /**
     * Startup all selected instances. It checks that instances are not awaiting approval or running.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void startupAll() {
        boolean error = false;
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && !instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING)) {
                if (!jobHelper.doStartup(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the list
        Grid grid = (Grid) getFellow("overviewGrid");
        ((InstanceListModel)grid.getModel()).setInstances(instances);
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Shutdown all selected instances. It checks that instances are not awaiting approval or stopped.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void shutdownAll() {
        boolean error = false;
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && !instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED)) {
                if (!jobHelper.doShutdown(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the list
        Grid grid = (Grid) getFellow("overviewGrid");
        ((InstanceListModel)grid.getModel()).setInstances(instances);
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Backup all selected instances. It checks that instances are not awaiting approval.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void backupAll(){
        boolean error = false;
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
                if (!jobHelper.doBackup(instance, username, 0)){
                    error = true;
                }
            }
        }
        //Re-render the list
        Grid grid = (Grid) getFellow("overviewGrid");
        ((InstanceListModel)grid.getModel()).setInstances(instances);
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Upgrade all selected instances. It checks that instances are not awaiting approval and have a pending upgrade.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void upgradeAll(){
        boolean error = false;
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL) && instance.getVersion() != null && !instance.getVersion().isEmpty()) {
                if (!jobHelper.doUpgrade(instance, username)){
                    error = true;
                }
            }
        }
        //Re-render the list
        Grid grid = (Grid) getFellow("overviewGrid");
        ((InstanceListModel)grid.getModel()).setInstances(instances);
        //Show error if any
        if (error)
            showError(DODConstants.ERROR_COLLECTIVE_ACTION);
    }
    
    /**
     * Opens the add upgrade window.
     */
    public void openAddUpgrade(){
        try {
            AddUpgradeController upgradeController = new AddUpgradeController();
            //Only show window if it is not already being diplayed
            if (getFellowIfAny(upgradeController.getId()) == null) {
                upgradeController.setParent(this);
                upgradeController.doModal();
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, "ERROR OPENING ADD UPGRADE WINDOW", ex);
            showError(DODConstants.ERROR_OPENING_ADD_UPGRADE);
        }
    }
    
    /**
     * Displays all instances in the view
     */
    public void showAll() {
        Grid grid = (Grid) getFellow("overviewGrid");
        grid.setMold("default");
        Foot footer = (Foot) getFellow("footer");
        footer.setStyle("display:none");
    }
    
    /**
     * Displays all instances in the view
     */
    public void showAllUpgrades() {
        Grid grid = (Grid) getFellow("upgradesGrid");
        grid.setMold("default");
        Foot footer = (Foot) getFellow("footerUpgrades");
        footer.setStyle("display:none");
    }
    
    /**
     * Filters instances according to the content on the filter fields.
     */
    public void filterInstances () {
        Grid grid = (Grid) getFellow("overviewGrid");
        ((InstanceListModel)grid.getModel()).filter(((Textbox)getFellow("dbNameFilter")).getValue(),
                                                    ((Textbox)getFellow("usernameFilter")).getValue(),
                                                    ((Textbox)getFellow("eGroupFilter")).getValue(),
                                                    (String)((Combobox)getFellow("categoryFilter")).getSelectedItem().getValue(),
                                                    ((Textbox)getFellow("projectFilter")).getValue(),
                                                    (String)((Combobox)getFellow("dbTypeFilter")).getSelectedItem().getValue(),
                                                    (String)((Combobox)getFellow("actionFilter")).getSelectedItem().getValue());
    }
    
    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(String errorCode) {
        Window errorWindow = (Window) getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(AdminController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}