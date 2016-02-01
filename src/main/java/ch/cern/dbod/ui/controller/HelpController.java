/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;

import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.*;

/**
 * Controller for the help view.
 * @author Jose Andres Cordero Benitez
 */
public class HelpController extends Vbox implements AfterCompose{

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    @Override
    public void afterCompose() {
        //Set the address of help documentation, for users and administrators.
        Iframe user_help = (Iframe) getFellowIfAny("user_help");
        Iframe admin_help = (Iframe) getFellowIfAny("admin_help");
        if (user_help != null)
            user_help.setSrc("https://dbod-user-guide.web.cern.ch/dbod-user-guide");
        
        if (admin_help != null)
            admin_help.setSrc("https://dbod-admin-guide.web.cern.ch/dbod-admin-guide");
    }
}