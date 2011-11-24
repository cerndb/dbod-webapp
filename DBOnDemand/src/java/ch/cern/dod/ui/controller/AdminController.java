package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ui.model.InstanceListModel;
import ch.cern.dod.ui.renderer.OverviewGridRenderer;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the admin overview of instances. It allows the admins to manage every instance.
 * @author Daniel Gomez Blanco
 * @version 22/11/2011
 */
public class AdminController extends Vbox implements BeforeCompose, AfterCompose{
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
        instances = new DODInstanceDAO().selectAll();
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
            Grid grid = (Grid) getFellow("overviewGrid");
            grid.setModel(new InstanceListModel(instances));
            grid.setRowRenderer(new OverviewGridRenderer(checked));
            grid.getPagingChild().setMold("os");
        }
    }

    /**
     * Refreshes the list of instances.
     */
    public void refreshInstances(){
        //Get instances
        instances = new DODInstanceDAO().selectAll();
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
        
        //Set the new instances
        Grid grid = (Grid) getFellow("overviewGrid");
        ((InstanceListModel)grid.getModel()).setInstances(instances);
    }

    /**
     * Getter for the instances size.
     * @return number of instances.
     */
    public int getInstancesSize() {
        return instancesSize;
    }
    
    /**
     * Checks all the instances for a group operation
     */
    public void checkAll() {
        Checkbox checkAll = (Checkbox)getFellow("checkAll");
        
        if (checkAll.isChecked()) {
            //Add al elements to checklist
            for (int i=0; i < instances.size(); i++) {
                if (!checked.contains(instances.get(i))) {
                    checked.add(instances.get(i));
                }
            }
            ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(false);
            ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(false);
        }
        else {
            checked.removeAll(checked);
            ((Toolbarbutton) getFellow("startupAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("shutdownAllBtn")).setDisabled(true);
            ((Toolbarbutton) getFellow("backupAllBtn")).setDisabled(true);
        }
        
        //Re-render the list
        Grid grid = (Grid) getFellow("overviewGrid");
        ((InstanceListModel)grid.getModel()).setInstances(instances);
    }
    
    /**
     * Startup all selected instances. It checks that instances are not awaiting approval.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void startupAll() {
        boolean error = false;
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
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
     * Shutdown all selected instances. It checks that instances are not awaiting approval.
     * It does not matter if the instance is pending a job, it will be executed after it.
     */
    public void shutdownAll() {
        boolean error = false;
        for (int i=0; i<checked.size(); i++) {
            DODInstance instance = checked.get(i);
            if (!instance.getState().equals(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL)) {
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