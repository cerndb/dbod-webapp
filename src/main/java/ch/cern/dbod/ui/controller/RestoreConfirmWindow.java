/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.dbod.ui.controller;

import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Snapshot;
import ch.cern.dbod.ui.model.OverviewTreeModel;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.JobHelper;
import java.util.Date;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

/**
 * Confirm window to warn the user about the restore operation.
 */
public class RestoreConfirmWindow extends Window {
    
    /**
     * Instance being managed at the moment.
     */
    private Instance instance;
    /**
     * User authenticated in the system at the moment.
     */
    private String username;
    /**
     * Snaphsot to restore.
     */
    private Snapshot snapshotToRestore;
    /**
     * Helper to execute jobs.
     */
    private JobHelper jobHelper;
    /**
     * Timebox to select time to restore
     */
    private Timebox time;
    /**
     * Day to restore
     */
    private Date dateToRestore;
    /**
     * Model of the tree (null if we are in list view).
     */
    private OverviewTreeModel model;
    /**
     * RestoreController parent component.
     */
    RestoreController parent;

    /**
     * Constructor for this window.
     * @throws InterruptedException if the window cannot be created.
     */
    public RestoreConfirmWindow(RestoreController parent, JobHelper jobHelper, OverviewTreeModel model, Instance instance, String username, Timebox time, Snapshot snap, Date date) throws InterruptedException {
        //Call super constructor
        super();
        
        //Initialize instance and create job helper
        this.parent = parent;
        this.jobHelper = jobHelper;
        this.model = model;
        this.username = username;
        this.instance = instance;

        //Instantiate variables
        snapshotToRestore = snap;
        dateToRestore = date;

        //Basic window properties
        this.setId("restoreConfirmWindow");
        this.setTitle(Labels.getLabel(CommonConstants.LABEL_RESTORE_CONFIRM_TITLE));
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
        messageBox.appendChild(new Image(CommonConstants.IMG_WARNING));
        //Main message
        Label message = new Label(Labels.getLabel(CommonConstants.LABEL_RESTORE_CONFIRM_MESSAGE));
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
        cancelButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_CANCEL));
        cancelButton.setZclass(CommonConstants.STYLE_BUTTON);
        cancelButton.setImage(CommonConstants.IMG_CANCEL);
        cancelButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doCancel();
            }
        });
        cancelBox.appendChild(cancelButton);
        Label cancelLabel = new Label(Labels.getLabel(CommonConstants.LABEL_CANCEL));
        cancelLabel.setSclass(CommonConstants.STYLE_TITLE);
        cancelLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        cancelLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doCancel();
            }
        });
        cancelBox.appendChild(cancelLabel);
        buttonsDiv.appendChild(cancelBox);

        //Accept button
        Hbox acceptBox = new Hbox();
        acceptBox.setHeight("24px");
        acceptBox.setAlign("bottom");
        acceptBox.setStyle("float:right;");
        Label acceptLabel = new Label(Labels.getLabel(CommonConstants.LABEL_ACCEPT));
        acceptLabel.setSclass(CommonConstants.STYLE_TITLE);
        acceptLabel.setStyle("font-size:10pt !important;cursor:pointer;");
        acceptLabel.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doAccept();
            }
        });
        acceptBox.appendChild(acceptLabel);
        Toolbarbutton acceptButton = new Toolbarbutton();
        acceptButton.setTooltiptext(Labels.getLabel(CommonConstants.LABEL_ACCEPT));
        acceptButton.setZclass(CommonConstants.STYLE_BUTTON);
        acceptButton.setImage(CommonConstants.IMG_ACCEPT);
        acceptButton.addEventListener(Events.ON_CLICK, new EventListener() {
            @Override
            public void onEvent(Event event) {
                doAccept();
            }
        });
        acceptBox.appendChild(acceptButton);
        buttonsDiv.appendChild(acceptBox);
        this.appendChild(buttonsDiv);
    }


    /**
     * Method executed when user accepts the form. A job is created and the window is detached.
     */
    private void doAccept() {
        //Create new job and update instance status
        if (jobHelper.doRestore(instance, username, snapshotToRestore, dateToRestore)) {
            if (model != null) {
            //Reload the node
            model.updateInstance(instance);
        } //If we are in the instance page
            else if (time.getRoot().getFellowIfAny("controller") != null && time.getRoot().getFellow("controller") instanceof InstanceController) {
                InstanceController controller = (InstanceController) time.getRoot().getFellow("controller");
                controller.afterCompose();
            }
        } else {
            parent.showError(CommonConstants.ERROR_DISPATCHING_JOB);
        }
        this.detach();
        parent.detach();
    }

    /**
     * Method executed when the user cancels the form. The window is detached from the page.
     */
    private void doCancel() {
        this.detach();
    }
}
