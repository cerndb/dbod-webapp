/*
 * Copyright (C) 2017, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.util;

import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Snapshot;
import static ch.cern.dbod.util.CommonConstants.RUNDECK_GET_SNAPSHOTS;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Helper to manage snapshots using Rundeck jobs.
 * @author Jose Andres Cordero Benitez
 */
public class SnapshotHelper {

    /**
     * Gets the snapshots for a specific instance.
     * @param instance instance to get the snapshots of.
     * @return list of snapshots.
     */
    public List<Snapshot> getSnapshots(Instance instance) {
        ArrayList<Snapshot> snapshots = new ArrayList<>();
        try {
            String snapshotsString = RestHelper.runRundeckJob(RUNDECK_GET_SNAPSHOTS, instance.getDbName());
            if (snapshotsString != null) {
                String[] snapshotArray = snapshotsString.split(":");
                Pattern pattern = Pattern.compile("_");
                for (int i=0; i<snapshotArray.length;i++) {
                    if (snapshotArray[i] != null && !snapshotArray[i].isEmpty()) {
                        snapshots.add(getSnapshotFromString(snapshotArray[i], pattern));
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING SNAPSHOTS FOR INSTANCE " + instance.getDbName(), ex.getMessage());
            ex.printStackTrace();
        }
        return snapshots;
    }

    /**
     * Creates a snapshot object from a string with the file locator.
     * @param s String to be parsed.
     * @param pattern Pattern to be applied to separate fields.
     * @return Snapshot object
     */
    private Snapshot getSnapshotFromString (String s, Pattern pattern) {
        Snapshot snapshot = new Snapshot();
        //File locator, is the string itself
        snapshot.setFileLocator(s);

        //Split string and get the second and third items (date and time)
        String[] items = pattern.split(s);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Integer.parseInt(items[1].substring(4, 8)), Integer.parseInt(items[1].substring(2, 4)) - 1, Integer.parseInt(items[1].substring(0, 2)),
                        Integer.parseInt(items[2].substring(0, 2)), Integer.parseInt(items[2].substring(2, 4)), Integer.parseInt(items[2].substring(4, 6)));
        snapshot.setCreationDate(calendar.getTime());

        return snapshot;
    }
}
