package ch.cern.dbod.appservlet;

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
 * Servlet to be used as an internat proxy between the user and AppDynamics application.
 * @author Jose Andres Cordero Benitez
 * June 2015
 */
public class doProxy extends HttpServlet {

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
            String url = HttpConnection.generateUrlRelative(pathInfo);
            
            if (url == null)
            {
                response.sendError(404);
                return;
            }
            
            // Get and resend all the parameters in the request
            String params = HttpConnection.generateParametersString(request);
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
                HttpConnection.doGetUsingCookies(url, mimetype, request, response);
            else if (mimetype.startsWith("text"))
                HttpConnection.doGet(url, mimetype, request, response);
            else
                HttpConnection.doGetStream(url, mimetype, response);
        }
        catch (IOException ex)
        {
            Logger.getLogger(doProxy.class.getName()).log(Level.SEVERE, null, ex);
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
        String url = HttpConnection.generateUrlRelative(pathInfo);
        
        HttpConnection.doPostStream(url, request, response);
    }
}
