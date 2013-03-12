/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.dod.ui.model;

import ch.cern.dod.db.entity.DODInstance;
import java.util.ArrayList;
import org.zkoss.zul.DefaultTreeNode;

/**
 * Represents a node in the tree of instances.
 * @author Daniel Gomez Blanco
 */
public class OverviewTreeNode extends DefaultTreeNode implements Comparable{
            
    /**
     * Constructor of this class.
     * @param data Data associated with this node, in this case a DODInstance or a String in case of shared instances.
     * @param children List of children belonging to this node.
     */
    public OverviewTreeNode(Object data, ArrayList<OverviewTreeNode> children) {
        super(data, children);
    }
 
    /**
     * Constructor without children.
     * @param data Data associated with this node, in this case a DODInstance or a String in case of shared instances.
     */
    public OverviewTreeNode(DODInstance data) {
        super(data);
    }

    /**
     * Overrides the equals method of the object, having into consideration that the node's data might be a DODInstance
     * or a String.
     * @param o Object to compare to.
     * @return true if objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o != null &&  o instanceof OverviewTreeNode) {
            OverviewTreeNode node = (OverviewTreeNode) o;
            if (this.getData() instanceof String)
                if (node.getData() instanceof String)
                    return this.getData().equals(node.getData());
                else if (node.getData() instanceof DODInstance)
                    return this.getData().equals(((DODInstance)node.getData()).getDbName());
                else
                    return false;
            else if (this.getData() instanceof DODInstance)
                if (node.getData() instanceof String)
                    return ((DODInstance)node.getData()).getDbName().equals(node.getData());
                else if (node.getData() instanceof DODInstance)
                    return ((DODInstance)node.getData()).getDbName().equals(((DODInstance)node.getData()).getDbName());
                else
                    return false;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Overrides the compareTo method of the object, having into consideration that the node's data might be a DODInstance
     * or a String.
     * @param o Object to compare to.
     * @return -1 if this is less than the object, 0 if they are equal, 1 if this is greater than the object.
     */
    @Override
    public int compareTo(Object o) {
        if (o != null &&  o instanceof OverviewTreeNode) {
            OverviewTreeNode node = (OverviewTreeNode) o;
            if (this.getData() instanceof String)
                if (node.getData() instanceof String)
                    return ((String)this.getData()).compareTo((String)node.getData());
                else if (node.getData() instanceof DODInstance)
                    return ((String)this.getData()).compareTo(((DODInstance)node.getData()).getDbName());
                else
                    return 0;
            else if (this.getData() instanceof DODInstance)
                if (node.getData() instanceof String)
                    return ((DODInstance)this.getData()).getDbName().compareTo((String)node.getData());
                else if (node.getData() instanceof DODInstance)
                    return ((DODInstance)this.getData()).getDbName().compareTo(((DODInstance)node.getData()).getDbName());
                else
                    return 0;
            else
                return 0;
        }
        else
            return 0;
    }
}
