/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ui.components;

import ch.cern.dbod.db.entity.Snapshot;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.zkoss.json.JSONArray;
import org.zkoss.zul.Calendar;

/**
 * Customized calendar to mark days for snapshots (it should be used with the correspondent script
 * markSnapshots.js included in the page).
 * @author Daniel Gomez Blanco
 */
public class SnapshotCalendar extends Calendar {

    /**
     * List of formatted dates.
     */
    private List<String> snapshots;
    /**
     * Date formatter.
     */
    private DateFormat dateFormatter = new SimpleDateFormat("d/M/yyyy");

    /**
     * Converts a list of strings to a JSON array.
     * @param events list of formatted dates.
     * @return JSON array containing the formatted dates.
     */
    private String getSnapshotsInJSONFormat(List<String> events) {
        return JSONArray.toJSONString(events);
    }

    /**
     * Setter for the snapshots. It formats the dates in the snapshot list passed as
     * a parameter and return a list of string with the formatted dates.
     * @param snapshots list of formatted dates.
     */
    public void setSnapshots(List<Snapshot> snapshots) {
        if (snapshots != null && snapshots.size() > 0) {
            this.snapshots = new ArrayList<>();
            for (Snapshot item : snapshots) {
                this.snapshots.add(dateFormatter.format((item.getCreationDate())));
            }
            smartUpdate("snapshots", getSnapshotsInJSONFormat(this.snapshots));
        }
    }

    /**
     * Adds the snapshot list as a property of the calendar, to be rendered in the client.
     * @param renderer renderer to use.
     * @throws java.io.IOException if the property cannot be rendered.
     */
    @Override
    protected void renderProperties(org.zkoss.zk.ui.sys.ContentRenderer renderer) throws java.io.IOException {
        super.renderProperties(renderer);
        if (this.snapshots != null && this.snapshots.size() > 0) {
            render(renderer, "snapshots", getSnapshotsInJSONFormat(this.snapshots));
        }
    }
}
