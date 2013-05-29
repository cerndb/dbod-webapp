package ch.cern.dod.ui.model;

import ch.cern.dod.db.entity.DODCommandStat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.ListDataEvent;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ext.Sortable;

/**
 * Represents a list of command stats. It implements sorting to save it from query to query.
 * @author Daniel Gomez Blanco
 */
public class CommandStatsModel extends AbstractListModel implements Sortable{
    /**
     * Command stats in the model.
     */
    private List<DODCommandStat> commandStats;
    /**
     * Indicates if the order is ascending or descending.
     */
    private boolean ascending;
    /**
     * Comparator to sort the stats once they are reloaded.
     */
    private Comparator comparator;

    /**
     * Constructor for this class, passing the list of command stats as a parameter.
     * @param commandStats command stats to make the model of.
     */
    public CommandStatsModel(List<DODCommandStat> commandStats) {
        this.commandStats = commandStats;
    }
    
    public void setCommandStats(List<DODCommandStat> commandStats) {
        this.commandStats = commandStats;
        sort(comparator, ascending);
    }

    /**
     * Overrides the method to get the size of the model.
     * @return the number of stats in the model.
     */
    public int getSize() {
        return commandStats.size();
    }

    /**
     * Gets the stat at a certain position.
     * @param index index of the stat.
     * @return the stat.
     */
    public Object getElementAt(int index) {
        return commandStats.get(index);
    }

    /**
     * Sort the stats in the model.
     * @param comparator comparator to use.
     * @param ascending indicates if the order is ascending or descending.
     */
    public void sort(Comparator comparator, boolean ascending) {
        this.ascending = ascending;
        this.comparator = comparator;
        Collections.sort(commandStats, comparator);
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }

    public String getSortDirection(Comparator cmpr) {
        if (ascending) {
            return "ascending";
        }
        else {
            return "descending";
        }
    }
}
