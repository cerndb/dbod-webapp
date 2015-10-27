/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.appservlet;

import static ch.cern.dbod.util.CommonConstants.CONFIG_LOCATION;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reads the configuration file for the proxy server to AppDynamics.
 * @author Jose Andres Cordero Benitez
 */
public class ConfigLoader {
    
    private static final Properties propertiesFile = init();
    
    public static String getProxyPassword()
    {
        return propertiesFile.getProperty("token_password");
    }
    
    public static String getAppDynamicAuth()
    {
        return propertiesFile.getProperty("appdyn_auth");
    }
    
    public static String getAppDynHost()
    {
        return propertiesFile.getProperty("appdyn_host");
    }
    
    public static String getKibanaDashboard()
    {
        return propertiesFile.getProperty("kibana_dashboard");
    }
    
    public static String getDBTunaPath()
    {
        return propertiesFile.getProperty("appdyn_dbtuna_path");
    }
    
    public static String getDBTuna4PgPath()
    {
        return propertiesFile.getProperty("appdynn_dbtuna4pg_path");
    }
    
    private static Properties init()
    {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream(CONFIG_LOCATION))
        {
            prop.load(input);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ConfigLoader.class.getName()).log(Level.SEVERE, "UNCAUGHT EXCEPTION", ex);
        }
        return prop;
    }
}
