/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;


import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.ConfigLoader;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zul.*;
import org.zkoss.zk.ui.Sessions;

/**
 * Controller for the admin config view.
 * @author Jose Andres Cordero Benitez
 */
public class ConfigController extends Vbox implements AfterCompose{

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the instances obtained before composing.
     */
    @Override
    public void afterCompose() {
        String configPath = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(CommonConstants.CONFIG_LOCATION);
        try (BufferedReader br = new BufferedReader(new FileReader(configPath))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            ((Textbox) getFellow("configEditor")).setValue(sb.toString());
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, "EXCEPTION READING CONFIG FILE ON " + configPath, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, "EXCEPTION READING CONFIG FILE ON " + configPath, ex);
        }
    }
    
    public void saveFile() {
        String configPath = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(CommonConstants.CONFIG_LOCATION);
        try (PrintWriter out = new PrintWriter(configPath)) {
            String content = ((Textbox) getFellow("configEditor")).getValue();
            out.print(content);
            Label msg = ((Label) getFellow("configurationMsg"));
            msg.setStyle("font-weight:bold;color:green");
            msg.setValue(Labels.getLabel(CommonConstants.LABEL_CONFIG_FILE_SAVED));
            msg.setVisible(true);
        }
        catch (FileNotFoundException ex) {
            Label msg = ((Label) getFellow("configurationMsg"));
            msg.setStyle("font-weight:bold;color:red");
            msg.setValue(Labels.getLabel(CommonConstants.LABEL_CONFIG_FILE_ERROR));
            msg.setVisible(true);
            Logger.getLogger(ConfigController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void refreshConfig() {
        ConfigLoader.reload();
        Label msg = ((Label) getFellow("configurationMsg"));
        msg.setStyle("font-weight:bold;color:green");
        msg.setValue(Labels.getLabel(CommonConstants.LABEL_CONFIG_FILE_RELOAD));
        msg.setVisible(true);
    }
}