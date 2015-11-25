/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */
package ch.cern.dbod.db.dao;

import java.io.*;
import java.security.NoSuchAlgorithmException;
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
public class InstanceREST {
    
    /*HttpPost p;
    HttpDelete d;
    HttpGet g;
    HttpPut u;*/
    
    public static String selectByDbName(String db_name)
    {
        try {
            // Do not do this in production!!!
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(SSLContext.getDefault(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpClient httpclient = HttpClientBuilder.create().setSSLSocketFactory(sslsf).build();
            
            HttpGet request = new HttpGet("https://dbodtests.cern.ch:5443/api/v1/entity/" + db_name);
            HttpResponse response = httpclient.execute(request);
            if (response.getStatusLine().getStatusCode() == 200)
            {
                String json = EntityUtils.toString(response.getEntity());
                System.out.println("==== RESPONSE ====");
                System.out.println(json);
                return json;
            }

        } catch (NoSuchAlgorithmException | IOException | ParseException e) {
            System.out.println("\nError while calling Crunchify REST Service");
            System.out.println(e);
        }
        
        return null;
    }
}
