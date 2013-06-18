package ch.cern.dod.ui.renderer;

import ch.cern.dod.db.entity.DODInstanceChange;
import ch.cern.dod.util.DODConstants;
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
     * Renders a row in the instance changes table.
     * @param row row to render.
     * @param object change to render.
     * @throws Exception 
     */
    @Override
    public void render(Row row, Object object, int i) throws Exception {
        DateFormat dateFormatter = new SimpleDateFormat(DODConstants.DATE_FORMAT);
        DateFormat timeFormatter = new SimpleDateFormat(DODConstants.TIME_FORMAT);
        DODInstanceChange change = (DODInstanceChange) object; 
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
