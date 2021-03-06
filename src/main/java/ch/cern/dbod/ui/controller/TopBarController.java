/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;

import ch.cern.dbod.util.ConfigLoader;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.EGroupHelper;
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
 * Controller for top bar's actions. It obtains some of the attributes that are
 * used in the top bar, and it renders the announcement banner when needed.
 * @author Daniel Gomez Blanco
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
        eGroups = execution.getHeader(CommonConstants.ADFS_GROUP);

        //Only show admin link if the user is admin
        admin = EGroupHelper.groupInList(ConfigLoader.getProperty(CommonConstants.ADMIN_E_GROUP), eGroups);
        
        //Get authenticated user's name
        username = execution.getHeader(CommonConstants.ADFS_LOGIN);
        fullName = execution.getHeader(CommonConstants.ADFS_FULLNAME);
    }
    
    @Override
    public void afterCompose() {
        //Get HTML for announcement
        String text = new String();
        String announcementPath = ConfigLoader.getProperty(CommonConstants.ANNOUNCEMENT_LOCATION);
        try {
            byte[] encoded = Files.readAllBytes(Paths.get(announcementPath));
            text = Charset.defaultCharset().decode(ByteBuffer.wrap(encoded)).toString().trim();
        }
        catch (IOException e){
            Logger.getLogger(TopBarController.class.getName()).log(Level.SEVERE, "ERROR READING ANNOUNCEMENT FILE ON " + announcementPath, e);
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
