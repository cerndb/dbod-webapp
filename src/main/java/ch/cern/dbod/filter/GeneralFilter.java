/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.filter;

import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.HTTPHelper;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a filter for general use. It prevents the server from showing an error page in case
 * an uncaught exception is raised.
 * @author Daniel Gomez Blanco
 */
public class GeneralFilter implements Filter{

    /**
     * Init method
     * @param fc filter config
     */
    @Override
    public void init(FilterConfig fc){}

    /**
     * Destroy method
     */
    @Override
    public void destroy() {}

    /**
     * Filters the request.
     * @param request servlet request.
     * @param response servlet response.
     * @param filterChain filter chain.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        }
        catch (IOException | ServletException ex) {
            Logger.getLogger(GeneralFilter.class.getName()).log(Level.SEVERE, "UNCAUGHT EXCEPTION", ex);
            HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, CommonConstants.PAGE_ERROR);
        }
    }
}
