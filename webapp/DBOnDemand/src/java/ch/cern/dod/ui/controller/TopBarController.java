package ch.cern.dod.ui.controller;

import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;

/**
 * Controller for top bar's actions.
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class TopBarController extends Div implements BeforeCompose, AfterCompose{
    
    /**
     * e-groups this user belongs to
     */
    private String eGroups;
    /**
     * Indicates if the user is admin
     */
    private boolean admin;
    /**
     * Userame logged in
     */
    private String username;
    
    /**
     * Full name logged in
     */
    private String fullName;

    @Override
    public void beforeCompose() {
        //Get username and groups
        Execution execution = Executions.getCurrent();
        eGroups = execution.getHeader(DODConstants.ADFS_GROUP);

        //Only show admin link if the user is admin
        admin = EGroupHelper.groupInList(DODConstants.ADMIN_E_GROUP, eGroups);
        
        //Get authenticated user's name
        username = execution.getHeader(DODConstants.ADFS_LOGIN);
        fullName = execution.getHeader(DODConstants.ADFS_FULLNAME);
    }
    
    @Override
    public void afterCompose() {
        //Get HTML for announcement
        String text = new String();
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(DODConstants.ANNOUNCEMENT_FILE));
            text = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString().trim();
        }
        catch (IOException e){
            Logger.getLogger(TopBarController.class.getName()).log(Level.SEVERE, "ERROR READING ANNOUNCEMENT FILE ON " + DODConstants.ANNOUNCEMENT_FILE, e);
        }
        
        if (!text.isEmpty()) {
            Html announcement = (Html) getFellow("announcement");
            announcement.setContent(text);
            ((Div) getFellow("announcementDiv")).setVisible(true);
        }
    }

    public boolean isAdmin() {
        return admin;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }
}
