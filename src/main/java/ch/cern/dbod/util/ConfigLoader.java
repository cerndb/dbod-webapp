/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Reads the configuration file for the proxy server to AppDynamics.
 * @author Jose Andres Cordero Benitez
 */
public class ConfigLoader implements ServletContextListener {
    
    private static Properties properties;
    
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        reload(sce.getServletContext());
    }
    
    public static String getProperty(String name) {
        return properties.getProperty(name);
    }
    
    public static void reload(ServletContext sc) {
        properties = new Properties();
        String configPath = sc.getInitParameter(CommonConstants.CONFIG_LOCATION);
        try (InputStream input = new FileInputStream(configPath)) {
            properties.load(input);
        } catch (IOException ex) {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, "EXCEPTION READING CONFIG FILE ON " + configPath, ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to do
    }
}
