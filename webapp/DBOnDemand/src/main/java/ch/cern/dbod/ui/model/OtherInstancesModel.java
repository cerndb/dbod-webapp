package ch.cern.dbod.ui.model;

import ch.cern.dbod.db.entity.Instance;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.ListDataEvent;
import org.zkoss.zul.AbstractListModel;

/**
 * Represents a list of instances. This model is used in the instance view,
 * where there is a list of instances for quick access.
 * @author Daniel Gomez Blanco
 */
public class OtherInstancesModel extends AbstractListModel {
    /**
     * Instances in the model.
     */
    private List<Instance> instances;
    /**
     * Filtered instances in the model.
     */
    private List<Instance> filtered;
    /**
     * Current dbName filtered
     */
    private String dbName = "";

    /**
     * Constructor for this class, passing the list of instances as a parameter.
     * @param instances instances to make the model of.
     */
    public OtherInstancesModel(List<Instance> instances) {
        this.instances = instances;
        this.filtered = instances;
    }
    
    /**
     * Sets the list of instances.
     * @param instances list of instances.
     */
    public void setInstances(List<Instance> instances) {
        this.instances = instances;
        filterInstances(dbName);
    }

    /**
     * Overrides the method to get the size of the model.
     * @return the number of instances in the model.
     */
    @Override
    public int getSize() {
        return filtered.size();
    }

    /**
     * Gets the instance at a certain position.
     * @param index index of the instances.
     * @return the instances.
     */
    @Override
    public Object getElementAt(int index) {
        return filtered.get(index);
    }
    
    /**
     * Filters the instances according to a DB name.
     * @param dbName DB name to filter.
     */
    public void filterInstances (String dbName) {
        //Store values
        this.dbName = dbName;
        //Filter stats
        filtered = new ArrayList<>();
        for (int i=0; i< instances.size(); i++) {
            Instance instance = instances.get(i);
            if (instance.getDbName().toLowerCase().indexOf(dbName.trim().toLowerCase()) >= 0) {
                filtered.add(instance);
            }
        }
        //Notify the change
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }
}