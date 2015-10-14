/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */
 
package ch.cern.dbod.ui.model;

import ch.cern.dbod.db.entity.Instance;
import java.util.ArrayList;
import org.zkoss.zul.DefaultTreeNode;

/**
 * Represents a node in the tree of instances.
 * @author Daniel Gomez Blanco
 */
public class OverviewTreeNode extends DefaultTreeNode implements Comparable{
            
    /**
     * Constructor of this class.
     * @param data Data associated with this node.
     * @param children List of children belonging to this node.
     */
    public OverviewTreeNode(Object data, ArrayList<OverviewTreeNode> children) {
        super(data, children);
    }
 
    /**
     * Constructor without children.
     * @param data Data associated with this node.
     */
    public OverviewTreeNode(Instance data) {
        super(data);
    }
    
    /**
     * Overrides the equals method of the object, having into consideration that the node's data might be a Instance
     * or a String.
     * @param o Object to compare to.
     * @return true if objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof OverviewTreeNode) {
            OverviewTreeNode node = (OverviewTreeNode) o;
            if (this.getData() instanceof String)
                if (node.getData() instanceof String)
                    return this.getData().equals(node.getData());
                else if (node.getData() instanceof Instance)
                    return this.getData().equals(((Instance)node.getData()).getDbName());
                else
                    return false;
            else if (this.getData() instanceof Instance)
                if (node.getData() instanceof String)
                    return ((Instance)this.getData()).getDbName().equals(node.getData());
                else if (node.getData() instanceof Instance)
                    return ((Instance)this.getData()).getDbName().equals(((Instance)node.getData()).getDbName());
                else
                    return false;
            else
                return false;
        }
        else
            return false;
    }

    /**
     * Overrides the compareTo method of the object, having into consideration that the node's data might be a Instance
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
                else if (node.getData() instanceof Instance)
                    return ((String)this.getData()).compareTo(((Instance)node.getData()).getDbName());
                else
                    return 0;
            else if (this.getData() instanceof Instance)
                if (node.getData() instanceof String)
                    return ((Instance)this.getData()).getDbName().compareTo((String)node.getData());
                else if (node.getData() instanceof Instance)
                    return ((Instance)this.getData()).getDbName().compareTo(((Instance)node.getData()).getDbName());
                else
                    return 0;
            else
                return 0;
        }
        else
            return 0;
    }
}
