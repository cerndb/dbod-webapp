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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

/**
 * Some utils and dependencies used for the internal proxy.
 * @author Jose Andres Cordero Benitez
 */
public class ProxyUtils {
    
    public static final int TOKEN_STATUS_OK = 0;
    public static final int TOKEN_STATUS_FAIL = 1;
    public static final int TOKEN_STATUS_NEW = 2;
    
    protected static void getAndProcessCookies(HttpResponse httpresponse, HttpServletResponse response, Map<String, String> sessions)
    {
        String sessionid = null;
        
        Pattern patternSessionID = Pattern.compile("^JSESSIONID=([^;]+)");
        Pattern patternMySQL = Pattern.compile("^MYSQL_host=([^;]+)");
        Pattern patternPgSql = Pattern.compile("^POSTGRES_host=([^;]+)");

        String mysql_host = null;
        String pg_host = null;
        String set_cookie = null;

        // Get all the cookies sent by AppDynamics.
        Header[] headers = httpresponse.getHeaders("Set-Cookie");
        for (Header header : headers)
        {
            Matcher matcherSessionID = patternSessionID.matcher(header.getValue());
            if (matcherSessionID.find())
            {
                sessionid = matcherSessionID.group(1);
                // The JSESSIONID has to be sent to the user to trace the request.
                // However, the name is changed to APPDYNID, to avoid collisions with the JSESSIONID created by Java.
                set_cookie = header.getValue().replace("JSESSIONID", "APPDYNID");
            }

            Matcher matcherMySQL = patternMySQL.matcher(header.getValue());
            if (matcherMySQL.find())
                mysql_host = matcherMySQL.group(1);

            Matcher matcherPgSql = patternPgSql.matcher(header.getValue());
            if (matcherPgSql.find())
                pg_host = matcherPgSql.group(1);
        }

        // Send the cookie to the client.
        response.setHeader("Set-Cookie", set_cookie);

        // Build the cookie and save in our sessions map.
        StringBuilder cookieString = new StringBuilder();
        cookieString.append("JSESSIONID=").append(sessionid).append(";");

        if (mysql_host != null)
            cookieString.append("MYSQL_host=").append(mysql_host).append(";");

        if (pg_host != null)
            cookieString.append("POSTGRES_host=").append(pg_host).append(";");

        sessions.put(sessionid, cookieString.toString());
    }
    
    /**
     * Get the APPDYNID Cookie.
     * @param request
     * @return APPDYNID
     */
    public static String getSessionFromCookies(HttpServletRequest request)
    {
        String appdynid = null;
        
        // Get cookies, if jsessionid arrived I need to proxy it to the server.
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (Cookie c : cookies)
            {
                if (c.getName().equals("APPDYNID"))
                    appdynid = c.getValue();
            }
        }
        
        return appdynid;
    }

    protected static int checkSecurityToken(HttpServletRequest request, String appdynid, HttpGet httprequest, Map<String, String> sessions)
    {
        String sec_token = request.getParameter("sec_token");
        String host = request.getParameter("host");
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String date = dateFormat.format(new Date());
        
        // Add cookies if this session already exists
        if (appdynid != null)
        {
            // Check if I have received a security token to change the host
            // This is needed to replace the cookie if the user has changed to another instance
            if (sessions.containsKey(appdynid))
            {
                // The token is valid, so the request is processed
                if (host != null && sec_token != null && sec_token.equalsIgnoreCase(DigestUtils.sha256Hex(ConfigLoader.getProperty(CommonConstants.APPDYN_TOKEN) + ":" + host + ":" + date)))
                {
                    // Don't send the cookie this time, so a new session and cookie will be created
                    httprequest.addHeader("Cookie", "anycookie=1;");
                    return TOKEN_STATUS_NEW;
                }
                else
                {
                    String sessionhost = sessions.get(appdynid);
                    
                    // The user is trying to change the host without a valid token
                    if (host != null && !sessionhost.endsWith("_host=" + host + ";"))
                        return TOKEN_STATUS_FAIL;
                        
                    // Send our internal data
                    httprequest.addHeader("Cookie", sessionhost);
                    return TOKEN_STATUS_OK;
                }
            }
            else
            {
                // The session is not saved in our map, check if the token is valid
                if (host != null && sec_token != null && sec_token.equalsIgnoreCase(DigestUtils.sha256Hex(ConfigLoader.getProperty(CommonConstants.APPDYN_TOKEN) + ":" + host + ":" + date)))
                {
                    // Don't send the cookie this time, so a new session and cookie will be created
                    httprequest.addHeader("Cookie", "anycookie=1;");
                    return TOKEN_STATUS_NEW;
                }
                else
                {
                    // The token is invalid, or not provided
                    return TOKEN_STATUS_FAIL;
                }
            }
        }
        else 
        {
            // The token is valid, so the request is processed
            if (host != null && sec_token != null && sec_token.equalsIgnoreCase(DigestUtils.sha256Hex(ConfigLoader.getProperty(CommonConstants.APPDYN_TOKEN) + ":" + host + ":" + date)))
            {
                // Don't send the cookie this time, so a new session and cookie will be created
                httprequest.addHeader("Cookie", "anycookie=1;");
                return TOKEN_STATUS_NEW;
            }
            else
            {
                return TOKEN_STATUS_FAIL;
            }
        }
    }
}
