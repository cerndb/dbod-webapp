package ch.cern.dbod.ui.renderer;

import ch.cern.dbod.db.entity.InstanceChange;
import ch.cern.dbod.util.CommonConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * Renderer for instance changes
 * @author Daniel Gomez Blanco
 */
public class InstanceChangesRenderer implements RowRenderer{

    /**
     * Renders a row
     * @param row object where to place information
     * @param object object to be rendered
     * @param i index of the row
     * @throws Exception in case components cannot be added to the row
     */
    @Override
    public void render(Row row, Object object, int i) throws Exception {
        DateFormat dateFormatter = new SimpleDateFormat(CommonConstants.DATE_FORMAT);
        DateFormat timeFormatter = new SimpleDateFormat(CommonConstants.TIME_FORMAT);
        InstanceChange change = (InstanceChange) object; 
        // the data append to each row with simple label
        row.appendChild(new Label(dateFormatter.format(change.getChangeDate())
                            + " "  + timeFormatter.format(change.getChangeDate())));
        row.appendChild(new Label(change.getAttribute()));
        row.appendChild(new Label(change.getRequester()));
        if (change.getOldValue() != null)
            row.appendChild(getFormattedLabel(change.getOldValue(), 50));
        else
            row.appendChild(new Label("-"));
        if (change.getNewValue() != null)
            row.appendChild(getFormattedLabel(change.getNewValue(), 50));
        else
            row.appendChild(new Label("-"));
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
}
