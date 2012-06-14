package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the upgrade window. Creates the window and all its components.
 * @author Daniel Gomez Blanco
 */
public class UpgradeController extends Window {

    /**
     * Instance being managed at the moment.
     */
    private DODInstance instance;
    /**
     * Helper to execute jobs.
     */
    private JobHelper jobHelper;
    /**
     * User authenticated in the system at the moment.
     */
    private String username;
    /**
     * List of instances shared wit the current one.
     */
    private List<DODInstance> sharedInstances;


    /**
     * Constructor for this window.
     * @param inst instance to be managed.
     * @param user username for the authenticated user.
     * @param jobHelper helper to execute jobs.
     * @throws InterruptedException if the window cannot be created.
     */
    public UpgradeController (DODInstance inst, String user, JobHelper jobHelper) throws InterruptedException {        
        //Call super constructor
        super();
        
        //Initialize instance and create job helper
        this.instance = inst;
        this.jobHelper = jobHelper;
        this.username = user;
        
        DODInstanceDAO instanceDAO = new DODInstanceDAO();
        DODUpgradeDAO upgradeDAO = new DODUpgradeDAO();
        List<DODUpgrade> upgrades = upgradeDAO.selectAll();
        
        //Boolean that indicates if the slave is upgraded (requirement in case of master upgrade)
        boolean slaveUpgraded = true;
        if (instance.getSlave() != null && !instance.getSlave().isEmpty()) {
            DODInstance slave = instanceDAO.selectByDbName(instance.getSlave(), upgrades);
            if (slave.getUpgradeTo() != null && !slave.getUpgradeTo().isEmpty())
                slaveUpgraded = false;
        }
        
        //Get shared instances
        if (instance.getSharedInstance() != null && !instance.getSharedInstance().isEmpty()) {
            sharedInstances = instanceDAO.selectSharedInstances(instance.getSharedInstance(), upgrades);
            sharedInstances.remove(instance);
        }

        //Basic window properties
        this.setId("upgradeWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_UPGRADE_TITLE));
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("350px");

        //Main box used to apply pading
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        //Box for message
        Hbox messageBox = new Hbox();
        messageBox.setAlign("center");
        messageBox.appendChild(new Image(DODConstants.IMG_WARNING));
        //Main message
        String messageStr;
        
        //If the slave is upgraded (or instance is not master)
        if (slaveUpgraded) {
            messageStr = Labels.getLabel(DODConstants.LABEL_UPGRADE_MESSAGE_FROM) + " " + instance.getVersion()
                                        + " " + Labels.getLabel(DODConstants.LABEL_UPGRADE_MESSAGE_TO) + " " +instance.getUpgradeTo() + "?";

            //Append warning to message in case of sharedInstance
            if (sharedInstances != null && sharedInstances.size() > 0) {
                messageStr += " " + Labels.getLabel(DODConstants.LABEL_SHARED_INSTANCE_WARNING) + " " + instance.getSharedInstance() + " (";
                for (int i = 0; i < sharedInstances.size() - 1; i++) {
                    messageStr += sharedInstances.get(i).getDbName() + ", ";
                }
                messageStr += sharedInstances.get(sharedInstances.size() - 1).getDbName() + ").";
            }
        }
        else {
            messageStr = Labels.getLabel(DODConstants.LABEL_UPGRADE_SLAVE_FIRST);
        }
        
        Label message = new Label(messageStr);
        messageBox.appendChild(message);
        mainBox.appendChild(messageBox);

        //Div for accept and cancel buttons
        Div buttonsDiv = new Div();
        buttonsDiv.setWidth("100%");

        //Cancel button
        Hbox cancelBox = new Hbox();
        cancelBox.setHeight("24px");
        cancelBox.setAlign("bottom");
        cancelBox.setStyle("float:left;");
        Toolbarbutton cancelButton = new Toolbarbutton();
        cancelButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CANCEL));
        cancelButton.setZclass(DODConstants.STYLE_BUTTON);
        cancelButton.setImage(DODConstants.IMG_CANCEL);
        cancelButton.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doCancel();
            }
        });
        cancelBox.appendChild(cancelButton);
        Label cancelLabel = new Label(Labels.getLabel(DODConstants.LABEL_CANCEL));
        cancelLabel.setSclass(DODConstants.STYLE_TITLE);
        cancelLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        cancelLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                doCancel();
            }
        });
        cancelBox.appendChild(cancelLabel);
        buttonsDiv.appendChild(cancelBox);

        //Accept button (only created when slave is upgraded or instance is not master)
        if (slaveUpgraded) {
            Hbox acceptBox = new Hbox();
            acceptBox.setHeight("24px");
            acceptBox.setAlign("bottom");
            acceptBox.setStyle("float:right;");
            Label acceptLabel = new Label(Labels.getLabel(DODConstants.LABEL_ACCEPT));
            acceptLabel.setSclass(DODConstants.STYLE_TITLE);
            acceptLabel.setStyle("font-size:10pt !important;cursor:pointer;");
            acceptLabel.addEventListener(Events.ON_CLICK, new EventListener() {
                public void onEvent(Event event) {
                    doAccept();
                }
            });
            acceptBox.appendChild(acceptLabel);
            Toolbarbutton acceptButton = new Toolbarbutton();
            acceptButton.setTooltiptext(Labels.getLabel(DODConstants.LABEL_ACCEPT));
            acceptButton.setZclass(DODConstants.STYLE_BUTTON);
            acceptButton.setImage(DODConstants.IMG_ACCEPT);
            acceptButton.addEventListener(Events.ON_CLICK, new EventListener() {
                public void onEvent(Event event) {
                    doAccept();
                }
            });
            acceptBox.appendChild(acceptButton);
            buttonsDiv.appendChild(acceptBox);
        }
        this.appendChild(buttonsDiv);
    }
    

    /**
     * Method executed when user accepts the form. A job is created and the window is detached.
     */
    private void doAccept() {
        ///Create new job and update instance status
        if (jobHelper.doUpgrade(instance, username)) {
            //If we are in the overview page
            if (this.getRoot().getFellowIfAny("overviewTree") != null) {
                Tree tree = (Tree) this.getRoot().getFellow("overviewTree");
                tree.setModel(tree.getModel());
            } //If we are in the instance page
            else if (this.getRoot().getFellowIfAny("controller") != null && this.getRoot().getFellow("controller") instanceof InstanceController) {
                InstanceController controller = (InstanceController) this.getRoot().getFellow("controller");
                controller.afterCompose();
            }
        }
        else {
            showError(DODConstants.ERROR_DISPATCHING_UPGRADE_JOB);
        }
        this.detach();
    }

    /**
     * Method executed when the user cancels the form. The window is detached from the page.
     */
    private void doCancel() {
        this.detach();
    }

    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(String errorCode) {
        Window errorWindow = (Window) this.getParent().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
