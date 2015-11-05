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
import ch.cern.dbod.util.AuthenticationHelper;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.EGroupHelper;
import ch.cern.dbod.util.FormValidations;
import ch.cern.dbod.ws.authentication.UserInfo;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.*;

/**
 * Controller for the window that creates a new instance in DBOnDemand
 * @author Daniel Gomez Blanco
 * @author Jose Andres Cordero Benitez
 */
public class NewInstanceController extends Window implements AfterCompose {
    /**
     * CCID of the user creating the instance.
     */
    private int userCCID;
    
    /**
     * Helper to manage e-groups.
     */
    AuthenticationHelper authenticationHelper;

    /**
     * Helper to manage e-groups.
     */
    EGroupHelper eGroupHelper;

    /**
     * DAO to create instances
     */
    InstanceDAO instanceDAO;
    
    /**
     * Method called after the the children of every component have been composed.
     * It instantiates objects and interface components.
     */
    @Override
    public void afterCompose() {
        //Get user and password for the web services account
        String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(CommonConstants.WS_USER);
        String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(CommonConstants.WS_PSWD);
        eGroupHelper = new EGroupHelper(wsUser, wsPswd);
        authenticationHelper = new AuthenticationHelper(wsUser, wsPswd);

        //Create DAO
        instanceDAO = new InstanceDAO();

        //Ininitialize components
        ((Textbox) getFellow("username")).setMaxlength(CommonConstants.MAX_USERNAME_LENGTH);
        ((Textbox) getFellow("dbName")).setMaxlength(CommonConstants.MAX_DB_NAME_LENGTH);
        ((Textbox) getFellow("eGroup")).setMaxlength(CommonConstants.MAX_E_GROUP_LENGTH);
        ((Combobox) getFellow("category")).getItemAtIndex(0).setValue(CommonConstants.CATEGORY_OFFICIAL);
        ((Combobox) getFellow("category")).getItemAtIndex(1).setValue(CommonConstants.CATEGORY_TEST);
        ((Combobox) getFellow("category")).setSelectedIndex(0);
        ((Datebox) getFellow("expiryDate")).setFormat(CommonConstants.DATE_FORMAT);
        ((Datebox) getFellow("expiryDate")).setTimeZonesReadonly(true);
        ((Combobox) getFellow("dbType")).getItemAtIndex(0).setValue(CommonConstants.DB_TYPE_MYSQL);
        ((Combobox) getFellow("dbType")).getItemAtIndex(1).setValue(CommonConstants.DB_TYPE_ORACLE);
        ((Combobox) getFellow("dbType")).getItemAtIndex(2).setValue(CommonConstants.DB_TYPE_ORA);
        ((Combobox) getFellow("dbType")).getItemAtIndex(3).setValue(CommonConstants.DB_TYPE_PG);
        ((Combobox) getFellow("dbType")).setSelectedIndex(0);
        ((Slider) getFellow("dbSizeSlider")).setMaxpos(CommonConstants.MAX_DB_SIZE);
        ((Textbox) getFellow("dbSize")).setMaxlength(String.valueOf(CommonConstants.MAX_DB_SIZE).length());
        ((Slider) getFellow("noConnectionsSlider")).setMaxpos(CommonConstants.MAX_NO_CONNECTIONS);
        ((Textbox) getFellow("noConnections")).setMaxlength(String.valueOf(CommonConstants.MAX_NO_CONNECTIONS).length());
        ((Textbox) getFellow("project")).setMaxlength(CommonConstants.MAX_PROJECT_LENGTH);
        ((Textbox) getFellow("description")).setMaxlength(CommonConstants.MAX_DESCRIPTION_LENGTH);
        ((Textbox) getFellow("version")).setMaxlength(CommonConstants.MAX_VERSION_LENGTH);
        ((Textbox) getFellow("master")).setMaxlength(CommonConstants.MAX_DB_NAME_LENGTH);
        ((Textbox) getFellow("host")).setMaxlength(CommonConstants.MAX_HOST_LENGTH);
    }

    /**
     * Creates an instance, first checking if the egroup already exists.
     */
    public void createInstanceAndCheckEGroup () {
        //Check for errors in form
        if (isUsernameValid()
                & FormValidations.isDbNameValid((Textbox) getFellow("dbName"))
                & FormValidations.isEGroupValid((Textbox) getFellow("eGroup"), (String)((Combobox)getFellow("dbType")).getSelectedItem().getValue(), eGroupHelper)
                & FormValidations.isCategoryValid((Combobox) getFellow("category"))
                & FormValidations.isExpiryDateValid((Datebox) getFellow("expiryDate"))
                & FormValidations.isDbTypeValid((Combobox) getFellow("dbType"))
                & FormValidations.isVersionValid((Textbox) getFellow("version"))
                & FormValidations.isMasterValid((Textbox) getFellow("master"), instanceDAO)
                & FormValidations.isHostValid((Textbox) getFellow("host"))
                & FormValidations.isDbSizeValid((Textbox) getFellow("dbSize"))
                & FormValidations.isNOConnectionsValid((Textbox) getFellow("noConnections"))
                & FormValidations.isProjectValid((Textbox) getFellow("project"))
                & FormValidations.isDescriptionValid((Textbox) getFellow("description"))) {
            //If there is an egroup
            if(((Textbox) getFellow("eGroup")).getValue() != null && !((Textbox) getFellow("eGroup")).getValue().isEmpty()) {
                //Check if e-group exists
                boolean eGroupExists = eGroupHelper.eGroupExists(((Textbox) getFellow("eGroup")).getValue());
                //If the egroup does not exist show confimation window and return
                if (!eGroupExists) {
                    try {
                        ((Window) getFellow("eGroupConfirm")).doModal();
                    } catch (SuspendNotAllowedException ex) {
                        Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR OPENING EGROUP CONFIRM WINDOW", ex);
                    }
                }
                //Create instance with this values
                else
                    createInstance(true);
            }
            //If the egroup is not specified we create the instance as if it already existed
            else
                createInstance(true);
        }
    }

    /**
     * Creates an instance and an egroup if necessary.
     * @param eGroupExists If the specified egroup exists.
     */
    public void createInstance(boolean eGroupExists) {
        //Check for errors in form
        if (isUsernameValid()
                & FormValidations.isDbNameValid((Textbox) getFellow("dbName"))
                & FormValidations.isEGroupValid((Textbox) getFellow("eGroup"), (String)((Combobox)getFellow("dbType")).getSelectedItem().getValue(), eGroupHelper)
                & FormValidations.isCategoryValid((Combobox) getFellow("category"))
                & FormValidations.isExpiryDateValid((Datebox) getFellow("expiryDate"))
                & FormValidations.isDbTypeValid((Combobox) getFellow("dbType"))
                & FormValidations.isVersionValid((Textbox) getFellow("version"))
                & FormValidations.isMasterValid((Textbox) getFellow("master"), instanceDAO)
                & FormValidations.isHostValid((Textbox) getFellow("host"))
                & FormValidations.isDbSizeValid((Textbox) getFellow("dbSize"))
                & FormValidations.isNOConnectionsValid((Textbox) getFellow("noConnections"))
                & FormValidations.isProjectValid((Textbox) getFellow("project"))
                & FormValidations.isDescriptionValid((Textbox) getFellow("description"))) {
            boolean eGroupCreated = false;
            //If the egroup does not exist create it
            if (!eGroupExists) {
                //If the egroup was successfully created store the instance in the DB
                eGroupCreated = eGroupHelper.createEGroup(((Textbox) getFellow("eGroup")).getValue(),
                        ((Textbox) getFellow("dbName")).getValue(), userCCID, true);
            }
            
            //If the egroups exists or it was created adn  instance is Oracle 12c create OEM e-group
            boolean addedToOEM = true;
            if ((eGroupExists || eGroupCreated)
                    && CommonConstants.DB_TYPE_ORA.equals((String)((Combobox)getFellow("dbType")).getSelectedItem().getValue())) {
                addedToOEM = eGroupHelper.addEgroupToOEM(((Textbox) getFellow("dbName")).getValue(),
                                            ((Textbox) getFellow("eGroup")).getValue());
            }
            
            //If the egroup exists or it was successfully created (and added to OEM)
            if ((eGroupExists || eGroupCreated) && addedToOEM) {
                //Create instace object
                Instance instance = new Instance();
                instance.setUsername(((Textbox) getFellow("username")).getValue());
                instance.setDbName(((Textbox) getFellow("dbName")).getValue());
                instance.setEGroup(((Textbox) getFellow("eGroup")).getValue());
                instance.setCategory(((String)((Combobox) getFellow("category")).getSelectedItem().getValue()));
                instance.setCreationDate(new Date());
                instance.setExpiryDate(((Datebox) getFellow("expiryDate")).getValue());
                instance.setDbType(((String)((Combobox) getFellow("dbType")).getSelectedItem().getValue()));
                instance.setVersion(((Textbox) getFellow("version")).getValue());
                instance.setMaster(((Textbox) getFellow("master")).getValue());
                instance.setHost(((Textbox) getFellow("host")).getValue());
                instance.setDbSize(Integer.valueOf(((Textbox) getFellow("dbSize")).getValue()));
                if (!((Textbox) getFellow("noConnections")).getValue().isEmpty())
                    instance.setNoConnections(Integer.valueOf(((Textbox) getFellow("noConnections")).getValue()).intValue());
                instance.setProject(((Textbox) getFellow("project")).getValue());
                instance.setDescription(((Textbox) getFellow("description")).getValue());
                instance.setStatus(true);
                instance.setState(CommonConstants.INSTANCE_STATE_AWAITING_APPROVAL);

                //Insert object in DB
                int result = instanceDAO.insert(instance);

                //If the operation was succesful
                if (result > 0) {
                    //Hide window and redirect to the instance page
                    this.setVisible(false);
                    Sessions.getCurrent().setAttribute(CommonConstants.INSTANCE, instance);
                    Executions.sendRedirect(CommonConstants.PAGE_INSTANCE + "?" + CommonConstants.INSTANCE + "=" + instance.getDbName());
                }
                else if (result == -1){
                    ((Textbox) getFellow("dbName")).setErrorMessage(Labels.getLabel(CommonConstants.ERROR_INSTANCE_UNIQUE));
                }
                else{
                    ((Textbox) getFellow("dbName")).setErrorMessage(Labels.getLabel(CommonConstants.ERROR_INSTANCE_CREATION));
                }
            }
            else {
                ((Textbox) getFellow("eGroup")).setErrorMessage(Labels.getLabel(CommonConstants.ERROR_E_GROUP_CREATION));
            }
        }
    }
    
    /**
     * Checks if the owner of the database is a valid user, and if the account exists.
     * @return true if the use is valid
     */
    private boolean isUsernameValid () {
        Textbox username = (Textbox) getFellow("username");
        UserInfo userInfo = FormValidations.isUsernameValid(username, authenticationHelper);
        if (userInfo != null) {
            userCCID = userInfo.getCcid();
            return true;
        }
        else
            return false;
    }
}