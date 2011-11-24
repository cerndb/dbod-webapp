package ch.cern.dod.ui.controller;

import ch.cern.dod.ui.model.FileTreeModel;
import ch.cern.dod.ui.renderer.FileTreeItemRenderer;
import ch.cern.dod.util.DODConstants;
import java.io.File;
import java.util.Iterator;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treeitem;

/**
 * Controller for help page.
 * @author Daniel Gomez Blanco
 * @version 22/11/2011
 * @deprecated Help is retrieved from CERN's TWiki now
 */
public class HelpController extends Hbox implements AfterCompose{

    /**
     * Method called after the components are evaluated. It sets the model for the help tree.
     */
    public void afterCompose() {
        File root = new File(DODConstants.HELP_DIR);
        FileTreeModel model = new FileTreeModel(root);
        Tree tree = (Tree) getFellow("helpTree");
        tree.setItemRenderer(new FileTreeItemRenderer());
        tree.setModel(model);
        if (tree.getItemCount() > 0) {
            Iterator<Treeitem> iter = tree.getItems().iterator();
            while (iter.hasNext()){
                if (expandItem(iter.next()))
                    break;
            }
        }
    }

    /**
     * Recursive function to expand all items until it finds a leaf node.
     * @param item root item to be expanded.
     * @return true if the item is a leaf node, false otherwise.
     */
    private boolean expandItem (Treeitem item) {
        item.setOpen(true);
        if (item.isContainer()) {
            if (item.getTreechildren() != null && item.getTreechildren().getItemCount() > 0) {
                Iterator<Treeitem> iter = item.getTreechildren().getItems().iterator();
                while (iter.hasNext()){
                    if (expandItem(iter.next())){
                        return true;
                    }
                }
                return false;
            }
            else {
                return false;
            }
        }
        else {
            Events.postEvent(Events.ON_CLICK, item, null);
            return true;
        }

    }
}
