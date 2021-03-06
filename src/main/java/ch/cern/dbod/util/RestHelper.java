/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */
package ch.cern.dbod.util;

import com.google.gson.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * @author Jose Andres Cordero Benitez
 */
public class RestHelper {
    
    private static Gson init() {
        BooleanSerializer serializer = new BooleanSerializer();
        Gson gson = new GsonBuilder()
                    .registerTypeAdapter(boolean.class, serializer)
                    .registerTypeAdapter(Boolean.class, serializer)
                    .addSerializationExclusionStrategy(new AnnotationExclusionStrategySerialization())
                    .addDeserializationExclusionStrategy(new AnnotationExclusionStrategyDeserialization())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    .create();
        
        return gson;
    }
    
    private static void addAuthorizationHeader(HttpRequest request) {
        String encode = ConfigLoader.getProperty(CommonConstants.DBOD_API_USER) + ":" + ConfigLoader.getProperty(CommonConstants.DBOD_API_PASS);
        byte[] encodedBytes = Base64.encodeBase64(encode.getBytes());
        request.addHeader("Authorization", "Basic " + new String(encodedBytes));
    }
    
    public static JsonObject parseObject(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonObject();
    }
    
    public static JsonArray parseList(String json) {
        JsonParser parser = new JsonParser();
        return parser.parse(json).getAsJsonArray();
    }
    
    public static <T> T getObjectFromRestApi(String path, Class<T> object, String resName) throws IOException, ParseException, IllegalStateException {
        Gson gson = init();
        
        HttpClient httpclient = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
        addAuthorizationHeader(request);
        
        HttpResponse response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() == 200)
        {
            String resp = EntityUtils.toString(response.getEntity());
            JsonObject json = parseObject(resp).getAsJsonArray(resName).get(0).getAsJsonObject();
            EntityUtils.consume(response.getEntity());

            T result = gson.fromJson(json, object);
            return result;
        }
        
        return null;
    }
    
    public static JsonObject getJsonObjectFromRestApi(String path) throws IOException, ParseException, IllegalStateException {
        HttpClient httpclient = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
        addAuthorizationHeader(request);

        HttpResponse response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() == 200)
        {
            String resp = EntityUtils.toString(response.getEntity());
            JsonObject json = parseObject(resp).getAsJsonObject();
            EntityUtils.consume(response.getEntity());

            return json;
        }
        
        return null;
    }
    
    public static <T> ArrayList<T> getObjectListFromRestApi(String path, HashMap<String, String> params, Class<T> object, String auth, String response) throws IOException, ParseException, IllegalStateException {
        Gson gson = init();
        ArrayList<T> objectList = new ArrayList<>();
        
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            URIBuilder builder = new URIBuilder(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
            if (params != null) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    builder.setParameter(param.getKey(), param.getValue());
                }
            }

            HttpGet request = new HttpGet(builder.build());
            addAuthorizationHeader(request);
            if (auth != null) {
                request.addHeader("auth", auth);
            }

            HttpResponse resp = httpclient.execute(request);
            if (resp.getStatusLine().getStatusCode() == 200)
            {
                String respStr = EntityUtils.toString(resp.getEntity());
                EntityUtils.consume(resp.getEntity());
                JsonArray jList;
                if (response != null) {
                    jList = parseObject(respStr).getAsJsonArray(response);
                } else {
                    jList = parseList(respStr).getAsJsonArray();
                }

                Iterator<JsonElement> itr = jList.iterator();
                while (itr.hasNext()) {
                    JsonObject jItem = itr.next().getAsJsonObject();
                    T item = gson.fromJson(jItem, object);
                    objectList.add(item);
                }
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return objectList;
    }

    public static String getValueFromRestApi(String path) throws IOException, ParseException {
        HttpClient httpclient = HttpClientBuilder.create().build();

        HttpGet request = new HttpGet(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
        addAuthorizationHeader(request);

        HttpResponse response = httpclient.execute(request);
        if (response.getStatusLine().getStatusCode() == 200)
        {
            String value = EntityUtils.toString(response.getEntity());
            EntityUtils.consume(response.getEntity());
            return value;
        }
        
        return null;
    }
    
    public static boolean putValueToRestApi(String value, String path) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            
            HttpPut request = new HttpPut(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
            addAuthorizationHeader(request);
            
            StringEntity jsonData = new StringEntity(value, "UTF-8");
            /* Body of request */
            request.setEntity(jsonData);
            
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return true;
            } else {
                Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, "API Returned error code: {0}", response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
    }
    
    public static String runRundeckJob(String job, String instance) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            
            HttpPost request = new HttpPost(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + "api/v1/rundeck/job/" + job + "/" + instance);
            addAuthorizationHeader(request);
            
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String resp = EntityUtils.toString(response.getEntity());
                String result = parseObject(resp).getAsJsonObject("response").getAsJsonArray("entries").get(0).getAsJsonObject().get("log").getAsString();
                EntityUtils.consume(response.getEntity());
                return result;
            } else {
                Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, "API Returned error code: {0}", response.getStatusLine().getStatusCode());
            }
        } catch (IOException | ParseException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return null;
    }
    
    public static boolean putJsonToRestApi(JsonElement json, String path) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            
            HttpPut request = new HttpPut(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
            addAuthorizationHeader(request);

            StringEntity jsonData = new StringEntity(json.toString(), "UTF-8");
            /* Body of request */
            request.setEntity(jsonData);
            
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return true;
            } else {
                Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, "API Returned error code: {0}", response.getStatusLine().getStatusCode());
            }
        } catch (IOException | ParseException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
    }
    
    public static boolean postJsonToRestApi(JsonElement json, String path) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            
            HttpPost request = new HttpPost(ConfigLoader.getProperty(CommonConstants.DBOD_API_LOCATION) + path);
            addAuthorizationHeader(request);

            StringEntity jsonData = new StringEntity(json.toString(), "UTF-8");
            /* Body of request */
            request.setEntity(jsonData);
            
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 204) {
                return true;
            } else {
                Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, "API Returned error code: {0}", response.getStatusLine().getStatusCode());
            }
        } catch (IOException | ParseException e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        } catch (Exception e) {
            Logger.getLogger(RestHelper.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return false;
    }
    
    public static <T> T fromJson(JsonObject json, Class<T> object) {
        Gson gson = init();
        T result = gson.fromJson(json, object);
        return result;
    }
    
    public static String toJson(Object object) {
        Gson gson = init();
        return gson.toJson(object);
    }
}
