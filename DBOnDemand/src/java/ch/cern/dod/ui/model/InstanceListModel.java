package ch.cern.dod.ui.model;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.util.DODConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.ListDataEvent;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModelExt;

/**
 * Represents a list of instances. It implements sorting to save it from query to query.
 * @author Daniel Gomez Blanco
 * @version 04/10/2011
 */
public class InstanceListModel extends AbstractListModel implements ListModelExt {
    /**
     * Instances in the model.
     */
    private List<DODInstance> instances;
    /**
     * The instances in the model after a filter has been applied.
     */
    private List<DODInstance> filtered;
    /**
     * Indicates if the order is ascending or descending.
     */
    private boolean ascending;
    /**
     * Comparator to sort the instances once they are reloaded.
     */
    private Comparator comparator;
    private String dbName = "";
    private String username = "";
    private String eGroup = "";
    private String category = "";
    private String project = "";
    private String dbType = "";
    private String action = "";

    /**
     * Constructor for this class, passing the list of instances as a parameter.
     * @param instances instances to make the model of.
     */
    public InstanceListModel(List<DODInstance> instances) {
        this.instances = instances;
        this.filtered = instances;
    }

    /**
     * Setter for the instances, passing the list of instances as a parameter.
     * @param instances instances to make the model of.
     */
    public void setInstances(List<DODInstance> instances) {
        this.instances = instances;
        filter(dbName, username, eGroup, category, project, dbType, action);
    }
    
    /**
     * Getter for the filtered instances
     * @return 
     */
    public List<DODInstance> getFiltered() {
        return filtered;
    }

    /**
     * Overrides the method to get the size of the model.
     * @return the number of instances in the model.
     */
    public int getSize() {
        return filtered.size();
    }

    /**
     * Gets the instance at a certain position.
     * @param index index of the instance.
     * @return the instances.
     */
    public Object getElementAt(int index) {
        return filtered.get(index);
    }

    /**
     * Sort the instances in the model.
     * @param comparator comparator to use.
     * @param ascending indicates if the order is ascending or descending.
     */
    public void sort(Comparator comparator, boolean ascending) {
        this.ascending = ascending;
        this.comparator = comparator;
        Collections.sort(instances, comparator);
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }
    
    public void filter (String dbName, String username, String eGroup, String category, String project, String dbType, String action) {
        this.dbName = dbName;
        this.username = username;
        this.eGroup = eGroup;
        this.category = category;
        this.project = project;
        this.dbType = dbType;
        this.action = action;
        
        if (dbName.isEmpty() && username.isEmpty() && eGroup.isEmpty() && category.isEmpty() && project.isEmpty() && dbType.isEmpty() && action.isEmpty()) {
            filtered = instances;
        }
        else {
            filtered = new ArrayList<DODInstance>();
            
            for (int i=0; i < instances.size(); i++) {
                DODInstance instance = instances.get(i);
                if (instance.getDbName().indexOf(dbName.trim()) >= 0 && instance.getUsername().indexOf(username) >= 0
                        && (eGroup.isEmpty() || (instance.getEGroup() != null && instance.getEGroup().indexOf(eGroup.trim()) >= 0))
                        && (project.isEmpty() || (instance.getProject() != null && instance.getProject().indexOf(project.trim()) >= 0))
                        && (category.isEmpty() || instance.getCategory().equals(category))
                        && (dbType.isEmpty() || instance.getDbType().equals(dbType))) {
                    if (action.isEmpty())
                        filtered.add(instance);
                    else {
                        //Check actions (a bit different behaviour)
                        if ((action.equals(DODConstants.JOB_STARTUP) && instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED))
                                || (action.equals(DODConstants.JOB_SHUTDOWN) && instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING))
                                || (action.equals(DODConstants.JOB_UPGRADE) && instance.getUpgradeTo() != null && !instance.getUpgradeTo().isEmpty()))
                            filtered.add(instance);
                    }
                }
            }
        }
        sort (comparator, ascending);
    }
}
