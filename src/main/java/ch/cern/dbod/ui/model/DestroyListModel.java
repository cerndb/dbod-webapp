package ch.cern.dbod.ui.model;

import ch.cern.dbod.db.entity.Instance;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.ListDataEvent;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ext.Sortable;

/**
 * Represents a list of instances to be destroyed. It implements sorting to save it from query to query.
 * @author Daniel Gomez Blanco
 */
public class DestroyListModel extends AbstractListModel implements Sortable {
    /**
     * Instances in the model.
     */
    private List<Instance> instances;
    /**
     * Indicates if the order is ascending or descending.
     */
    private boolean ascending;
    /**
     * Comparator to sort the instances once they are reloaded.
     */
    private Comparator comparator;

    /**
     * Constructor for this class, passing the list of instances as a parameter.
     * @param instances instances to make the model of.
     */
    public DestroyListModel(List<Instance> instances) {
        this.instances = instances;
    }

    /**
     * Setter for the upgrades, passing the list of instances as a parameter.
     * @param instances instances to make the model of.
     */
    public void setInstances(List<Instance> instances) {
        this.instances = instances;
        sort(comparator, ascending);
    }

    /**
     * Overrides the method to get the size of the model.
     * @return the number of instances in the model.
     */
    @Override
    public int getSize() {
        return instances.size();
    }

    /**
     * Gets the instance at a certain position.
     * @param index index of the upgrade.
     * @return the instance.
     */
    @Override
    public Object getElementAt(int index) {
        return instances.get(index);
    }

    /**
     * Sort the instances in the model.
     * @param comparator comparator to use.
     * @param ascending indicates if the order is ascending or descending.
     */
    @Override
    public void sort(Comparator comparator, boolean ascending) {
        this.ascending = ascending;
        this.comparator = comparator;
        Collections.sort(instances, comparator);
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }
    
    /**
     * Obtains the sort direction.
     * @param cmprtr comparator being used
     * @return ascending or descending
     */
    @Override
    public String getSortDirection(Comparator cmprtr) {
        if (ascending)
            return "ascending";
        else
            return "descending";
    }
}
