package ch.cern.dod.ui.renderer;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.util.DODConstants;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Html;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * Renderer for the list of instances in the instance view
 * @author Daniel Gomez Blanco
 */
public class OtherInstancesRenderer implements RowRenderer{

    /**
     * Renders a row in the instances table.
     * @param row row to render.
     * @param object instance to render.
     * @throws Exception 
     */
    public void render(Row row, Object object, int i) throws Exception {
        DODInstance instance = (DODInstance) object;
        row.setStyle("padding-top: 0px; padding-bottom: 0px");
        row.setHeight("24px");
        // the data append to each row with simple label
        Html dbName = new Html();
        dbName.setContent("<a style=\"text-decoration:underline;color:blue\" class=\"z-label\" href=\""
                            + Executions.encodeURL(DODConstants.PAGE_INSTANCE + "?" + DODConstants.INSTANCE + "=" + instance.getDbName()) 
                            +"\">" + instance.getDbName() + "</a>");
        row.appendChild(dbName);
    }
}
