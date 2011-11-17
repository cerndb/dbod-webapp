package ch.cern.dod.ui.model;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import org.zkoss.zul.AbstractTreeModel;

/**
 * Tree model for a file tree.
 * @author Daniel Gomez Blanco.
 * @version 16/11/2011
 * @deprecated Help is retrieved from CERN's TWiki now
 */
public class FileTreeModel extends AbstractTreeModel {

    /**
     * Constructor for this class. Calls the superclass constructor with the root node.
     * @param helpDir root node
     */
    public FileTreeModel(File helpDir) {
        super(helpDir);
    }

    /**
     * Checks if a node is a leaf or not.
     * @param node node to check.
     * @return true if the node is a directory, false otherwise.
     */
    public boolean isLeaf(Object node) {
        if (node != null) {
            File file = (File) node;
            return !file.isDirectory();
        } else {
            return true;
        }
    }

    /**
     * Obtains the child of a node in the specified index.
     * @param parent parent node.
     * @param index child index.
     * @return Object representing the child node.
     */
    public Object getChild(Object parent, int index) {
        File dir = (File) parent;
        File[] files = dir.listFiles();
        sortFiles(files);
        if (index < files.length) {
            return files[index];
        } else {
            return null;
        }
    }

    /**
     * Obtains the number of children of particular node.
     * @param parent node to get the number of children of.
     * @return number of children of the specified node.
     */
    public int getChildCount(Object parent) {
        File dir = (File) parent;
        if (dir.isDirectory()) {
            return dir.list().length;
        } else {
            return 0;
        }
    }

    private void sortFiles(File[] files) {
        Arrays.sort(files, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((File) o1).compareTo((File) o2);
            }
        });
    }
}
