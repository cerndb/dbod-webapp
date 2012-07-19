package ch.cern.dod.ui.controller;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.exception.ConfigFileSizeException;
import ch.cern.dod.util.FileHelper;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.JobHelper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * Controller for the config. Creates the window and all its components.
 * @author Daniel Gomez Blanco
 * @version 17/10/2011
 */
public class FileController extends Window {

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
     * Combobox with the file name of the slow log to download.
     */
    private Combobox slowLog;
    /**
     * Array of slow logs.
     */
    private String[] slowLogArray;
    /**
     * User athenticated in the system at the moment.
     */
    private String username;

    /**
     * Helper to obtain config files.
     */
    FileHelper fileHelper;

    /**
     * Constructor for this window.
     * @param inst instance to be managed.
     * @param user username for the authenticated user.
     * @param jobHelper helper to execute jobs.
     * @throws InterruptedException if the window cannot be created.
     */
    public FileController(DODInstance inst, String user, JobHelper helper) throws InterruptedException {
        //Call super constructor
        super();

        //Initialize instance and create job helper
        this.instance = inst;
        this.username = user;
        this.jobHelper = helper;

        //Get user and password for the web services account
        String wsUser = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_USER);
        String wsPswd = ((ServletContext)Sessions.getCurrent().getWebApp().getNativeContext()).getInitParameter(DODConstants.WS_PSWD);
        fileHelper = new FileHelper(wsUser, wsPswd);

        //Basic window properties
        this.setId("filesWindow");
        this.setTitle(Labels.getLabel(DODConstants.LABEL_FILES_TITLE) + " " + instance.getDbName());
        this.setBorder("normal");
        this.setMode(Window.OVERLAPPED);
        this.setPosition("center");
        this.setClosable(false);
        this.setWidth("450px");

        //Main box used to apply padding
        Vbox mainBox = new Vbox();
        mainBox.setStyle("padding-top:5px;padding-left:5px;padding-right:5px");
        this.appendChild(mainBox);

        
        //Config files groupbox
        Groupbox config = new Groupbox();
        config.setClosable(false);
        Caption configCap = new Caption();
        configCap.setLabel(Labels.getLabel(DODConstants.LABEL_CONFIG_TITLE));
        configCap.setImage(DODConstants.IMG_CONFIG);
        config.appendChild(configCap);
        //Config files message
        Label configMessage = new Label(Labels.getLabel(DODConstants.LABEL_CONFIG_MESSAGE));
        config.appendChild(configMessage);

        //Box containing the file selector and buttons
        Hbox configBox =  new Hbox();
        configBox.setStyle("margin-top:10px;margin-bottom:10px;margin-left:20px");
        configBox.setAlign("bottom");
        //Create combobox for file selector
        type = getConfigCombobox(instance.getDbType());
        configBox.appendChild(type);
        //Upload config file button
        Div uploadDiv = new Div();
        uploadDiv.setTooltiptext(Labels.getLabel(DODConstants.LABEL_CONFIG_UPLOAD));
        Toolbarbutton uploadBtn = new Toolbarbutton();
        uploadBtn.setImage(DODConstants.IMG_UPLOAD);
        uploadBtn.setZclass(DODConstants.STYLE_BUTTON);
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
                            if (type.getRoot().getFellowIfAny("overviewTree") != null) {
                                Tree tree = (Tree) type.getRoot().getFellow("overviewTree");
                                tree.setModel(tree.getModel());
                            } //If we are in the instance page
                            else if (type.getRoot().getFellowIfAny("controller") != null && type.getRoot().getFellow("controller") instanceof InstanceController) {
                                InstanceController controller = (InstanceController) type.getRoot().getFellow("controller");
                                controller.afterCompose();
                            }
                        }
                        type.getFellow("filesWindow").detach();
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
        Toolbarbutton downloadBtn = new Toolbarbutton();
        downloadBtn.setZclass(DODConstants.STYLE_BUTTON);
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
                        type.getFellow("filesWindow").detach();
                    }
                    else {
                        Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, "ERROR ON INSTANCE " + instance.getDbName() + " DOWNLOADING CONFIG FILE: " + filePath);
                        showError(DODConstants.ERROR_DOWNLOADING_CONFIG_FILE, null);
                    }
                }
                else{
                    type.setErrorMessage(Labels.getLabel(DODConstants.ERROR_CONFIG_TYPE));
                }
            }
        });
        configBox.appendChild(downloadDiv);
        config.appendChild(configBox);
        mainBox.appendChild(config);
        
        //Slow logs groupbox (only if instance is MySQL)
        if (instance.getDbType().equals(DODConstants.DB_TYPE_MYSQL)) {
            Groupbox slowLogs = new Groupbox();
            slowLogs.setClosable(false);
            Caption slowLogsCap = new Caption();
            slowLogsCap.setLabel(Labels.getLabel(DODConstants.LABEL_SLOW_LOGS_TITLE));
            slowLogsCap.setImage(DODConstants.IMG_SLOW_LOGS);
            slowLogs.appendChild(slowLogsCap);
            //Slow logs message
            Label slowLogsMessage = new Label(Labels.getLabel(DODConstants.LABEL_SLOW_LOGS_MESSAGE));
            slowLogs.appendChild(slowLogsMessage);

            //Box containing the file selector and buttons
            Hbox slowLogsBox =  new Hbox();
            slowLogsBox.setStyle("margin-top:10px;margin-bottom:10px;margin-left:20px");
            slowLogsBox.setAlign("bottom");
            //Create combobox for file selector
            slowLog = getSlowLogsCombobox();
            slowLogsBox.appendChild(slowLog);
            //Download slow logs file button
            Div downloadSlowDiv = new Div();
            downloadSlowDiv.setTooltiptext(Labels.getLabel(DODConstants.LABEL_SLOW_LOGS_DOWNLOAD));
            Toolbarbutton downloadSlowBtn = new Toolbarbutton();
            downloadSlowBtn.setZclass(DODConstants.STYLE_BUTTON);
            downloadSlowBtn.setImage(DODConstants.IMG_DOWNLOAD);
            downloadSlowBtn.setParent(downloadSlowDiv);
            downloadSlowBtn.setTarget("slowLogIFrame");
            downloadSlowBtn.addEventListener(Events.ON_CLICK, new EventListener() {
                public void onEvent(Event event) {
                    //Check config file value
                    if (isSlowLogValid()) {
                        //Obtain file
                        String filePath = ((String)slowLog.getSelectedItem().getValue());
                        String url = fileHelper.getServedFileURL(instance, filePath);
                        if (url != null && !url.isEmpty()) {
                                Executions.sendRedirect(url);
                                slowLog.getFellow("filesWindow").detach();
                        }else {
                            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, "ERROR ON INSTANCE " + instance.getDbName() + " DOWNLOADING SLOW LOG: " + filePath);
                            showError(DODConstants.ERROR_DOWNLOADING_SLOW_LOG_FILE, null);
                        }
                    }
                    else{
                        slowLog.setErrorMessage(Labels.getLabel(DODConstants.ERROR_SLOW_LOG_FILE));
                    }
                }
            });
            slowLogsBox.appendChild(downloadSlowDiv);
            slowLogs.appendChild(slowLogsBox);
            mainBox.appendChild(slowLogs);
        }

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
        this.appendChild(buttonsDiv);
    }

    /**
     * Method executed when the user cancels the form. The window is detached from the page.
     */
    private void doCancel() {
        type.getFellow("filesWindow").detach();
    }

    /**
     * Get the different types of configuration files for a DB type.
     * @param dbType type of the DB to get the configuration file from.
     * @return a combobox with the different configuration files, and their paths.
     */
    private Combobox getConfigCombobox (String dbType) {
        Combobox toret =  new Combobox();
        toret.setReadonly(true);
        toret.setWidth("300px");
        if (dbType.equals(DODConstants.DB_TYPE_MYSQL)) {
            Comboitem myCfg = new Comboitem();
            myCfg.setLabel(Labels.getLabel(DODConstants.LABEL_CONFIG + DODConstants.CONFIG_FILE_MY_CNF));
            myCfg.setValue(new String[]{DODConstants.CONFIG_FILE_MY_CNF, DODConstants.CONFIG_PATH_MY_CNF});
            toret.appendChild(myCfg);
        }
        else {
            Comboitem noConfig = new Comboitem();
            noConfig.setLabel(Labels.getLabel(DODConstants.LABEL_NO_CONFIG_FILES));
            noConfig.setValue(null);
            toret.appendChild(noConfig);
        }
        toret.setSelectedIndex(0);
        return toret;
    }
    
    /**
     * Get the slow logs available for this instance.
     * @return a combobox with the different configuration files, and their paths.
     */
    private Combobox getSlowLogsCombobox () {
        slowLogArray = fileHelper.getSlowLogs(instance);
        Combobox toret =  new Combobox();
        toret.setReadonly(true);
        toret.setWidth("300px");
        if (slowLogArray != null) {
            for (int i=0; i < slowLogArray.length; i++) {
                Comboitem item = new Comboitem();
                item.setLabel(slowLogArray[i].substring(slowLogArray[i].lastIndexOf('/') + 1));
                item.setValue(slowLogArray[i]);
                toret.appendChild(item);
            }
        }
        else {
            Comboitem item = new Comboitem();
            item.setLabel(Labels.getLabel(DODConstants.LABEL_NO_SLOW_LOGS));
            item.setValue(null);
            toret.appendChild(item);
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
            if (instance.getDbType().equals(DODConstants.DB_TYPE_MYSQL)) {
                String fileType = ((String[])item.getValue())[0];
                if (!DODConstants.CONFIG_FILE_MY_CNF.equals(fileType))
                    return false;
                String filePath = ((String[])item.getValue())[1];
                if (!DODConstants.CONFIG_PATH_MY_CNF.equals(filePath))
                    return false;
            }
            if (instance.getDbType().equals(DODConstants.DB_TYPE_ORACLE)) {
                return false;
            }
            return true;
        }
        else
            return false;
    }
    
    /**
     * Checks if the selected slow log is valid.
     * @return true if the selected slow log is valid, false otherwise.
     */
    public boolean isSlowLogValid() {
        Comboitem item = slowLog.getSelectedItem();
        if (item != null && item.getValue() != null && item.getValue() instanceof String) {
            String filePath = (String)item.getValue();
            for (int i=0; i < slowLogArray.length; i++) {
                if (slowLogArray[i].equals(filePath))
                    return true;
            }
            return false;
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
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, "ERROR DISPATCHING JOB", exception);
        }
        Window errorWindow = (Window) this.getParent().getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        } catch (InterruptedException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        } catch (SuspendNotAllowedException ex) {
            Logger.getLogger(FileController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}
