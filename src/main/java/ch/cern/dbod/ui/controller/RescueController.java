/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;

import ch.cern.dbod.db.dao.InstanceDAO;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.util.CommonConstants;
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
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the rescue window. Creates the window and all its components.
 * This modifies the status of an instance from 0 to 1, bringing it back to life.
 * @author Daniel Gomez Blanco
 */
public class RescueController extends Window {

    /**
     * Instance being managed at the moment.
     */
    private Instance instance;
    /**
     * DAO to rescue instance.
     */
    private InstanceDAO instanceDAO;


    /**
     * Constructor for this window.
     * @param inst instance to be managed.
     * @param instanceDAO DAO for instances.
     * @throws InterruptedException if the window cannot be created.
     */
    public RescueController (Instance inst, InstanceDAO instanceDAO) throws InterruptedException {        
        //Call super constructor
        super();
        
        //Initialize instance and DAO
        this.instance = inst;
        this.instanceDAO = instanceDAO;

        //Basic window properties
        this.setId("rescueWindow");
        this.setTitle(Labels.getLabel(CommonConstants.LABEL_RESCUE_TITLE) + " " + instance.getDbName());
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
        messageBox.appendChild(new Image(CommonConstants.IMG_WARNING));
        //Main message
        Label message = new Label(Labels.getLabel(CommonConstants.LABEL_RESCUE_MESSAGE));
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
     * Method executed when user accepts the form. The instance is deleted and the window is detached.
     */
    private void doAccept() {
        ///Delete instance
        if (instanceDAO.rescue(instance) == 1) {
            //Reload the grid
            AdminController controller = (AdminController) this.getRoot().getFellow("controller");
            controller.refreshInstances();
        }
        else {
            showError(CommonConstants.ERROR_RESCUING_INSTANCE);
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
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(RescueController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
