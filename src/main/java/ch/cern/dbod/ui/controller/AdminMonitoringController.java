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
import ch.cern.dbod.db.dao.StatsDAO;
import ch.cern.dbod.db.entity.CommandStat;
import ch.cern.dbod.db.entity.Job;
import ch.cern.dbod.db.entity.JobStat;
import ch.cern.dbod.ui.model.CommandStatsModel;
import ch.cern.dbod.ui.model.JobStatsModel;
import ch.cern.dbod.ui.model.LastJobsModel;
import ch.cern.dbod.ui.renderer.CommandStatsRenderer;
import ch.cern.dbod.ui.renderer.JobStatsRenderer;
import ch.cern.dbod.ui.renderer.LastJobsRenderer;
import ch.cern.dbod.util.CommonConstants;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
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
     * Stats DAO
     */
    private StatsDAO statsDAO;
    /**
     * Job DAO
     */
    private JobDAO jobDAO;
    /**
     * List of command stats.
     */
    private List<CommandStat> commandStats;
    /**
     * List of job stats.
     */
    private List<JobStat> jobStats;
    /**
     * List of jobs.
     */
    private List<Job> jobs;

    /**
     * Method executed before the page is composed. Obtains stats from DB.
     */
    @Override
    public void beforeCompose() {        
        //Select stats
        statsDAO = new StatsDAO();
        jobDAO = new JobDAO();
        commandStats = statsDAO.selectCommandStats();
        jobStats = statsDAO.selectJobStats();
        jobs = jobDAO.selectLastJobs();
    }

    /**
     * Method executed after composing the page. Sets the model of the grid
     * with the stats obtained before composing.
     */
    @Override
    public void afterCompose() {
        //Filters for jobs
        String filterJobDbName = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_JOB_DB_NAME);
        if (filterJobDbName != null && !filterJobDbName.isEmpty()) {
            ((Textbox) getFellow("jobStatsDBNameFilter")).setValue(filterJobDbName);
        }
        String filterJobCommandName = (String) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_JOB_COMMAND_NAME);
        if (filterJobCommandName != null && !filterJobCommandName.isEmpty()) {
            ((Textbox) getFellow("jobStatsCommandFilter")).setValue(filterJobCommandName);
        }
        
        //Last jobs information grid
        Grid lastJobsGrid = (Grid) getFellow("lastJobsInformation");
        lastJobsGrid.setModel(new LastJobsModel(jobs));
        lastJobsGrid.setRowRenderer(new LastJobsRenderer());

        //Command stats grid
        Grid commandStatsGrid = (Grid) getFellow("commandStatsGrid");
        commandStatsGrid.setModel(new CommandStatsModel(commandStats));
        commandStatsGrid.setRowRenderer(new CommandStatsRenderer());
        
        //Job stats grid
        Grid jobStatsGrid = (Grid) getFellow("jobStatsGrid");
        jobStatsGrid.setModel(new JobStatsModel(jobStats));
        jobStatsGrid.setRowRenderer(new JobStatsRenderer());
        filterJobStats(); //Filter jobs (there could be values from session)
        
        displayOrHideAreas();
        
        Boolean showAllJobStats = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL_JOB_STATS);
        if (showAllJobStats != null && showAllJobStats) {
            showAllJobStats(showAllJobStats);
        }
        else {
            showAllJobStats(false);
        }
        Boolean showAllCommandStats = (Boolean) Sessions.getCurrent().getAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL_COMMAND_STATS);
        if (showAllCommandStats != null && showAllCommandStats) {
            showAllCommandStats(showAllCommandStats);
        }
        else {
            showAllCommandStats(false);
        }
    }
    
    /**
     * Displays or hides certain areas when refreshing instances and upgrades
     */
    private void displayOrHideAreas () { 
        if (commandStats != null && commandStats.size() > 0) {
            ((Grid) getFellow("commandStatsGrid")).setStyle("display:block");
            ((Div) getFellow("emptyCommandStatsMsg")).setStyle("display:none");
            if (commandStats.size() > 10) {
                ((Foot) getFellow("commandStatsFooter")).setStyle("display:block");
            }
            else {
                ((Foot) getFellow("commandStatsFooter")).setStyle("display:none");
            }
        }
        else {
            ((Grid) getFellow("commandStatsGrid")).setStyle("display:none");
            ((Div) getFellow("emptyCommandStatsMsg")).setStyle("display:block");
            ((Foot) getFellow("commandStatsFooter")).setStyle("display:none");
        }
        
        if (jobStats != null && jobStats.size() > 0) {
            ((Grid) getFellow("jobStatsGrid")).setStyle("display:block");
            ((Div) getFellow("emptyJobStatsMsg")).setStyle("display:none");
            if (jobStats.size() > 10) {
                ((Foot) getFellow("jobStatsFooter")).setStyle("display:block");
            }
            else {
                ((Foot) getFellow("jobStatsFooter")).setStyle("display:none");
            }
        }
        else {
            ((Grid) getFellow("jobStatsGrid")).setStyle("display:none");
            ((Div) getFellow("emptyJobStatsMsg")).setStyle("display:block");
            ((Foot) getFellow("jobStatsFooter")).setStyle("display:none");
        }
    }

    /**
     * Displays all command stats in the view
     * 
     * @param show indicates if all should be displayed or not
     */
    public void showAllCommandStats(boolean show) {
        Grid grid = (Grid) getFellow("commandStatsGrid");
        Hbox showAll = (Hbox) getFellow("showAllCommandStats");
        Hbox paging = (Hbox) getFellow("pagingCommandStats");
        if (show) {
            grid.setMold("default");
            showAll.setStyle("display:none");
            paging.setStyle("display:block");
        }
        else {
            grid.setMold("paging");
            grid.setPageSize(10);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL_COMMAND_STATS, show);
    }
    
    /**
     * Displays all job stats in the view
     * 
     * @param show indicates if all should be displayed or not
     */
    public void showAllJobStats(boolean show) {
        Grid grid = (Grid) getFellow("jobStatsGrid");
        Hbox showAll = (Hbox) getFellow("showAllJobStats");
        Hbox paging = (Hbox) getFellow("pagingJobStats");
        if (show) {
            grid.setMold("default");
            showAll.setStyle("display:none");
            paging.setStyle("display:block");
        }
        else {
            grid.setMold("paging");
            grid.setPageSize(10);
            showAll.setStyle("display:block");
            paging.setStyle("display:none");
        }
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_SHOW_ALL_JOB_STATS, show);
    }

    /**
     * Re-renders the grid in order to filter job stats.
     */
    public void filterJobStats () {
        //Re-render the grid
        Grid grid = (Grid) getFellow("jobStatsGrid");
        ((JobStatsModel) grid.getModel()).filterJobStats(((Textbox) getFellow("jobStatsDBNameFilter")).getValue(),
                                                            ((Textbox) getFellow("jobStatsCommandFilter")).getValue());
        
        displayOrHideAreas();
        
        //Set filters on session
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_JOB_COMMAND_NAME, ((Textbox) getFellow("jobStatsCommandFilter")).getValue());
        Sessions.getCurrent().setAttribute(CommonConstants.ATTRIBUTE_ADMIN_FILTER_JOB_DB_NAME, ((Textbox) getFellow("jobStatsDBNameFilter")).getValue());
    }
    
    /**
     * Displays an error window for the error code provided.
     * @param errorCode error code for the message to be displayed.
     */
    private void showError(String errorCode) {
        Window errorWindow = (Window) getFellow("errorWindow");
        Label errorMessage = (Label) errorWindow.getFellow("errorMessage");
        errorMessage.setValue(Labels.getLabel(errorCode));
        try {
            errorWindow.doModal();
        }
        catch (SuspendNotAllowedException ex) {
            Logger.getLogger(AdminMonitoringController.class.getName()).log(Level.SEVERE, "ERROR SHOWING ERROR WINDOW", ex);
        }
    }
}