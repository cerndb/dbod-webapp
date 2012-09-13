package ch.cern.dod.ui.renderer;

import ch.cern.dod.db.entity.DODCommandStat;
import ch.cern.dod.util.DateTimeHelper;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * Renderer for job stats
 * @author Daniel Gomez Blanco
 */
public class CommandStatsRenderer implements RowRenderer{

    public void render(Row row, Object object) throws Exception {
        DODCommandStat stat = (DODCommandStat) object; 
        // the data append to each row with simple label
        row.appendChild(new Label(stat.getCommandName()));
        row.appendChild(new Label(String.valueOf(stat.getCount())));
        row.appendChild(new Label(DateTimeHelper.timeToString(stat.getMeanDuration())));
    }
    
}
