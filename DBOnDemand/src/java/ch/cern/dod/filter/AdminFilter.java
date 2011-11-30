package ch.cern.dod.filter;

import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import ch.cern.dod.util.HTTPHelper;
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
 * Filters requests to only allow admin users to visit admin content.
 * @author Daniel Gomez Blanco
 * @version 30/09/2011
 */
public class AdminFilter implements Filter{

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
            //Get groups
            String eGroups = ((HttpServletRequest) request).getHeader(DODConstants.ADFS_GROUP);

            //If user is not admin redirect to unauthorized
            if (EGroupHelper.groupInList(DODConstants.ADMIN_E_GROUP, eGroups))
                filterChain.doFilter(request, response);
            else
                HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, DODConstants.PAGE_NOT_AUTHORIZED);
        }
        catch (IOException ex) {
            Logger.getLogger(AdminFilter.class.getName()).log(Level.SEVERE, "ERROR IN ADMIN FILTER", ex);
            HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, DODConstants.PAGE_ERROR);
        }
        catch (ServletException ex) {
            Logger.getLogger(AdminFilter.class.getName()).log(Level.SEVERE, "ERROR IN ADMIN FILTER", ex);
            HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, DODConstants.PAGE_ERROR);
        }
    }
}
