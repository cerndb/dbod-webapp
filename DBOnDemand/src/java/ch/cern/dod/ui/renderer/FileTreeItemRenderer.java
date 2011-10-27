package ch.cern.dod.ui.renderer;

import ch.cern.dod.util.DODConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

/**
 * Implements a renderer for tree items.
 * @author Daniel Gomez Blanco
 * @version 26/09/2011
 */
public class FileTreeItemRenderer implements TreeitemRenderer{

    /**
     * Renders a tree item with the title of the file
     * @param item item to render
     * @param data file to render
     */
    public void render(final Treeitem item, Object data) {
        if (data != null) {
            final File file = (File) data;
            final String label = file.getName().substring(file.getName().indexOf("-")+1).replaceAll("_", " ").replaceAll(".html", "");
            item.setLabel(label);
            item.addEventListener(Events.ON_CLICK, new EventListener() {
                public void onEvent(Event event) {
                    try {
                        if (file != null && !file.isDirectory()) {
                            ((Label) item.getFellow("title")).setValue(label);
                            Media media = new AMedia(file, "text/html", "UTF-8");
                            Html content = (Html) item.getFellow("content");
                            content.setContent(media.getStringData());
                            item.setSelected(true);
                        }
                        else {
                            item.setSelected(false);
                            if (item.isOpen())
                                item.setOpen(false);
                            else
                                item.setOpen(true);
                        }
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(FileTreeItemRenderer.class.getName()).log(Level.SEVERE, "EROR DISPLAYING HELP", ex);
                        showError(item, DODConstants.ERROR_DISPLAYING_HELP);
                    }
                }
            });
        }
    }
    
    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(Treeitem item, String errorCode) {
        Window errorWindow = (Window) item.getParent().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(FileTreeItemRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(FileTreeItemRenderer.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
