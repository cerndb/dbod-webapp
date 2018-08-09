/*
 * Copyright (C) 2017, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.renderer;

import ch.cern.dbod.db.entity.Job;
import ch.cern.dbod.util.CommonConstants;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.*;

/**
 * Renderer for last jobs information.
 * @author Jose Andres Cordero Benitez
 */
public class LastJobsRenderer implements RowRenderer {
    
    /**
     * Date formatter for times.
     */
    DateFormat dateTimeFormatter;
    
    public LastJobsRenderer() {
        dateTimeFormatter = new SimpleDateFormat(CommonConstants.DATE_TIME_FORMAT);  
    }

    /**
     * Renders a row
     * @param row object where to place information
     * @param object object to be rendered
     * @param i index of the row
     * @throws Exception in case components cannot be added to the row
     */
    @Override
    public void render(Row row, Object object, int i) throws Exception {
        Job job = (Job) object; 
        row.setStyle("padding-top: 0px; padding-bottom: 0px");
        row.setHeight("24px");
        // the data append to each row with simple label
        row.appendChild(new Label(job.getInstance_name()));
        row.appendChild(new Label(job.getInstance_type()));
        row.appendChild(new Label(Labels.getLabel(CommonConstants.LABEL_JOB + job.getCommandName())));
        
        row.appendChild(new Label(dateTimeFormatter.format(job.getCreationDate())));
        row.appendChild(new Label(job.getCompletionDate() != null ? dateTimeFormatter.format(job.getCompletionDate()) : "-"));
        
        Component statecell;
        switch (job.getState()) {
            case CommonConstants.JOB_STATE_FINISHED_OK:
                statecell = new Image(CommonConstants.IMG_RUNNING);
                break;
            case CommonConstants.JOB_STATE_RUNNING:
                statecell = new Image(CommonConstants.IMG_PENDING);
                break;
            case CommonConstants.JOB_STATE_FINISHED_FAIL:
                statecell = new Image(CommonConstants.IMG_STOPPED);
                break;
            case CommonConstants.JOB_STATE_FINISHED_WARNING: 
                statecell = new Image(CommonConstants.IMG_BUSY);
                break;
            case CommonConstants.JOB_STATE_PENDING:
                statecell = new Image(CommonConstants.IMG_AWAITING_APPROVAL);
                break;
            default:
                statecell = new Label("-");
        }
        row.appendChild(statecell);
    }
}
