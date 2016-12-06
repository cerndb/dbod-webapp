/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.appservlet;

import ch.cern.dbod.util.CommonConstants;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.zkoss.zk.ui.Sessions;

/**
 * Reads the configuration file for the proxy server to AppDynamics.
 * @author Jose Andres Cordero Benitez
 */
public class ConfigLoader {
    
    private static final Properties propertiesFile = init();
    
    public static String getProperty(String name) {
        return propertiesFile.getProperty(name);
    }
    
    private static Properties init()
    {
        Properties prop = new Properties();
        String configPath = ((ServletContext)Sessions.getCurrent().getWebApp().getServletContext()).getInitParameter(CommonConstants.CONFIG_LOCATION);
        try (InputStream input = new FileInputStream(configPath))
        {
            prop.load(input);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, "EXCEPTION READING CONFIG FILE ON " + configPath, ex);
        }
        return prop;
    }
}
