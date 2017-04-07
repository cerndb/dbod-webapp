/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.filter;

import ch.cern.dbod.util.ConfigLoader;
import ch.cern.dbod.db.dao.InstanceDAO;
import ch.cern.dbod.db.dao.UpgradeDAO;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Upgrade;
import ch.cern.dbod.util.CommonConstants;
import ch.cern.dbod.util.EGroupHelper;
import ch.cern.dbod.util.HTTPHelper;
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
     * Destroy method
     */
    @Override
    public void destroy() {
    }

    /**
     * Filters the request.
     * @param request servlet request.
     * @param response servlet response.
     * @param filterChain filter chain.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) {
        try {
            //Get instance from request
            String dbName = (String) request.getParameter(CommonConstants.INSTANCE);
            if (dbName != null && !dbName.isEmpty()) {
                UpgradeDAO upgradeDAO = new UpgradeDAO();
                List<Upgrade> upgrades = upgradeDAO.selectAll();
                InstanceDAO instanceDAO = new InstanceDAO();
                Instance instance = instanceDAO.selectByDbName(dbName, upgrades);

                //Get admin mode from headers
                String eGroups = ((HttpServletRequest) request).getHeader(CommonConstants.ADFS_GROUP);
                Boolean adminMode = (Boolean) EGroupHelper.groupInList(ConfigLoader.getProperty(CommonConstants.ADMIN_E_GROUP), eGroups);

                //If any of these is null redirect to instance not found
                if (instance != null && adminMode != null) {
                    //Admins are allowed to manage every instance
                    if (adminMode) {
                        filterChain.doFilter(request, response);
                    }
                    else {
                        //Obtain username and egroups
                        String username = ((HttpServletRequest) request).getHeader(CommonConstants.ADFS_LOGIN);
                        //Check if user is authorized to see instance
                        if (username.equals(instance.getUsername()) || EGroupHelper.groupInList(instance.getEGroup(), eGroups)) {
                            filterChain.doFilter(request, response);
                        }
                        else {
                            HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, CommonConstants.PAGE_NOT_AUTHORIZED);
                        }
                    }

                }
                else {
                    HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, CommonConstants.PAGE_INSTANCE_NOT_FOUND);
                }
            }
            else {
                HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, CommonConstants.PAGE_INSTANCE_NOT_FOUND);
            }
        }
        catch (IOException | ServletException ex) {
            Logger.getLogger(InstanceFilter.class.getName()).log(Level.SEVERE, "ERROR IN INSTANCE FILTER", ex);
            HTTPHelper.redirect((HttpServletRequest) request, (HttpServletResponse) response, CommonConstants.PAGE_ERROR);
        }
    }
}
