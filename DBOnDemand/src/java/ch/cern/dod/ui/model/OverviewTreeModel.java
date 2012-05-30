package ch.cern.dod.ui.model;

import ch.cern.dod.db.entity.DODInstance;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.zkoss.zul.AbstractTreeModel;

/**
 * Model for overview tree.
 * @author Daniel Gomez Blanco
 */
public class OverviewTreeModel extends AbstractTreeModel{
    
    /**
     * Constructor of this class.
     * @param root Root node to create the model from.
     */
    public OverviewTreeModel (OverviewTreeNode root) {
        super(root);
    }
    
    /**
     * Static method that constructs a tree of overview tree nodes from a list of instances, and returns an object of this model.
     * @param instances List of instances to get the model from.
     * @return Model representing the list of instances passed as parameter.
     */
    public static OverviewTreeModel getInstance (List<DODInstance> instances) {
        ArrayList<OverviewTreeNode> mainList = new ArrayList<OverviewTreeNode>();
        ArrayList<DODInstance> masters = new ArrayList<DODInstance>();
        ArrayList<DODInstance> slaves = new ArrayList<DODInstance>();
        ArrayList<OverviewTreeNode> sharedInstances = new ArrayList<OverviewTreeNode>();
        
        //Separate masters and slaves and create single and shared isntances
        for (int i=0; i < instances.size(); i++) {
            //Masters and slaves are treated later
            DODInstance instance = instances.get(i);
            if (instance.getSlave() != null) {
                masters.add(instance);
            }
            else if (instance.getMaster() != null) {
                slaves.add(instance);
            }
            else {
                //If instance is shared then create node or add instance to existing node
                if (instance.getSharedInstance() != null) {
                    boolean found = false;
                    for (int j=0; j < sharedInstances.size(); j++) {
                        OverviewTreeNode node = sharedInstances.get(j);
                        if (((String)node.getData()).equals(instance.getSharedInstance())) {
                            node.add(new OverviewTreeNode(instance));
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ArrayList<OverviewTreeNode> sharedList =  new ArrayList<OverviewTreeNode>();
                        sharedList.add(new OverviewTreeNode(instance));
                        sharedInstances.add(new OverviewTreeNode(instance.getSharedInstance(), sharedList));
                    }
                }
                //If it's a single instance create leaf node
                else {
                    mainList.add(new OverviewTreeNode(instance));
                }
            }
        }
        //Add all shared instances to list
        mainList.addAll(sharedInstances);
        
        //Merge masters and slaves
        for (int i=0; i < masters.size(); i++) {
            DODInstance master = masters.get(i);
            for (int j=0; j < slaves.size(); j++) {
                DODInstance slave = slaves.get(j);
                ArrayList<OverviewTreeNode> slavesList =  new ArrayList<OverviewTreeNode>();
                if (slave.getMaster().equals(master.getDbName())) {
                    slavesList.add(new OverviewTreeNode(slave));
                    mainList.add(new OverviewTreeNode(master, slavesList));
                }
            }  
        }
        
        //Order list
        Collections.sort(mainList);
        
        //Create root and return object
        OverviewTreeNode root = new OverviewTreeNode (null, mainList);
        return new OverviewTreeModel(root);
    }

    /**
     * Get if a given object is a leaf or not.
     * @param node Node to check.
     * @return true if node is a leaf, false otherwise.
     */
    public boolean isLeaf(Object node) {
        return ((OverviewTreeNode)node).isLeaf();
    }

    /**
     * Gets a specific child of a node.
     * @param node Node to get the child from.
     * @param i Index of the child.
     * @return Node representing the selected child.
     */
    public Object getChild(Object node, int i) {
        if (((OverviewTreeNode)node).getChildren() != null)
            return ((OverviewTreeNode)node).getChildAt(i);
        else
            return null;
    }

    /**
     * Gets the number of children a node has.
     * @param node Node to get the number of children from.
     * @return Number of children the node has.
     */
    public int getChildCount(Object node) {
        if (((OverviewTreeNode)node).getChildren() != null)
            return ((OverviewTreeNode)node).getChildCount();
        else
            return 0;
    }
}
