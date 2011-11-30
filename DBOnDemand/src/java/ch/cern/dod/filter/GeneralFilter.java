package ch.cern.dod.filter;

import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.HTTPHelper;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a filter for general use. It prevents the server from showing an error page in case
 * an uncaught exception is raised.
 * @author Daniel Gomez Blanco
 * @version 30/11/2011
 */
public class GeneralFilter implements Filter{

    /**
     * Init method
     * @param fc filter config
     */
    public void init(FilterConfig fc){}

    /**
     * Destroy method
     */
    public void destroy() {}

    /**
     * Filters the request.
     * @param sr servlet request.
     * @param sr1 servlet response.
     * @param fc filter chain.
     */
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        }
        catch (Exception ex) {
            Logger.getLogger(GeneralFilter.class.getName()).log(Level.SEVERE, "UNCAUGHT EXCEPTION", ex);
            HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, DODConstants.PAGE_ERROR);
        }
    }
}
