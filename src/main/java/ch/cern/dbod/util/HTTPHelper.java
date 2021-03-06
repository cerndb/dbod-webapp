/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.util;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Constains methods to manage HTTP operations.
 * @author Daniel Gomez Blanco
 */
public class HTTPHelper {

    /**
     * Redirects to a page specified as a parameter.
     * @param page page to be redirected to
     * @param request request created by the user
     * @param response from the server
     */
    public static void redirect(HttpServletRequest request, HttpServletResponse response, String page) {
        try {
            response.sendRedirect(request.getContextPath() + response.encodeRedirectURL(page));
        } catch (IOException ex) {
            Logger.getLogger(HTTPHelper.class.getName()).log(Level.SEVERE, "ERROR REDIRECTING TO PAGE", ex);
        }
    }
}
