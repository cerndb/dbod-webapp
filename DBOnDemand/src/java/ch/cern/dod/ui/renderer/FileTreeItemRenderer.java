package ch.cern.dod.ui.renderer;

import ch.cern.dod.util.DODConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;
import org.zkoss.zul.Window;

/**
 * Implements a renderer for tree items.
 * @author Daniel Gomez Blanco
 * @version 22/11/2011
 * @deprecated Help is retrieved from CERN's TWiki now
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
            final String label = file.getName().substring(file.getName().indexOf("-")+1).replaceAll("_", " ").replaceAll(".zul", "");
            item.setLabel(label);
            item.setId(file.getAbsolutePath());
            item.addEventListener(Events.ON_CLICK, new EventListener() {
                public void onEvent(Event event) {
                    try {
                        //If it is a file and not a directory
                        if (file != null && !file.isDirectory()) {
                            item.setSelected(true);
                            ((Label) item.getFellow("title")).setValue(label);
                            Div content = (Div) item.getFellow("content");
                            //If there is something loaded detach it
                            if (content.getFellowIfAny("helpContainer") != null)
                                content.getFellow("helpContainer").detach();
                            FileReader reader = new FileReader(file);
                            Div container = new Div();
                            container.setId("helpContainer");
                            content.appendChild(container);
                            try {
                                Component helpPage = Executions.createComponentsDirectly(reader, null, container, null);
                            } catch (IOException ex) {
                                Logger.getLogger(FileTreeItemRenderer.class.getName()).log(Level.SEVERE, "ERROR OBTAINING HELP FILE", ex);
                            }
                            
                        }
                        //If it is a directory open it or close it
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
     * @param item tree item on which the error occured.
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
