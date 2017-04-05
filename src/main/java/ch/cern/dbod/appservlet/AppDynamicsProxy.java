/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.appservlet;

import ch.cern.dbod.appservlet.handler.AppDynamicsHandler;
import ch.cern.dbod.db.dao.ActivityDAO;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.URI;

/**
 * Servlet to be used as an internal proxy between the user and AppDynamics application.
 * @author Jose Andres Cordero Benitez
 * June 2015
 */
public class AppDynamicsProxy extends HttpServlet {
    
    private ActivityDAO activityDAO;
    
    public AppDynamicsProxy() {
        activityDAO = new ActivityDAO();
    }

    /**
     * Handle a normal GET request.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        try
        {
            // Generate the full URL, relative to AppDynamics
            String pathInfo = request.getPathInfo();
            String url = AppDynamicsHandler.generateUrlRelative(pathInfo);

            // Activity logging
            String instance = request.getParameter("host");
            String sec_token = request.getParameter("sec_token");
            if (sec_token != null)
                activityDAO.insert("-Proxy-", null, "APPDYNAMICS", "Access to instance " + instance);
            
            if (url == null)
            {
                response.sendError(404);
                return;
            }
            
            // Get and resend all the parameters in the request
            String params = AppDynamicsHandler.generateParametersString(request);
            if (params != null)
                url += params;
            
            // Create an URL object to parse, encode an verify the URL provided. If there is an error an exception will be thrown in the creation.
            URL urlc = new URL(url);
            URI uric = new URI(urlc.getProtocol(), urlc.getUserInfo(), urlc.getHost(), urlc.getPort(), urlc.getPath(), urlc.getQuery(), urlc.getRef());
            url = uric.toString();
            
            // In function the mimetype the request will be treated as binary or text
            ServletContext cntx = getServletContext();
            String mimetype = cntx.getMimeType(url);
            
            // Do the request and proxy it
            if (mimetype == null)
                AppDynamicsHandler.doGetUsingCookies(url, mimetype, request, response);
            else if (mimetype.startsWith("text"))
                AppDynamicsHandler.doGet(url, mimetype, request, response);
            else
                AppDynamicsHandler.doGetStream(url, mimetype, response);
        }
        catch (IOException ex)
        {
            Logger.getLogger(AppDynamicsProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Handle a POST request, usually from an AJAX communication with the server.
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException 
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String pathInfo = request.getPathInfo();
        
        // Generate the full URL, relative to AppDynamics
        String url = AppDynamicsHandler.generateUrlRelative(pathInfo);
        
        if (url != null)
            AppDynamicsHandler.doPostStream(url, request, response);
        else
            response.sendError(404);
    }
}
