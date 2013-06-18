package ch.cern.dod.filter;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODUpgradeDAO;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODUpgrade;
import ch.cern.dod.util.DODConstants;
import ch.cern.dod.util.EGroupHelper;
import ch.cern.dod.util.HTTPHelper;
import java.io.IOException;
import java.util.List;
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
 * Filters requests to only allow admin users to manage their own instances.
 * @author Daniel Gomez Blanco
 * @version 30/09/2011
 */
public class InstanceFilter implements Filter {

    /**
     * Init method
     * @param fc filter config
     */
    @Override
    public void init(FilterConfig fc) {
    }

    /**
     * Destroy mehod
     */
    @Override
    public void destroy() {
    }

    /**
     * Filters the request.
     * @param sr servlet request.
     * @param sr1 servlet response.
     * @param fc filter chain.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        try {
            //Get instance from request
            String dbName = (String) request.getParameter(DODConstants.INSTANCE);
            if (dbName != null && !dbName.isEmpty()) {
                DODUpgradeDAO upgradeDAO = new DODUpgradeDAO();
                List<DODUpgrade> upgrades = upgradeDAO.selectAll();
                DODInstanceDAO instanceDAO = new DODInstanceDAO();
                DODInstance instance = instanceDAO.selectByDbName(dbName, upgrades);

                //Get admin mode from headers
                String eGroups = ((HttpServletRequest) request).getHeader(DODConstants.ADFS_GROUP);
                Boolean adminMode = (Boolean) EGroupHelper.groupInList(DODConstants.ADMIN_E_GROUP, eGroups);

                //If any of these is null redirect to instance not found
                if (instance != null && adminMode != null) {
                    //Admins are allowed to manage every instance
                    if (adminMode) {
                        filterChain.doFilter(request, response);
                    }
                    else {
                        //Obtain username and egroups
                        String username = ((HttpServletRequest) request).getHeader(DODConstants.ADFS_LOGIN);
                        //Check if user is authorized to see instance
                        if (username.equals(instance.getUsername()) || EGroupHelper.groupInList(instance.getEGroup(), eGroups)) {
                            filterChain.doFilter(request, response);
                        }
                        else {
                            HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, DODConstants.PAGE_NOT_AUTHORIZED);
                        }
                    }

                }
                else {
                    HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, DODConstants.PAGE_INSTANCE_NOT_FOUND);
                }
            }
            else {
                HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, DODConstants.PAGE_INSTANCE_NOT_FOUND);
            }
        }
        catch (IOException | ServletException ex) {
            Logger.getLogger(InstanceFilter.class.getName()).log(Level.SEVERE, "ERROR IN INSTANCE FILTER", ex);
            HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, DODConstants.PAGE_ERROR);
        }
    }
}
