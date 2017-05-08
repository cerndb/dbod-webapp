/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.controller;

import ch.cern.dbod.db.dao.JobDAO;
import ch.cern.dbod.db.entity.Job;
import ch.cern.dbod.ui.model.LastJobsModel;
import ch.cern.dbod.ui.renderer.LastJobsRenderer;
import java.util.List;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.ext.BeforeCompose;
import org.zkoss.zul.*;

/**
 * Controller for the admin monitoring of instances. It allows the admins to get
 * a general view of the whole system.
 * @author Daniel Gomez Blanco
 */
public class AdminMonitoringController extends Vbox implements BeforeCompose, AfterCompose{

    /**
     * Job DAO
     */
    private JobDAO jobDAO;
    /**
     * List of jobs.
     */
    private List<Job> jobs;

    /**
     * Method executed before the page is composed. Obtains stats from DB.
     */
    @Override
    public void beforeCompose() {        
        jobDAO = new JobDAO();
        jobs = jobDAO.selectLastJobs();
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the stats obtained before composing.
     */
    @Override
    public void afterCompose() {
        //Last jobs information grid
        Grid lastJobsGrid = (Grid) getFellow("lastJobsInformation");
        lastJobsGrid.setModel(new LastJobsModel(jobs));
        lastJobsGrid.setRowRenderer(new LastJobsRenderer());
    }
}