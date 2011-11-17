package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.util.AuthenticationHelper;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import ch.cern.dod.ws.authentication.AuthenticationLocator;
import ch.cern.dod.ws.authentication.AuthenticationSoap;
import ch.cern.dod.ws.authentication.AuthenticationSoapStub;
import ch.cern.dod.ws.authentication.UserInfo;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.xml.rpc.ServiceException;
import org.apache.axis.AxisFault;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Slider;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

/**
 * Controller for the window that creates a new instance in DBOnDemand
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class NewInstanceController extends Window implements AfterCompose {
    /**
     * User creating the instance.
     */
    private String fullName;
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
    DODInstanceDAO instanceDAO;
    
    /**
     * Method called after the the children of every component have been composed.
     * It instantiates objects and interface components.
     */
    public void afterCompose() {
        //Get user and password for the web services account
        String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_USER);
        String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_PSWD);
        eGroupHelper = new EGroupHelper(wsUser, wsPswd);
        authenticationHelper = new AuthenticationHelper(wsUser, wsPswd);

        //Create DAO
        instanceDAO = new DODInstanceDAO();

        //Ininitialize components
        ((Textbox) getFellow("username")).setMaxlength(DODConstants.MAX_USERNAME_LENGTH);
        ((Textbox) getFellow("dbName")).setMaxlength(DODConstants.MAX_DB_NAME_LENGTH);
        ((Textbox) getFellow("eGroup")).setMaxlength(DODConstants.MAX_E_GROUP_LENGTH);
        ((Combobox) getFellow("category")).getItemAtIndex(0).setValue(DODConstants.CATEGORY_OFFICIAL);
        ((Combobox) getFellow("category")).getItemAtIndex(1).setValue(DODConstants.CATEGORY_PERSONAL);
        ((Combobox) getFellow("category")).getItemAtIndex(2).setValue(DODConstants.CATEGORY_TEST);
        ((Combobox) getFellow("category")).setSelectedIndex(0);
        ((Datebox) getFellow("expiryDate")).setFormat(DODConstants.DATE_FORMAT);
        ((Datebox) getFellow("expiryDate")).setTimeZonesReadonly(true);
        //((Combobox) getFellow("dbType")).getItemAtIndex(0).setValue(DODConstants.DB_TYPE_ORACLE);
        ((Combobox) getFellow("dbType")).getItemAtIndex(0).setValue(DODConstants.DB_TYPE_MYSQL);
        ((Combobox) getFellow("dbType")).setSelectedIndex(0);
        ((Slider) getFellow("dbSizeSlider")).setMaxpos(DODConstants.MAX_DB_SIZE);
        ((Textbox) getFellow("dbSize")).setMaxlength(String.valueOf(DODConstants.MAX_DB_SIZE).length());
        ((Slider) getFellow("noConnectionsSlider")).setMaxpos(DODConstants.MAX_NO_CONNECTIONS);
        ((Textbox) getFellow("noConnections")).setMaxlength(String.valueOf(DODConstants.MAX_NO_CONNECTIONS).length());
        ((Textbox) getFellow("project")).setMaxlength(DODConstants.MAX_PROJECT_LENGTH);
        ((Textbox) getFellow("description")).setMaxlength(DODConstants.MAX_DESCRIPTION_LENGTH);   
    }

    /**
     * Creates an instance, first checking if the egroup already exists.
     */
    public void createInstanceAndCheckEGroup () {
        //Check for errors in form
        if (isUsernameValid() & isDbNameValid() & isEGroupValid() & isCategoryValid() & isExpiryDateValid() & isDbTypeValid()
                & isDbSizeValid() & isNOConnectionsValid() & isProjectValid() & isDescriptionValid()) {
            //If there is an egroup
            if(((Textbox) getFellow("eGroup")).getValue() != null && !((Textbox) getFellow("eGroup")).getValue().isEmpty()) {
                //Check if e-group exists
                boolean eGroupExists = false;
                try {
                    eGroupExists = eGroupHelper.eGroupExists(((Textbox) getFellow("eGroup")).getValue());
                } catch (AxisFault ex) {
                    eGroupExists = false;
                } catch (ServiceException ex) {
                    ((Textbox) getFellow("eGroup")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_SEARCH));
                    return;
                } catch (RemoteException ex) {
                    ((Textbox) getFellow("eGroup")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_SEARCH));
                    return;
                }
                //If the egroup does not exist show confimation window and return
                if (!eGroupExists) {
                    try {
                        ((Window) getFellow("eGroupConfirm")).doModal();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR OPENING EGROUP CONFIRM WINDOW", ex);
                    } catch (SuspendNotAllowedException ex) {
                        Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR OPENING EGROUP CONFIRM WINDOW", ex);
                    }
                    return;
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
        if (isUsernameValid() & isDbNameValid() & isEGroupValid() & isCategoryValid() & isExpiryDateValid() & isDbTypeValid()
                & isDbSizeValid() & isNOConnectionsValid() & isProjectValid() & isDescriptionValid()) {
            try {
                boolean eGroupCreated = false;
                //If the egroup does not exist create it
                if (!eGroupExists) {
                    //If the egroup was successfully created store the instance in the DB
                    eGroupCreated = eGroupHelper.createEGroup(((Textbox) getFellow("eGroup")).getValue(),
                            ((Textbox) getFellow("dbName")).getValue(), userCCID, fullName);
                }
                //If the egroup exists or it was successfully created
                if (eGroupExists || eGroupCreated) {
                    //Create instace object
                    DODInstance instance = new DODInstance();
                    instance.setUsername(((Textbox) getFellow("username")).getValue());
                    instance.setDbName(((Textbox) getFellow("dbName")).getValue());
                    instance.setEGroup(((Textbox) getFellow("eGroup")).getValue());
                    instance.setCategory(((String)((Combobox) getFellow("category")).getSelectedItem().getValue()));
                    instance.setCreationDate(new Date());
                    instance.setExpiryDate(((Datebox) getFellow("expiryDate")).getValue());
                    instance.setDbType(((String)((Combobox) getFellow("dbType")).getSelectedItem().getValue()));
                    instance.setDbSize(Integer.valueOf(((Textbox) getFellow("dbSize")).getValue()));
                    if (!((Textbox) getFellow("noConnections")).getValue().isEmpty())
                        instance.setNoConnections(Integer.valueOf(((Textbox) getFellow("noConnections")).getValue()).intValue());
                    instance.setProject(((Textbox) getFellow("project")).getValue());
                    instance.setDescription(((Textbox) getFellow("description")).getValue());
                    instance.setStatus(true);
                    instance.setState(DODConstants.INSTANCE_STATE_AWAITING_APPROVAL);

                    //Insert object in DB
                    int result = instanceDAO.insert(instance);

                    //If the operation was succesful
                    if (result > 0) {
                        //Hide window and redirect to the instance page
                        this.setVisible(false);
                        Sessions.getCurrent().setAttribute(DODConstants.INSTANCE, instance);
                        Executions.sendRedirect(DODConstants.PAGE_INSTANCE);
                    }
                    else if (result == -1){
                        ((Textbox) getFellow("dbName")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_INSTANCE_UNIQUE));
                    }
                    else{
                        ((Textbox) getFellow("dbName")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_INSTANCE_CREATION));
                    }
                }
                else {
                    ((Textbox) getFellow("eGroup")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_CREATION));
                }
            }
            catch (ServiceException ex) {
                ((Textbox) getFellow("eGroup")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_CREATION));
                Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR CREATING EGROUP " + ((Textbox) getFellow("eGroup")).getValue(), ex);
            }
            catch (RemoteException ex) {
                ((Textbox) getFellow("eGroup")).setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_CREATION));
                Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR CREATING EGROUP " + ((Textbox) getFellow("eGroup")).getValue(), ex);
            }
        }  
    }
    
    /**
     * Validates username
     * @return true if username is valid, false otherwise
     */
    private boolean isUsernameValid() {
        Textbox username = (Textbox) getFellow("username");
        //If there are no previous errors
        if (username.getErrorMessage() == null || username.getErrorMessage().isEmpty()) {
            //Check if user has entered a value
            if (username.getValue().isEmpty()) {
                username.setErrorMessage(Labels.getLabel(DODConstants.ERROR_USERNAME_EMPTY));
                return false;
            }
            //Check dbName length
            if (username.getValue().length() > DODConstants.MAX_USERNAME_LENGTH) {
                username.setErrorMessage(Labels.getLabel(DODConstants.ERROR_USERNAME_LENGTH));
                return false;
            }
            //ASCII digits and non-digits
            if (!Pattern.matches("[a-zA-Z]*", username.getValue())) {
                username.setErrorMessage(Labels.getLabel(DODConstants.ERROR_USERNAME_CHARS));
                return false;
            }
            //Check if the user exists
            try {              
                UserInfo info = authenticationHelper.getUserInfo(username.getValue());
                if (info != null && info.getCcid() > 0) {
                    userCCID = info.getCcid();
                    fullName = info.getFirstname() + " " + info.getLastname();
                }
                else {
                    username.setErrorMessage(Labels.getLabel(DODConstants.ERROR_USERNAME_NOT_FOUND));
                    return false;
                }
            } catch (RemoteException ex) {
                username.setErrorMessage(Labels.getLabel(DODConstants.ERROR_USERNAME_WS));
                Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR OBTAINING INFO FOR USER " + username.getValue(), ex);
            } catch (ServiceException ex) {  
                username.setErrorMessage(Labels.getLabel(DODConstants.ERROR_USERNAME_WS));
                Logger.getLogger(NewInstanceController.class.getName()).log(Level.SEVERE, "ERROR OBTAINING INFO FOR USER " + username.getValue(), ex);
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates DB name
     * @return true if DB name is valid, false otherwise
     */
    private boolean isDbNameValid() {
        Textbox dbName = (Textbox) getFellow("dbName");
        //If there are no previous errors
        if (dbName.getErrorMessage() == null || dbName.getErrorMessage().isEmpty()) {
            //Check if user has entered a value
            if (dbName.getValue().isEmpty()) {
                dbName.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_NAME_EMPTY));
                return false;
            }
            //Check dbName length
            if (dbName.getValue().length() > DODConstants.MAX_DB_NAME_LENGTH) {
                dbName.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_NAME_LENGTH));
                return false;
            }
            //ASCII digits and non-digits
            if (!Pattern.matches("[\\da-zA-Z\\.\\-_]*", dbName.getValue())) {
                dbName.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_NAME_CHARS));
                return false;
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates e-Group name
     * @return true if e-Group name is valid, false otherwise
     */
    private boolean isEGroupValid() {
        Textbox eGroup = (Textbox) getFellow("eGroup");
        //If there are no previous errors
        if (eGroup.getErrorMessage() == null || eGroup.getErrorMessage().isEmpty()) {
            if (eGroup.getText().length() > 0) {
                //Check eGroup length
                if (eGroup.getText().length() > DODConstants.MAX_E_GROUP_LENGTH) {
                    eGroup.setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_LENGTH));
                    return false;
                }
                //Check if egroup name contains a dash
                if (!eGroup.getValue().contains("-")) {
                    eGroup.setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_DASH));
                    return false;
                }
                //Only upppercase and lowercase ASCII letters, numbers, dashes, dots and underscores are allowed
                if (!Pattern.matches("[\\da-zA-Z\\.\\-_]*", eGroup.getValue())) {
                    eGroup.setErrorMessage(Labels.getLabel(DODConstants.ERROR_E_GROUP_CHARS));
                    return false;
                }
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates category
     * @return true if category is valid, false otherwise
     */
    private boolean isCategoryValid() {
        Combobox category = (Combobox) getFellow("category");
        //If there are no previous errors
        if (category.getErrorMessage() == null || category.getErrorMessage().isEmpty()) {
            //Check if user has selected a value
            if (category.getSelectedItem() == null) {
                category.setErrorMessage(Labels.getLabel(DODConstants.ERROR_CATEGORY_EMPTY));
                return false;
            }
            //Check dbtype Oracle or MySQL
            if (!category.getSelectedItem().getValue().equals(DODConstants.CATEGORY_OFFICIAL)
                    && !category.getSelectedItem().getValue().equals(DODConstants.CATEGORY_PERSONAL)
                    && !category.getSelectedItem().getValue().equals(DODConstants.CATEGORY_TEST)) {
                category.setErrorMessage(Labels.getLabel(DODConstants.ERROR_CATEGORY_LIST));
                return false;
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates expiry date
     * @return true if expiry date is valid, false otherwise
     */
    private boolean isExpiryDateValid() {
        Datebox expiryDate = (Datebox) getFellow("expiryDate");
        //If there are no previous errors
        if (expiryDate.getErrorMessage() == null || expiryDate.getErrorMessage().isEmpty()) {
            //If the user has entered a value
            if (!expiryDate.getText().isEmpty()) {
                //Check valid date
                if (expiryDate.getValue() == null) {
                    expiryDate.setErrorMessage(Labels.getLabel(DODConstants.ERROR_EXPIRY_DATE_FORMAT));
                    return false;
                }
                //Check if it is a future date
                Date now = new Date();
                if (expiryDate.getValue().compareTo(now) <= 0) {
                    expiryDate.setErrorMessage(Labels.getLabel(DODConstants.ERROR_EXPIRY_DATE_FUTURE));
                    return false;
                }
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates DB type
     * @return true if DB type is valid, false otherwise
     */
    private boolean isDbTypeValid() {
        Combobox dbType = (Combobox) getFellow("dbType");
        //If there are no previous errors
        if (dbType.getErrorMessage() == null || dbType.getErrorMessage().isEmpty()) {
            //Check if user has selected a value
            if (dbType.getSelectedItem() == null) {
                dbType.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_TYPE_EMPTY));
                return false;
            }
            //Check dbtype Oracle or MySQL
            if (!dbType.getSelectedItem().getValue().equals(DODConstants.DB_TYPE_ORACLE)
                    && !dbType.getSelectedItem().getValue().equals(DODConstants.DB_TYPE_MYSQL)) {
                dbType.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_TYPE_LIST));
                return false;
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates DB size
     * @return true if DB size is valid, false otherwise
     */
    private boolean isDbSizeValid() {
        Textbox dbSize = (Textbox) getFellow("dbSize");
        //If there are no previous errors
        if (dbSize.getErrorMessage() == null || dbSize.getErrorMessage().isEmpty()) {
            //Check if user has entered a value
            if (dbSize.getValue().isEmpty()) {
                dbSize.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_SIZE_EMPTY));
                return false;
            }
            try {
                Integer size = Integer.valueOf(dbSize.getText());
                //Check dbName length
                if (size <= 0 || size >= DODConstants.MAX_DB_SIZE) {
                    dbSize.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_SIZE_RANGE));
                    return false;
                }
            } catch (NumberFormatException ex) {
                dbSize.setErrorMessage(Labels.getLabel(DODConstants.ERROR_INTEGER_FORMAT));
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates NO Connections
     * @return true if NO Connections is valid, false otherwise
     */
    private boolean isNOConnectionsValid() {
        Textbox noConnections = (Textbox) getFellow("noConnections");
        //If there are no previous errors
        if (noConnections.getErrorMessage() == null || noConnections.getErrorMessage().isEmpty()) {
            //Check only if user has entered a value
            if (!noConnections.getValue().isEmpty()) {
                try {
                    int noConn = Integer.valueOf(noConnections.getText()).intValue();
                    //Check dbName length
                    if (noConn <= 0 || noConn >= DODConstants.MAX_NO_CONNECTIONS) {
                        noConnections.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DB_SIZE_RANGE));
                        return false;
                    }
                } catch (NumberFormatException ex) {
                    noConnections.setErrorMessage(Labels.getLabel(DODConstants.ERROR_INTEGER_FORMAT));
                }
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates project
     * @return true if project is valid, false otherwise
     */
    private boolean isProjectValid() {
        Textbox project = (Textbox) getFellow("project");
        //If there are no previous errors
        if (project.getErrorMessage() == null || project.getErrorMessage().isEmpty()) {
            //Check description length
            if (project.getValue().length() > DODConstants.MAX_PROJECT_LENGTH) {
                project.setErrorMessage(Labels.getLabel(DODConstants.ERROR_PROJECT_LENGTH));
                return false;
            }
        }
        else
            return false;
        return true;
    }

    /**
     * Validates description
     * @return true if description is valid, false otherwise
     */
    private boolean isDescriptionValid() {
        Textbox description = (Textbox) getFellow("description");
        //If there are no previous errors
        if (description.getErrorMessage() == null || description.getErrorMessage().isEmpty()) {
            //Check description length
            if (description.getValue().length() > DODConstants.MAX_DESCRIPTION_LENGTH) {
                description.setErrorMessage(Labels.getLabel(DODConstants.ERROR_DESCRIPTION_LENGTH));
                return false;
            }
        }
        else
            return false;
        return true;
    }
}
