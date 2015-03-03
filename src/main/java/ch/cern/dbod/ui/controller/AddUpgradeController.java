package ch.cern.dbod.ui.controller;

import ch.cern.dbod.db.dao.UpgradeDAO;
import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.FormValidations;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
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
    private UpgradeDAO upgradeDAO;
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
        this.upgradeDAO = new UpgradeDAO();

        //Basic window properties
        this.setId("destroyWindow");
        this.setTitle(Labels.getLabel(CommonConstants.LABEL_ADD_UPGRADE_TITLE));
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("370px");

        //Main box used to apply pading
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        //Main message
        Label message = new Label(Labels.getLabel(CommonConstants.LABEL_ADD_UPGRADE_MESSAGE));
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
        typeLabel.setValue(Labels.getLabel(CommonConstants.LABEL_DB_TYPE));
        typeRow.appendChild(typeLabel);
        dbType = new Combobox();
        dbType.setWidth("200px");
        dbType.setMold("rounded");
        dbType.setReadonly(true);
        Comboitem selectOneType = new Comboitem();
        selectOneType.setValue(null);
        selectOneType.setLabel(Labels.getLabel(CommonConstants.LABEL_SELECT_ONE));
        dbType.appendChild(selectOneType);
        Comboitem mySQL = new Comboitem();
        mySQL.setValue(CommonConstants.DB_TYPE_MYSQL);
        mySQL.setLabel(Labels.getLabel(CommonConstants.LABEL_DB_TYPE + CommonConstants.DB_TYPE_MYSQL));
        dbType.appendChild(mySQL);
        Comboitem oracle = new Comboitem();
        oracle.setValue(CommonConstants.DB_TYPE_ORACLE);
        oracle.setLabel(Labels.getLabel(CommonConstants.LABEL_DB_TYPE + CommonConstants.DB_TYPE_ORACLE));
        dbType.appendChild(oracle);
        Comboitem ora = new Comboitem();
        ora.setValue(CommonConstants.DB_TYPE_ORA);
        ora.setLabel(Labels.getLabel(CommonConstants.LABEL_DB_TYPE + CommonConstants.DB_TYPE_ORA));
        dbType.appendChild(ora);
        Comboitem pg = new Comboitem();
        pg.setValue(CommonConstants.DB_TYPE_PG);
        pg.setLabel(Labels.getLabel(CommonConstants.LABEL_DB_TYPE + CommonConstants.DB_TYPE_PG));
        dbType.appendChild(pg);
        dbType.setSelectedIndex(0);
        typeRow.appendChild(dbType);
        rows.appendChild(typeRow);
        
        //Category
        Row categoryRow = new Row();
        categoryRow.setStyle("border:none");
        Label categoryLabel = new Label();
        categoryLabel.setValue(Labels.getLabel(CommonConstants.LABEL_CATEGORY));
        categoryRow.appendChild(categoryLabel);
        category = new Combobox();
        category.setWidth("200px");
        category.setMold("rounded");
        category.setReadonly(true);
        Comboitem selectOneCategory = new Comboitem();
        selectOneCategory.setValue(null);
        selectOneCategory.setLabel(Labels.getLabel(CommonConstants.LABEL_SELECT_ONE));
        category.appendChild(selectOneCategory);
        Comboitem official = new Comboitem();
        official.setValue(CommonConstants.CATEGORY_OFFICIAL);
        official.setLabel(Labels.getLabel(CommonConstants.LABEL_CATEGORY + CommonConstants.CATEGORY_OFFICIAL));
        category.appendChild(official);
        Comboitem personal = new Comboitem();
        personal.setValue(CommonConstants.CATEGORY_PERSONAL);
        personal.setLabel(Labels.getLabel(CommonConstants.LABEL_CATEGORY + CommonConstants.CATEGORY_PERSONAL));
        category.appendChild(personal);
        Comboitem test = new Comboitem();
        test.setValue(CommonConstants.CATEGORY_TEST);
        test.setLabel(Labels.getLabel(CommonConstants.LABEL_CATEGORY + CommonConstants.CATEGORY_TEST));
        category.appendChild(test);
        category.setSelectedIndex(0);
        categoryRow.appendChild(category);
        rows.appendChild(categoryRow);
        
        //Version from
        Row versionFromRow = new Row();
        versionFromRow.setStyle("border:none");
        Label versionFromLabel = new Label();
        versionFromLabel.setValue(Labels.getLabel(CommonConstants.LABEL_VERSION_FROM));
        versionFromRow.appendChild(versionFromLabel);
        versionFrom = new Textbox();
        versionFrom.setWidth("200px");
        versionFrom.setMaxlength(CommonConstants.MAX_VERSION_LENGTH);
        versionFromRow.appendChild(versionFrom);
        rows.appendChild(versionFromRow);
        
        //Version to
        Row versionToRow = new Row();
        versionToRow.setStyle("border:none");
        Label versionToLabel = new Label();
        versionToLabel.setValue(Labels.getLabel(CommonConstants.LABEL_VERSION_TO));
        versionToRow.appendChild(versionToLabel);
        versionTo = new Textbox();
        versionTo.setWidth("200px");
        versionTo.setMaxlength(CommonConstants.MAX_VERSION_LENGTH);
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
        if (FormValidations.isDbTypeValid(dbType)
                & FormValidations.isCategoryValid(category)
                & FormValidations.isVersionValid(versionFrom)
                & FormValidations.isVersionValid(versionTo)) {
            ///Create new upgrade and insert it
            Upgrade upgrade = new Upgrade();
            upgrade.setDbType((String)dbType.getSelectedItem().getValue());
            upgrade.setCategory((String)category.getSelectedItem().getValue());
            upgrade.setVersionFrom(versionFrom.getValue());
            upgrade.setVersionTo(versionTo.getValue());
            if (upgradeDAO.insert(upgrade)) {
                ((AdminController)this.getRoot().getFellow("controller")).refreshInstances();
            } else {
                showError(CommonConstants.ERROR_ADDING_UPGRADE);
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
            Logger.getLogger(RestoreController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
