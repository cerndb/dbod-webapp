package ch.cern.dod.ui.controller;

import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.util.DODConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the add upgrade window. Creates the window and all its components.
 * @author Daniel Gomez Blanco
 */
public class AddUpgradeController extends Window {

    /**
     * Upgrade DAO.
     */
    private DODUpgradeDAO upgradeDAO;
    /**
     * DB Type to add upgrade
     */
    Combobox dbType;
    /**
     * Category to add upgrade
     */
    Combobox category;
    /**
     * Version to add upgrade from
     */
    Textbox versionFrom;
    /**
     * Version to add upgrade to
     */
    Textbox versionTo;

    /**
     * Constructor for this window.
     * @throws InterruptedException if the window cannot be created.
     */
    public AddUpgradeController() throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance DAO
        this.upgradeDAO = new DODUpgradeDAO();

        //Basic window properties
        this.setId("destroyWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_ADD_UPGRADE_TITLE));
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("365px");

        //Main box used to apply pading
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        //Main message
        Label message = new Label(Labels.getLabel(DODConstants.LABEL_ADD_UPGRADE_MESSAGE));
        mainBox.appendChild(message);
        
        //Box for values
        Grid values = new Grid();
        values.setStyle("border:none");
        Columns columns = new Columns();
        Column left = new Column();
        left.setWidth("120px");
        columns.appendChild(left);
        Column right = new Column();
        right.setWidth("220px");
        columns.appendChild(right);
        values.appendChild(columns);
        Rows rows = new Rows();
        
        //Type of DB
        Row typeRow = new Row();
        typeRow.setStyle("border:none");
        Label typeLabel = new Label();
        typeLabel.setValue(Labels.getLabel(DODConstants.LABEL_DB_TYPE));
        typeRow.appendChild(typeLabel);
        dbType = new Combobox();
        dbType.setWidth("200px");
        dbType.setMold("rounded");
        dbType.setReadonly(true);
        Comboitem selectOneType = new Comboitem();
        selectOneType.setValue(null);
        selectOneType.setLabel(Labels.getLabel(DODConstants.LABEL_SELECT_ONE));
        dbType.appendChild(selectOneType);
//        Comboitem oracle = new Comboitem();
//        oracle.setValue(DODConstants.DB_TYPE_ORACLE);
//        oracle.setLabel(Labels.getLabel(DODConstants.LABEL_DB_TYPE + DODConstants.DB_TYPE_ORACLE));
//        dbType.appendChild(oracle);
        Comboitem mySQL = new Comboitem();
        mySQL.setValue(DODConstants.DB_TYPE_MYSQL);
        mySQL.setLabel(Labels.getLabel(DODConstants.LABEL_DB_TYPE + DODConstants.DB_TYPE_MYSQL));
        dbType.appendChild(mySQL);
        dbType.setSelectedIndex(0);
        typeRow.appendChild(dbType);
        rows.appendChild(typeRow);
        
        //Category
        Row categoryRow = new Row();
        categoryRow.setStyle("border:none");
        Label categoryLabel = new Label();
        categoryLabel.setValue(Labels.getLabel(DODConstants.LABEL_CATEGORY));
        categoryRow.appendChild(categoryLabel);
        category = new Combobox();
        category.setWidth("200px");
        category.setMold("rounded");
        category.setReadonly(true);
        Comboitem selectOneCategory = new Comboitem();
        selectOneCategory.setValue(null);
        selectOneCategory.setLabel(Labels.getLabel(DODConstants.LABEL_SELECT_ONE));
        category.appendChild(selectOneCategory);
        Comboitem official = new Comboitem();
        official.setValue(DODConstants.CATEGORY_OFFICIAL);
        official.setLabel(Labels.getLabel(DODConstants.LABEL_CATEGORY + DODConstants.CATEGORY_OFFICIAL));
        category.appendChild(official);
        Comboitem personal = new Comboitem();
        personal.setValue(DODConstants.CATEGORY_PERSONAL);
        personal.setLabel(Labels.getLabel(DODConstants.LABEL_CATEGORY + DODConstants.CATEGORY_PERSONAL));
        category.appendChild(personal);
        Comboitem test = new Comboitem();
        test.setValue(DODConstants.CATEGORY_TEST);
        test.setLabel(Labels.getLabel(DODConstants.LABEL_CATEGORY + DODConstants.CATEGORY_TEST));
        category.appendChild(test);
        category.setSelectedIndex(0);
        categoryRow.appendChild(category);
        rows.appendChild(categoryRow);
        
        //Version from
        Row versionFromRow = new Row();
        versionFromRow.setStyle("border:none");
        Label versionFromLabel = new Label();
        versionFromLabel.setValue(Labels.getLabel(DODConstants.LABEL_VERSION_FROM));
        versionFromRow.appendChild(versionFromLabel);
        versionFrom = new Textbox();
        versionFrom.setWidth("200px");
        versionFrom.setMaxlength(DODConstants.MAX_VERSION_LENGTH);
        versionFromRow.appendChild(versionFrom);
        rows.appendChild(versionFromRow);
        
        //Version to
        Row versionToRow = new Row();
        versionToRow.setStyle("border:none");
        Label versionToLabel = new Label();
        versionToLabel.setValue(Labels.getLabel(DODConstants.LABEL_VERSION_TO));
        versionToRow.appendChild(versionToLabel);
        versionTo = new Textbox();
        versionTo.setWidth("200px");
        versionTo.setMaxlength(DODConstants.MAX_VERSION_LENGTH);
        versionToRow.appendChild(versionTo);
        rows.appendChild(versionToRow);
        
        //Append rows to grid and grid to window
        values.appendChild(rows);
        mainBox.appendChild(values);

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
        cancelButton.setSclass(DODConstants.STYLE_BUTTON);
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

        //Accept button
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
        acceptButton.setSclass(DODConstants.STYLE_BUTTON);
        acceptButton.setImage(DODConstants.IMG_ACCEPT);
        acceptButton.addEventListener(Events.ON_CLICK, new EventListener() {
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
        if (isDbTypeValid() & isCategoryValid() & isVersionFromValid() & isVersionToValid()) {
            ///Create new upgrade and insert it
            DODUpgrade upgrade = new DODUpgrade();
            upgrade.setDbType((String)dbType.getSelectedItem().getValue());
            upgrade.setCategory((String)category.getSelectedItem().getValue());
            upgrade.setVersionFrom(versionFrom.getValue());
            upgrade.setVersionTo(versionTo.getValue());
            if (upgradeDAO.insert(upgrade)) {
                Executions.sendRedirect(DODConstants.PAGE_ADMIN);
            } else {
                showError(DODConstants.ERROR_ADDING_UPGRADE);
            }
            this.detach();
        }
    }

    /**
     * Method executed when the user cancels the form. The window is detached from the page.
     */
    private void doCancel() {
        this.detach();
    }
    
    /**
     * Validates DB type
     * @return true if DB type is valid, false otherwise
     */
    private boolean isDbTypeValid() {
        //If there are no previous errors
        if (dbType.getErrorMessage() == null || dbType.getErrorMessage().isEmpty()) {
            //Check if user has selected a value
            if (dbType.getSelectedItem() == null || dbType.getSelectedItem().getValue() == null) {
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
     * Validates category
     * @return true if category is valid, false otherwise
     */
    private boolean isCategoryValid() {
        //If there are no previous errors
        if (category.getErrorMessage() == null || category.getErrorMessage().isEmpty()) {
            //Check if user has selected a value
            if (category.getSelectedItem() == null || category.getSelectedItem().getValue() == null) {
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
     * Validates version from
     * @return true if version is valid, false otherwise
     */
    private boolean isVersionFromValid() {
        //If there are no previous errors
        if (versionFrom.getErrorMessage() == null || versionFrom.getErrorMessage().isEmpty()) {
            //Trim
            versionFrom.setValue(versionFrom.getValue().trim());
            //Check if user has entered a value
            if (versionFrom.getValue().isEmpty()) {
                versionFrom.setErrorMessage(Labels.getLabel(DODConstants.ERROR_VERSION_EMPTY));
                return false;
            }
            //Check dbName length
            if (versionFrom.getValue().length() > DODConstants.MAX_VERSION_LENGTH) {
                versionFrom.setErrorMessage(Labels.getLabel(DODConstants.ERROR_VERSION_LENGTH));
                return false;
            }
        }
        else
            return false;
        return true;
    }
    
    /**
     * Validates version to
     * @return true if version is valid, false otherwise
     */
    private boolean isVersionToValid() {
        //If there are no previous errors
        if (versionTo.getErrorMessage() == null || versionTo.getErrorMessage().isEmpty()) {
            //Trim
            versionTo.setValue(versionTo.getValue().trim());
            //Check if user has entered a value
            if (versionTo.getValue().isEmpty()) {
                versionTo.setErrorMessage(Labels.getLabel(DODConstants.ERROR_VERSION_EMPTY));
                return false;
            }
            //Check dbName length
            if (versionTo.getValue().length() > DODConstants.MAX_VERSION_LENGTH) {
                versionTo.setErrorMessage(Labels.getLabel(DODConstants.ERROR_VERSION_LENGTH));
                return false;
            }
        }
        else
            return false;
        return true;
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
