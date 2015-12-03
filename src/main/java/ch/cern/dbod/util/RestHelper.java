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
import ch.cern.dbod.db.entity.Instance;
import com.google.gson.*;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.*;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author Jose Andres Cordero Benitez
 */
public class RestHelper {
    
    private static Gson init()
    {
        Gson gson = new GsonBuilder()
                    .registerTypeAdapter(boolean.class, new BooleanSerializer())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();
            
        return gson;
    }
    
    public static JsonElement parseObject(String json)
    {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }
    
    public static JsonElement parseList(String json)
    {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonArray();
    }
    
    public static <T> T getObjectFromRestApi(String path, Class<T> object)
    {
        Gson gson = init();
        
        try {
            // Do not do this in production!!!
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpClient httpclient = HttpClientBuilder.create().setSSLSocketFactory(sslsf).build();
            
            HttpGet request = new HttpGet(ConfigLoader.getRestApiPath() + path);
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String resp = EntityUtils.toString(response.getEntity());
                JsonElement json = parseObject(resp);
                
                T result = gson.fromJson(json, object);
                return result;
            }

        } catch (NoSuchAlgorithmException | IOException | ParseException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return null;
    }
    
    public static <T> T getObjectListFromRestApi(String path, Class<T> object)
    {
        Gson gson = init();
        
        try {
            // Do not do this in production!!!
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpClient httpclient = HttpClientBuilder.create().setSSLSocketFactory(sslsf).build();
            
            HttpGet request = new HttpGet(ConfigLoader.getRestApiPath() + path);
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String resp = EntityUtils.toString(response.getEntity());
                JsonElement json = parseList(resp);
                
                T result = gson.fromJson(json, object);
                return result;
            }

        } catch (NoSuchAlgorithmException | IOException | ParseException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return null;
    }
}