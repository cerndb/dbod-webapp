package ch.cern.dod.ui.renderer;

import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.util.DODConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Renderer for grid of upgrades
 * @author Daniel Gomez Blanco
 */
public class UpgradesGridRenderer implements RowRenderer {
    /**
     * Upgrade DAO
     */
    DODUpgradeDAO upgradeDAO;

    /**
     * Constructor with upgradeDAO
     * @param upgradeDAO DAO for the upgrades
     */
    public UpgradesGridRenderer(DODUpgradeDAO upgradeDAO) {
        this.upgradeDAO = upgradeDAO;
    }
    
    /**
     * Render a given row with the given upgrade data.
     * @param row row to render
     * @param data DODUpgrade object to render
     */
    public void render(final Row row, Object data) {
        //Cast upgrade object
        final DODUpgrade upgrade = (DODUpgrade) data;
        
        //Info
        row.appendChild(new Label(Labels.getLabel(DODConstants.LABEL_DB_TYPE + upgrade.getDbType())));
        row.appendChild(new Label(Labels.getLabel(DODConstants.LABEL_CATEGORY + upgrade.getCategory())));
        row.appendChild(new Label(upgrade.getVersionFrom()));
        row.appendChild(new Label(upgrade.getVersionTo()));
        
        //Delete button
        Vbox box = new Vbox();
        box.setWidth("100%");
        box.setHeight("24px");
        box.setAlign("center");
        final Toolbarbutton deleteBtn = new Toolbarbutton();
        deleteBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_DELETE_UPGRADE));
        deleteBtn.setImage(DODConstants.IMG_DESTROY);
        deleteBtn.setSclass(DODConstants.STYLE_BUTTON);
        deleteBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                try {
                    if (upgradeDAO.delete(upgrade)) {
                        Executions.sendRedirect(DODConstants.PAGE_ADMIN);
                    }
                    else {
                        showError(row, null, DODConstants.ERROR_DELETING_UPGRADE);
                    }
                }
                catch (Exception ex) {
                    showError(row, ex, DODConstants.ERROR_DELETING_UPGRADE);
                }
            }
        });
        box.appendChild(deleteBtn);
        row.appendChild(box);
    }
    
    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(Row row, Exception exception, String errorCode) {
        if (exception != null) {
            Logger.getLogger(UpgradesGridRenderer.class.getName()).log(Level.SEVERE, "ERROR DELETING UPGRADE", exception);
        }
        Window errorWindow = (Window) row.getGrid().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(OverviewGridRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(OverviewGridRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
