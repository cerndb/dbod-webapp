package ch.cern.dod.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Constains methods to manage HTTP operations.
 * @author Daniel Gomez Blanco
 * @version 30/09/2011
 */
public class HTTPHelper {

    /**
     * Redirects to a page specified as a parameter.
     * @param page page to be redirected to
     */
    public static void redirect(HttpServletRequest request, HttpServletResponse response, String page) {
        try {
            response.sendRedirect(request.getContextPath() + response.encodeRedirectURL(page));
        } catch (IOException ex) {
            Logger.getLogger(HTTPHelper.class.getName()).log(Level.SEVERE, "ERROR REDIRECTING TO PAGE", ex);
        }
    }
}
