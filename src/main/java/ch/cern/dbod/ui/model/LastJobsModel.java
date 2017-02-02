/*
 * Copyright (C) 2017, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.model;

import ch.cern.dbod.db.entity.Job;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.event.ListDataEvent;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ext.Sortable;

/**
 * Represents the list of last executed jobs.
 * @author Jose Andres Cordero Benitez
 */
public class LastJobsModel extends AbstractListModel implements Sortable {
    /**
     * Last jobs information in the model.
     */
    private List<Job> jobs;
    /**
     * Indicates if the order is ascending or descending.
     */
    private boolean ascending;
    /**
     * Comparator to sort the jobs once they are reloaded.
     */
    private Comparator comparator;

    /**
     * Constructor for this class, passing the list of jobs as a parameter.
     * @param jobs jobs to make the model of.
     */
    public LastJobsModel(List<Job> jobs) {
        this.jobs = jobs;
    }
    
    /**
     * Sets the list of jobs.
     * @param jobs list of jobs.
     */
    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    /**
     * Overrides the method to get the size of the model.
     * @return the number of jobs in the model.
     */
    @Override
    public int getSize() {
        return jobs.size();
    }

    /**
     * Gets the job at a certain position.
     * @param index index of the job.
     * @return the job.
     */
    @Override
    public Object getElementAt(int index) {
        return jobs.get(index);
    }

    /**
     * Sort the jobs in the model.
     * @param comparator comparator to use.
     * @param ascending indicates if the order is ascending or descending.
     */
    @Override
    public void sort(Comparator comparator, boolean ascending) {
        this.ascending = ascending;
        this.comparator = comparator;
        Collections.sort(jobs, comparator);
        fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
    }
    
    /**
     * Obtains the sort direction.
     * @param cmprtr comparator being used
     * @return ascending or descending
     */
    @Override
    public String getSortDirection(Comparator cmprtr) {
        if (ascending)
            return "ascending";
        else
            return "descending";
    }
}