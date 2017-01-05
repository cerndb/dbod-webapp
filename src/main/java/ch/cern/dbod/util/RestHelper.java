/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */
package ch.cern.dbod.util;

import ch.cern.dbod.appservlet.ConfigLoader;
import com.google.gson.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author Jose Andres Cordero Benitez
 */
public class RestHelper {
    
    private static Gson init() {
        Gson gson = new GsonBuilder()
                    .registerTypeAdapter(boolean.class, new BooleanSerializer())
                    .setDateFormat("yyyy-MM-dd")
                    .create();
        
        return gson;
    }
    
    private static JsonObject parseObject(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }
    
    private static JsonArray parseList(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonArray();
    }
    
    public static <T> T getObjectFromRestApi(String path, Class<T> object, String resName) {
        Gson gson = init();
        
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            
            HttpGet request = new HttpGet(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String resp = EntityUtils.toString(response.getEntity());
                JsonObject json = parseObject(resp).getAsJsonObject().get(resName).getAsJsonArray().get(0).getAsJsonObject();
                EntityUtils.consume(response.getEntity());
                
                T result = gson.fromJson(json, object);
                return result;
            }
        } catch (IOException | ParseException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return null;
    }
    
    public static <T> T getObjectListFromRestApi(String path, Class<T> object) {
        Gson gson = init();
        
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            
            HttpGet request = new HttpGet(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String resp = EntityUtils.toString(response.getEntity());
                JsonElement json = parseList(resp);
                
                T result = gson.fromJson(json, object);
                EntityUtils.consume(response.getEntity());
                return result;
            }
        } catch (IOException | ParseException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return null;
    }
    
    public static String getValueFromRestApi(String path) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            
            HttpGet request = new HttpGet(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String value = EntityUtils.toString(response.getEntity());
                EntityUtils.consume(response.getEntity());
                return value;
            }
        } catch (IOException | ParseException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return null;
    }
}
