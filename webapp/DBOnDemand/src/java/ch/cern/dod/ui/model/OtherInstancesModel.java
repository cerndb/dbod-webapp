package ch.cern.dod.ui.model;

import ch.cern.dod.db.entity.DODInstance;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListDataEvent;
import org.zkoss.zul.AbstractListModel;

/**
 * Represents a list of instances.
 * @author Daniel Gomez Blanco
 */
public class OtherInstancesModel extends AbstractListModel {
    /**
     * Instances in the model.
     */
    private List<DODInstance> instances;
    /**
     * Filtered instances in the model.
     */
    private List<DODInstance> filtered;
    /**
     * Current dbName filtered
     */
    private String dbName = "";

    /**
     * Constructor for this class, passing the list of instances as a parameter.
     * @param instances instances to make the model of.
     */
    public OtherInstancesModel(List<DODInstance> instances) {
        this.instances = instances;
        this.filtered = instances;
    }
    
    /**
     * Sets the list of instances.
     * @param instances list of instances.
     */
    public void setInstances(List<DODInstance> instances) {
        this.instances = instances;
        filterInstances(dbName);
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
     * @param index index of the instances.
     * @return the instances.
     */
    public Object getElementAt(int index) {
        return filtered.get(index);
    }
    
    /**
     * Filters the instances.
     * @param dbName DB name to filter.
     */
    public void filterInstances (String dbName) {
        //Store values
        this.dbName = dbName;
        //Filter stats
        filtered = new ArrayList<DODInstance>();
        for (int i=0; i< instances.size(); i++) {
            DODInstance instance = instances.get(i);
            if (instance.getDbName().toLowerCase().indexOf(dbName.trim().toLowerCase()) >= 0) {
                filtered.add(instance);
            }
        }
        //Notify the change
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }
}