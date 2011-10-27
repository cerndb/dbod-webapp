package ch.cern.dod.ui.controller;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.exception.ConfigFileSizeException;
import ch.cern.dod.util.ConfigFileHelper;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the config. Creates the window and all its components.
 * @author Daniel Gomez Blanco
 * @version 17/10/2011
 */
public class ConfigController extends Window {

    /**
     * Instance being managed at the moment.
     */
    private DODInstance instance;
    /**
     * Helper to execute jobs.
     */
    private JobHelper jobHelper;
    /**
     * Combobox with the filetype to process.
     */
    private Combobox type;
    /**
     * User athenticated in the system at the moment.
     */
    private String username;

    /**
     * Helper to obtain config files.
     */
    ConfigFileHelper fileHelper;

    /**
     * Constructor for this window.
     * @param inst instance to be managed.
     * @param user username for the authenticated user.
     * @param jobHelper helper to execute jobs.
     * @throws InterruptedException if the window cannot be created.
     */
    public ConfigController(DODInstance inst, String user, JobHelper helper) throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance and create job helper
        this.instance = inst;
        this.username = user;
        this.jobHelper = helper;

        //Get user and password for the web services account
        String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_USER);
        String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_PSWD);
        fileHelper = new ConfigFileHelper(wsUser, wsPswd);

        //Basic window properties
        this.setId("configWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_CONFIG_TITLE));
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("350px");

        //Main box used to apply padding
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        //Main message
        Label message = new Label(Labels.getLabel(DODConstants.LABEL_CONFIG_MESSAGE));
        mainBox.appendChild(message);

        //Box containing the file selector and buttons
        Hbox configBox =  new Hbox();
        configBox.setStyle("margin-top:10px;margin-bottom:10px;margin-left:20px");
        configBox.setAlign("bottom");
        //Create combobox for file selector
        type = getCombobox(instance.getDbType());
        configBox.appendChild(type);
        //Upload config file button
        Div uploadDiv = new Div();
        uploadDiv.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CONFIG_UPLOAD));
        uploadDiv.setSclass(DODConstants.STYLE_BUTTON);
        Toolbarbutton uploadBtn = new Toolbarbutton();
        uploadBtn.setImage(DODConstants.IMG_UPLOAD);
        uploadBtn.setParent(uploadDiv);
        uploadBtn.setUpload("true");
        uploadBtn.addEventListener(Events.ON_UPLOAD, new EventListener() {
            public void onEvent(Event event) {
                try {
                    //Check config file value
                    if (isTypeValid()) {
                        //Create new job and update instance status
                        String fileType = ((String[])type.getSelectedItem().getValue())[0];
                        String filePath = ((String[])type.getSelectedItem().getValue())[1];
                        boolean result = jobHelper.doUpload(instance, username, fileType, filePath, (UploadEvent) event);
                        //Depending on the result
                        if (result) {
                            //If we are in the overview page
                            if (type.getRoot().getFellowIfAny("overviewGrid") != null) {
                                Grid grid = (Grid) type.getRoot().getFellow("overviewGrid");
                                grid.setModel(grid.getListModel());
                            } //If we are in the instance page
                            else if (type.getRoot().getFellowIfAny("controller") != null && type.getRoot().getFellow("controller") instanceof InstanceController) {
                                InstanceController controller = (InstanceController) type.getRoot().getFellow("controller");
                                controller.afterCompose();
                            }
                        }
                        type.getFellow("configWindow").detach();
                    }
                    else{
                        type.setErrorMessage(Labels.getLabel(DODConstants.ERROR_CONFIG_TYPE));
                    }
                }
                catch (ConfigFileSizeException ex) {
                    showError(DODConstants.ERROR_UPLOADING_CONFIG_FILE_SIZE, ex);
                }
                catch (IOException ex) {
                    showError(DODConstants.ERROR_UPLOADING_CONFIG_FILE, ex);
                }
            }
        });
        configBox.appendChild(uploadDiv);
        //Download config file button
        Div downloadDiv = new Div();
        downloadDiv.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CONFIG_DOWNLOAD));
        downloadDiv.setSclass(DODConstants.STYLE_BUTTON);
        Toolbarbutton downloadBtn = new Toolbarbutton();
        downloadBtn.setImage(DODConstants.IMG_DOWNLOAD);
        downloadBtn.setParent(downloadDiv);
        downloadBtn.addEventListener(Events.ON_CLICK, new EventListener() {
            public void onEvent(Event event) {
                //Check config file value
                if (isTypeValid()) {
                    //Obtain file
                    String filePath = ((String[])type.getSelectedItem().getValue())[1];
                    AMedia file = fileHelper.getFile(instance, filePath);
                    if (file != null) {
                        Filedownload.save(file);
                        type.getFellow("configWindow").detach();
                    }
                    else {
                        Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, "File" + filePath);
                        showError(DODConstants.ERROR_DOWNLOADING_CONFIG_FILE, null);
                    }
                }
                else{
                    type.setErrorMessage(Labels.getLabel(DODConstants.ERROR_CONFIG_TYPE));
                }
            }
        });
        configBox.appendChild(downloadDiv);
        mainBox.appendChild(configBox);

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
        this.appendChild(buttonsDiv);
    }

    /**
     * Method executed when the user cancels the form. The window is detached from the page.
     */
    private void doCancel() {
        type.getFellow("configWindow").detach();
    }

    /**
     * Get the different types of configuration files for a DB type.
     * @param dbType type of the DB to get the configuration file from.
     * @return a combobox with the different configuration files, and their paths.
     */
    private Combobox getCombobox (String dbType) {
        Combobox toret =  new Combobox();
        toret.setReadonly(true);
        if (dbType.equals(DODConstants.DB_TYPE_MYSQL)) {
            Comboitem myCfg = new Comboitem();
            myCfg.setLabel(Labels.getLabel(DODConstants.LABEL_CONFIG + DODConstants.CONFIG_FILE_MY_CNF));
            myCfg.setValue(new String[]{DODConstants.CONFIG_FILE_MY_CNF, DODConstants.CONFIG_PATH_MY_CNF});
            toret.appendChild(myCfg);
        }
        toret.setSelectedIndex(0);
        return toret;
    }

    /**
     * Checks if the selected type is valid.
     * @return true if the selected type is valid, false otherwise.
     */
    public boolean isTypeValid() {
        Comboitem item = type.getSelectedItem();
        if (item != null && item.getValue() != null && item.getValue() instanceof String[] && ((String[])item.getValue()).length == 2) {
            String fileType = ((String[])item.getValue())[0];
            if (!DODConstants.CONFIG_FILE_MY_CNF.equals(fileType))
                return false;
            String filePath = ((String[])item.getValue())[1];
            if (!DODConstants.CONFIG_PATH_MY_CNF.equals(filePath))
                return false;
            return true;
        }
        else
            return false;
    }

    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     * @param exception exception to log
     */
    private void showError(String errorCode, Exception exception) {
        if (exception != null) {
            Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, "ERROR DISPATCHING JOB", exception);
        }
        Window errorWindow = (Window) this.getParent().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
