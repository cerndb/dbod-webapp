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
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 * HttpConnection class to communicate with the AppDynamics application.
 * @author Jose Andres Cordero Benitez
 * June 2015
 */
public class HttpConnection {
    
    private static final Map<String, String> SESSIONS = new HashMap<>();
    
    // Save the last day of created sessions. When this value changes, all the sessions will be cleared.
    private static Integer last_session_day = null;
    
    /**
     * Check and clear all the created sessions when the day changes.
     */
    public static void checkSessions()
    {
        Date now = new Date();
        
        if (last_session_day == null)
            last_session_day = now.getDay();
        else if (last_session_day != now.getDay())
        {
            SESSIONS.clear();
            last_session_day = now.getDay();
        }
    }
    
    /**
     * Generates the final absolute URL using the relative received.
     * @param url
     * @return 
     */
    public static String generateUrlRelative(String url)
    {
        if (url == null || url.isEmpty())
            return null;
        
        // Security check to avoid relative URLs, eg: appdyn/../../www.cern.ch
        if (url.contains(".."))
            return null;
        
        // Filter url to avoid access no unauthorized places
        if (!url.contains("dbtuna") && !url.contains("reports") && !url.contains("shared"))
            return null;
        
        if (url.charAt(0) == '/')
            return ConfigLoader.getProperty(CommonConstants.APPDYN_HOST)+ url;
        else
            return ConfigLoader.getProperty(CommonConstants.APPDYN_HOST) + "/" + url;
    }
    
    /**
     * Generates the GET parameters string
     * @param request
     * @return 
     */
    public static String generateParametersString(HttpServletRequest request)
    {
        String params = null;
        Map<String, String[]> map = request.getParameterMap();
        for (String key : map.keySet())
        {
            String[] value = map.get(key);

            if (params == null)
                params = "?";
            else
                params += "&";

            params += key + "=" + value[0];
        }
        
        return params;
    }
    
    /**
     * Parse and replace the HTML/CSS/JS received code, removing the unwanted nodes and rewritting URLs.
     * @param contextPath
     * @param rd Input of the code
     * @param out Request output to write the response
     * @param mimetype Mimetype of the current code, to parse correctly
     * @throws IOException 
     */
    public static void parseAndReplaceCode(String contextPath, BufferedReader rd, PrintWriter out, String mimetype) throws IOException
    {
        String line;
        if (mimetype != null && (mimetype.endsWith("javascript") || mimetype.endsWith("css")))
        {
            while ((line = rd.readLine()) != null)
            {
                String pline = line.replace("../shared/", "/shared/");
                pline = pline.replace("/shared/", contextPath + "/appdyn/shared/");
                out.println(pline);
            }
        }
        else
        {
            boolean shorttopbar = false;
            boolean host_list = false;
            boolean deleting = false;
            boolean custom_view = false;
            boolean netapp_view = false;
            boolean reports = false;

            while ((line = rd.readLine()) != null)
            {
                if (deleting)
                {
                    // Check if we should finish deleting
                    if (!shorttopbar && line.equalsIgnoreCase("</div>"))
                    {
                        shorttopbar = true;
                        deleting = false;
                        continue;
                    }

                    if (!host_list && (line.equalsIgnoreCase("</li></ul>") || line.equalsIgnoreCase("</select>")))
                    {
                        host_list = true;
                        deleting = false;
                        continue;
                    }
                    
                    if (!custom_view && line.equalsIgnoreCase("</li></ul>"))
                    {
                        custom_view = true;
                        deleting = false;
                        continue;
                    }
                    
                    if (!reports && line.equalsIgnoreCase("</select>"))
                    {
                        reports = true;
                        deleting = false;
                        continue;
                    }

                    continue;
                }
                else
                {
                    // Check if we should start deleting
                    if (!shorttopbar && line.equalsIgnoreCase("<div class=\"shorttopbar\">"))
                    {
                        deleting = true;
                        continue;
                    }

                    if (!host_list && line.startsWith("<a href=\"#\">Host:&nbsp;", 25))
                    {
                        deleting = true;
                        continue;
                    }
                    
                    if (!host_list)
                    {
                        // Ignore this line
                        if (line.equals("<td class=\"darkLabel\">Database</td>"))
                            line = "<td class=\"darkLabel\"></td>";
                        else if (line.startsWith("<select name=\"host\""))
                        {
                            line = line.replace("<select name=\"host\"", "<select name=\"host\" hidden");
                        }
                    }
                    
                    if (!custom_view && line.startsWith("<a href=\"#\">Custom&nbsp;", 25))
                    {
                        deleting = true;
                        continue;
                    }
                    
                    if (!netapp_view && line.startsWith("\t<td class=\"sidebarheader\">"))
                    {
                        // Modify this line
                        line = "\t<td class=\"sidebarheader\">In MYSQL Over Time </td>";
                    }
                    
                    if (!reports && line.startsWith("<b>Other Reports:</b>"))
                    {
                        deleting = true;
                        continue;
                    }
                }

                // Do the parsing to change all the links
                String pline = line.replace("href=\"/", "href=\"" + contextPath + "/appdyn/");
                pline = pline.replace("value=\"/reports", "value=\"" + contextPath + "/appdyn/reports");
                pline = pline.replace("src=\"/", "src=\"" + contextPath + "/appdyn/");
                pline = pline.replace("src=\\\"/", "src=\\\"" + contextPath + "/appdyn/");
                pline = pline.replace("SRC=\"/", "SRC=\"" + contextPath + "/appdyn/");
                pline = pline.replace("SRC=\\\"/", "SRC=\\\"" + contextPath + "/appdyn/");
                pline = pline.replace("url(/", "url(" + contextPath + "/appdyn/");
                pline = pline.replace("window.open( '", "window.open( '" + contextPath + "/appdyn");
                if (pline.contains("appdyn/alerts/"))
                    continue;
                out.println(pline);
            }
        }
    }
    
    /**
     * GET request using cookies. Used for main and jsp webpages.
     * @param url
     * @param mimetype
     * @param request
     * @param response 
     */
    public static void doGetUsingCookies(String url, String mimetype, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            // Check if the sessions should be cleared.
            checkSessions();
            
            // Get sessionid from received Cookies
            String appdynid = ProxyUtils.getSessionFromCookies(request);
            
            HttpGet httprequest = new HttpGet(url);
            
            // Do all the security checks
            int token_result = ProxyUtils.checkSecurityToken(request, appdynid, httprequest, SESSIONS);
            switch(token_result)
            {
                case ProxyUtils.TOKEN_STATUS_FAIL:
                    response.sendError(403);
                    return;
                case ProxyUtils.TOKEN_STATUS_NEW:
                    appdynid = null;
            }
            
            // Add authentication headers
            httprequest.addHeader("Authorization", ConfigLoader.getProperty(CommonConstants.APPDYN_AUTH_STRING));

            // Execute the request
            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse httpresponse = client.execute(httprequest);
            
            response.setContentType("text/html;charset=UTF-8");
            if (mimetype == null)
            {
                // Get all the cookies and save them
                if (appdynid == null)
                    ProxyUtils.getAndProcessCookies(httpresponse, response, SESSIONS);
            }
            
            // Get the reader of the received page and the writer for the output
            try (BufferedReader rd = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()))) {
                PrintWriter out = response.getWriter();
                
                // Parse and do the needed replaces in the received code
                parseAndReplaceCode(request.getContextPath(), rd, out, mimetype);
            }
        }
        catch(IOException | IllegalStateException ex)
        {
            Logger.getLogger(HttpConnection.class.getName()).log(Level.SEVERE, "UNCAUGHT EXCEPTION", ex);
        }
    }
    
    /**
     * Do a normal GET Request, usually to get images and resource files.
     * @param url
     * @param mimetype
     * @param request
     * @param response 
     */
    public static void doGet(String url, String mimetype, HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            response.setContentType(mimetype);
            
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httprequest = new HttpGet(url);
            
            // Add authentication headers
            httprequest.addHeader("Authorization", ConfigLoader.getProperty(CommonConstants.APPDYN_AUTH_STRING));

            // Execute the request
            HttpResponse httpresponse = client.execute(httprequest);
            
            // Get the reader of the received page and the writer for the output
            BufferedReader rd = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
            PrintWriter out = response.getWriter();
            
            // Parse and do the needed replaces in the received code
            parseAndReplaceCode(request.getContextPath(), rd, out, mimetype);
        }
        catch(IOException | IllegalStateException ex)
        {
            Logger.getLogger(HttpConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reads the url and resends it to the user, treated as binary data.
     * @param url URL to load
     * @param mimetype
     * @param response Response of the Servlet
     */
    public static void doGetStream(String url, String mimetype, HttpServletResponse response) {
        try {
            HttpClient client = HttpClientBuilder.create().build();
            HttpGet httprequest = new HttpGet(url);
            
            response.setContentType(mimetype);

            // Add authentication headers
            httprequest.addHeader("Authorization", ConfigLoader.getProperty(CommonConstants.APPDYN_AUTH_STRING));
            HttpResponse httpresponse = client.execute(httprequest);

            // Read the resource and resend it
            try (BufferedInputStream bis = new BufferedInputStream(httpresponse.getEntity().getContent());
                 OutputStream outstr = response.getOutputStream()) {
                for (int data; (data = bis.read()) > -1;)
                    outstr.write(data);
            }
        }
        catch(IOException | IllegalStateException ex) {
            Logger.getLogger(HttpConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Do a POST request.
     * @param url
     * @param request
     * @param response 
     */
    public static void doPostStream(String url, HttpServletRequest request, HttpServletResponse response)
    {
        response.setContentType("text/html;charset=UTF-8");
        
        try
        {
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost httprequest = new HttpPost(url);

            // Add authentication headers
            httprequest.addHeader("Authorization", ConfigLoader.getProperty(CommonConstants.APPDYN_AUTH_STRING));
            httprequest.addHeader("Cookie", "anycookie=1;");
            
            // Add the POST parameters
            ArrayList<NameValuePair> postParameters = new ArrayList<>();
            for (String paramName : request.getParameterMap().keySet())
            {
                String paramValue = request.getParameter(paramName);
                if (paramValue != null)
                    postParameters.add(new BasicNameValuePair(paramName, paramValue));
            }

            httprequest.setEntity(new UrlEncodedFormEntity(postParameters));

            // Execute the request
            HttpResponse httpresponse = client.execute(httprequest);
            
            BufferedReader rd = new BufferedReader(new InputStreamReader(httpresponse.getEntity().getContent()));
            
            PrintWriter out = response.getWriter();
            String line;
            while ((line = rd.readLine()) != null)
            {
                out.println(line);
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(HttpConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
