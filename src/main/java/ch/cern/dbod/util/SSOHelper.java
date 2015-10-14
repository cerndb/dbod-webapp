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
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * This class contains methods to deal with resources behind the CERN SSO
 *
 * @author Daniel Gomez Blanco
 */
public class SSOHelper {    
    /**
     * Scheme registry to be used when accessing HTTPS resources
     */
    private SchemeRegistry schemeRegistry;

    /**
     * Constructs the Scheme Registry to be used for all the HTTP clients
     * @param keyStorePath path to key store in PKCS12 format
     * @param keyStorePassword password to key store
     * @param trustStorePath path to trust store in JKS format
     * @param trustStorePassword password to trust store
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException 
     */
    public SSOHelper(String keyStorePath, String keyStorePassword, String trustStorePath, String trustStorePassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {        
        //Needed since Java 7 (this a server issue, not on our side)
        System.setProperty("jsse.enableSNIExtension", "false");

        //Load the KeyStore containing the client certificate
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream keyStoreInput = new FileInputStream(keyStorePath);
        keyStore.load(keyStoreInput, keyStorePassword.toCharArray());

        //Load the Trustore, leave it null to rely on cacerts distributed with the JVM
        KeyStore trustStore = null;
        if (trustStorePath != null && trustStorePassword != null) {
            trustStore = KeyStore.getInstance("JKS");
            InputStream trustStoreInput = new FileInputStream(trustStorePath);
            trustStore.load(trustStoreInput, trustStorePassword.toCharArray());
        }

        //Create SchemeRegistry for HTTPS
        schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("https", new SSLSocketFactory(keyStore, keyStorePassword, trustStore), 443));
    }
    
    /**
     * Constructs the Scheme Registry to be used for all the HTTP clients
     * @param keyStorePath path to key store in PKCS12 format
     * @param keyStorePassword password to key store
     * @throws KeyStoreException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException 
     */
    public SSOHelper(String keyStorePath, String keyStorePassword)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, KeyManagementException, UnrecoverableKeyException {
        this(keyStorePath, keyStorePassword, null, null);
    }

    /**
     * Gets the SSO cookie for specific host
     * @param host hostname to get the cookie for
     * @return SSO cookie
     * @throws IOException 
     */
    public Cookie getSSOCookie(String host) throws IOException {

        //Create HTTPS client
        HttpParams httpParams = new BasicHttpParams();
        try (DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schemeRegistry), httpParams)) {
            
            //Check if cookie is already there
            Cookie cookie = getShibbolethCookie(httpClient.getCookieStore());
            if (cookie != null) {
                return cookie;
            }
            
            //Build GET request to CERN login site with certificate
            HttpGet getRequest = new HttpGet("https://login.cern.ch/adfs/ls/auth/sslclient/?wa=wsignin1.0&wreply=https%3A%2F%2F"
                    + host + "%2FShibboleth.sso%2FADFS&wtrealm=https%3A%2F%2F" + host + "%2FShibboleth.sso%2FADFS");
            
            //Send GET request
            try (CloseableHttpResponse getResponse = httpClient.execute(getRequest)) {
                
                //Check if cookie is already there
                cookie = getShibbolethCookie(httpClient.getCookieStore());
                if (cookie != null) {
                    return cookie;
                }
                
                //Get content. If the authentication is succesful, this a form with the SAML request to send to the Shibboleth
                HttpEntity getEntity = getResponse.getEntity();
                StringWriter getWriter = new StringWriter();
                IOUtils.copy(getEntity.getContent(), getWriter, "UTF-8");
                String getContent = getWriter.toString();
                
                //Make sure the entity is properly consumed
                EntityUtils.consume(getEntity);
                
                //Extract the form data (wa attribute is constant)
                String wa = "wsignin1.0";
                String wresultPlaceholder = "<input type=\"hidden\" name=\"wresult\" value=\"";
                String wresult = getContent.substring(getContent.indexOf(wresultPlaceholder) + wresultPlaceholder.length(), getContent.indexOf("\"", getContent.indexOf(wresultPlaceholder)+ wresultPlaceholder.length()));
                
                //Unescape the HTML string
                wresult = StringEscapeUtils.unescapeHtml3(wresult);
                
                //Build parameter list
                List<NameValuePair> paramList = new ArrayList<>();
                paramList.add(new BasicNameValuePair("wa", wa));
                paramList.add(new BasicNameValuePair("wresult", wresult));
                
                //Build request for Shibboleth
                HttpPost postRequest = new HttpPost("https://" + host + "/Shibboleth.sso/ADFS");
                postRequest.setEntity(new UrlEncodedFormEntity(paramList));
                
                //Send POST request to Shibboleth to get the cookie
                try (CloseableHttpResponse postResponse = httpClient.execute(postRequest)) {
                    //Get Shibboleth session cookie
                    cookie = getShibbolethCookie(httpClient.getCookieStore());
                    
                    //Make sure the entity is completely consumed
                    EntityUtils.consume(postResponse.getEntity());
                    
                    return cookie;
                }
            }
        }
    }
    
    /**
     * Iterates through a cookie store and locate the Shibboleth cookie
     * @param cookieStore cookie store to iterate through
     * @return Shibboleth cookie
     */
    private Cookie getShibbolethCookie (CookieStore cookieStore) {
        Iterator<Cookie> iter = cookieStore.getCookies().iterator();
        while (iter.hasNext()) {
            Cookie cookie = iter.next();
            if (cookie.getName().startsWith("_shibsession_")) {
                return cookie;
            }
        }
        return null;
    }
}