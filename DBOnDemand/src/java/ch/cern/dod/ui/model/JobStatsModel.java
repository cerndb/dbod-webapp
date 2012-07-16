package ch.cern.dod.ui.model;

import ch.cern.dod.db.entity.DODJobStat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.ListDataEvent;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModelExt;

/**
 * Represents a list of job stats. It implements sorting to save it from query to query.
 * @author Daniel Gomez Blanco
 */
public class JobStatsModel extends AbstractListModel implements ListModelExt {
    /**
     * Job stats in the model.
     */
    private List<DODJobStat> jobStats;
    /**
     * Indicates if the order is ascending or descending.
     */
    private boolean ascending;
    /**
     * Comparator to sort the stats once they are reloaded.
     */
    private Comparator comparator;

    /**
     * Constructor for this class, passing the list of job stats as a parameter.
     * @param jobStats job stats to make the model of.
     */
    public JobStatsModel(List<DODJobStat> jobStats) {
        this.jobStats = jobStats;
    }

    /**
     * Setter for the stats, passing the list of job stats as a parameter.
     * @param jobStats job stats to make the model of.
     */
    public void setJobStats(List<DODJobStat> jobStats) {
        this.jobStats = jobStats;
    }

    /**
     * Overrides the method to get the size of the model.
     * @return the number of stats in the model.
     */
    public int getSize() {
        return jobStats.size();
    }

    /**
     * Gets the stat at a certain position.
     * @param index index of the stat.
     * @return the stat.
     */
    public Object getElementAt(int index) {
        return jobStats.get(index);
    }

    /**
     * Sort the stats in the model.
     * @param comparator comparator to use.
     * @param ascending indicates if the order is ascending or descending.
     */
    public void sort(Comparator comparator, boolean ascending) {
        this.ascending = ascending;
        this.comparator = comparator;
        Collections.sort(jobStats, comparator);
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }
}