package ch.cern.dod.ui.renderer;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ui.controller.DestroyController;
import ch.cern.dod.ui.controller.RescueController;
import ch.cern.dod.util.DODConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * Renderer for instances to be destroyed
 * @author Daniel Gomez Blanco
 */
public class DestroyGridRenderer implements RowRenderer {
    /**
     * Instance DAO
     */
    DODInstanceDAO instanceDAO;

    /**
     * Constructor with instance DAO
     * @param instanceDAO DAO for instances
     */
    public DestroyGridRenderer(DODInstanceDAO instanceDAO) {
        this.instanceDAO = instanceDAO;
    }
    
    /**
     * Renders a row
     * @param row object where to place information
     * @param object object to be rendered
     * @param i index of the row
     * @throws Exception in case components cannot be added to the row
     */
    @Override
    public void render(final Row row, Object data, int i) {
        //Cast upgrade object
        final DODInstance instance = (DODInstance) data;
        row.setStyle("padding-top: 0px; padding-bottom: 0px");
        
        //Info
        row.appendChild(new Label(instance.getDbName()));
        
        Hbox box = new Hbox();
        box.setWidth("100%");
        box.setHeight("20px");
        box.setAlign("center");
        
        //Rescue button
        final Toolbarbutton rescueBtn = new Toolbarbutton();
        rescueBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_RESCUE_TITLE));
        rescueBtn.setImage(DODConstants.IMG_RESCUE);
        rescueBtn.setZclass(DODConstants.STYLE_BUTTON);
        rescueBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                try {
                    RescueController rescueController = new RescueController(instance, instanceDAO);
                    //Only show window if it is not already being diplayed
                    if (row.getRoot().getFellowIfAny(rescueController.getId()) == null) {
                        rescueController.setParent(row.getRoot());
                        rescueController.doModal();
                    }
                }
                catch (InterruptedException ex) {
                    showError(row, ex, DODConstants.ERROR_RESCUING_INSTANCE);
                }
            }
        });
        box.appendChild(rescueBtn);
        
        //Delete button
        final Toolbarbutton destroyBtn = new Toolbarbutton();
        destroyBtn.setTooltiptext(Labels.getLabel(DODConstants.LABEL_DESTROY_TITLE));
        destroyBtn.setImage(DODConstants.IMG_DESTROY);
        destroyBtn.setZclass(DODConstants.STYLE_BUTTON);
        destroyBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                try {
                    DestroyController destroyController = new DestroyController(instance, instanceDAO);
                    //Only show window if it is not already being diplayed
                    if (row.getRoot().getFellowIfAny(destroyController.getId()) == null) {
                        destroyController.setParent(row.getRoot());
                        destroyController.doModal();
                    }
                }
                catch (InterruptedException ex) {
                    showError(row, ex, DODConstants.ERROR_DESTROYING_INSTANCE);
                }
            }
        });
        box.appendChild(destroyBtn);
        
        row.appendChild(box);
    }
    
    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     * @param exception exception to be logged
     * @param row row where the exception occured
     */
    private void showError(Row row, Exception exception, String errorCode) {
        if (exception != null) {
            Logger.getLogger(DestroyGridRenderer.class.getName()).log(Level.SEVERE, "ERROR DESTROYING OR RESURRECTING INSTANCE", exception);
        }
        Window errorWindow = (Window) row.getGrid().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(DestroyGridRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}

