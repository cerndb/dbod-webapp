package ch.cern.dod.ui.model;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.util.DODConstants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.zkoss.zul.AbstractTreeModel;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Tree;

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
    public static OverviewTreeModel getInstance (List<DODInstance> instances, Tree tree) {
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
                //Only add instance to tree if it is filtered
                if (filterInstance(instance, tree)) {
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
        }
        //Add all shared instances to list
        mainList.addAll(sharedInstances);
        
        //Merge masters and slaves
        for (int i=0; i < masters.size(); i++) {
            DODInstance master = masters.get(i);
            ArrayList<OverviewTreeNode> slavesList =  new ArrayList<OverviewTreeNode>();
            for (int j=0; j < slaves.size(); j++) {
                DODInstance slave = slaves.get(j);
                if (slave.getMaster().equals(master.getDbName()) && filterInstance(slave, tree)) {
                    slavesList.add(new OverviewTreeNode(slave));
                }
            }
            //If there are filtered slaves
            if (slavesList.size() > 0)
                mainList.add(new OverviewTreeNode(master, slavesList));
            else {
                if (filterInstance(master, tree))
                    mainList.add(new OverviewTreeNode(master));
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
    
    /**
     * Filters an instance considering the information contained in the filter fields.
     * @param instance Instance to be filtered
     * @param tree Tree containing the instance
     * @return true if the instance is filtered, false otherwise
     */
    private static boolean filterInstance (DODInstance instance, Tree tree) {
        //Get field values
        String dbName = ((Textbox) tree.getFellow("dbNameFilter")).getValue().trim();
        String user = ((Textbox) tree.getFellow("usernameFilter")).getValue().trim();
        String eGroup = ((Textbox) tree.getFellow("eGroupFilter")).getValue().trim();
        String category = "";
        if (((Combobox)tree.getFellow("categoryFilter")).getSelectedItem() != null)
            category = ((String)((Combobox)tree.getFellow("categoryFilter")).getSelectedItem().getValue()).trim();
        String project = ((Textbox) tree.getFellow("projectFilter")).getValue().trim();
        String dbType = "";
        if (((Combobox) tree.getFellow("dbTypeFilter")).getSelectedItem() != null)
            dbType = ((String)((Combobox) tree.getFellow("dbTypeFilter")).getSelectedItem().getValue()).trim();
        String action = "";
        if (((Combobox) tree.getFellow("actionFilter")).getSelectedItem() != null)
            action = ((String)((Combobox) tree.getFellow("actionFilter")).getSelectedItem().getValue()).trim();
        
        if (instance.getDbName().indexOf(dbName.trim()) >= 0 && instance.getUsername().indexOf(user) >= 0
                && (eGroup.isEmpty() || (instance.getEGroup() != null && instance.getEGroup().indexOf(eGroup.trim()) >= 0))
                && (project.isEmpty() || (instance.getProject() != null && instance.getProject().indexOf(project.trim()) >= 0))
                && (category.isEmpty() || instance.getCategory().equals(category))
                && (dbType.isEmpty() || instance.getDbType().equals(dbType))) {
            if (action.isEmpty()) {
                return true;
            }
            else {
                //Check actions (a bit different behaviour)
                if ((action.equals(DODConstants.JOB_STARTUP) && instance.getState().equals(DODConstants.INSTANCE_STATE_STOPPED))
                        || (action.equals(DODConstants.JOB_SHUTDOWN) && instance.getState().equals(DODConstants.INSTANCE_STATE_RUNNING))
                        || (action.equals(DODConstants.JOB_UPGRADE) && instance.getUpgradeTo() != null && !instance.getUpgradeTo().isEmpty()))
                    return true;
            }
        }
        return false;
        
    }
    
    /**
     * Checks (or unchecks) all the instances in the model.
     * @param root root node to start checking.
     * @param checked true or false, depending if the action is checking or unchecking.
     */
    public void checkAll (OverviewTreeNode root, boolean checked) {
        if (root.getData() instanceof DODInstance)
            ((DODInstance)root.getData()).setChecked(checked);
        for (int i=0; i < root.getChildCount(); i++)
            checkAll((OverviewTreeNode)root.getChildAt(i), checked);
    }
    
    /**
     * Gets the list of checked instances.
     * @param root root node to start.
     * @return list of checked instances.
     */
    public List<DODInstance> getChecked (OverviewTreeNode root) {
        List<DODInstance> checked = new ArrayList<DODInstance>();
        if (root.getData() instanceof DODInstance && ((DODInstance)root.getData()).isChecked())
            checked.add((DODInstance)root.getData());
        for (int i=0; i < root.getChildCount(); i++)
            checked.addAll(getChecked((OverviewTreeNode)root.getChildAt(i)));
        return checked;
    }
}
