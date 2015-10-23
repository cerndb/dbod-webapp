/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.util;

import ch.cern.dbod.db.entity.Instance;
import static ch.cern.dbod.util.CommonConstants.*;
import java.util.Comparator;

/**
 * Instance comparator used by the Web Interface columns.
 * @author Jose Andres Cordero Benitez
 */
public class InstanceComparator implements Comparator<Instance> {

    private boolean asc = true;
    private String type = "NAME";
    
    public InstanceComparator(boolean asc, String type)
    {
        this.asc = asc;
        this.type = type;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    @Override
    public int compare(Instance o1, Instance o2)
    {
        switch(type)
        {
            case "STATE":
                return stateToNumber(o1.getState()).compareTo(stateToNumber(o2.getState())) * (asc ? 1 : -1);
            case "USERNAME":
                return o1.getUsername().compareTo(o2.getUsername()) * (asc ? 1 : -1);
            case "CREATIONDATE":
                return o1.getCreationDate().compareTo(o2.getCreationDate()) * (asc ? 1 : -1);
            case "HOST":
                return o1.getHost().compareTo(o2.getHost()) * (asc ? 1 : -1);
            case "CATEGORY":
                return o1.getCategory().compareTo(o2.getCategory()) * (asc ? 1 : -1);
            case "DBTYPE":
            case "NAME":
            default:
                return o1.getDbName().compareTo(o2.getDbName()) * (asc ? 1 : -1);
        }
    }
    
    private Integer stateToNumber(String state)
    {
        /* Custom order for State:
         *       UNKNOWN = 1
         *       BUSY = 2
         *       AWAITING_APPROVAL = 3
         *       JOB_PENDING = 4
         *       STOPPED = 5
         *       MAINTENANCE = 6
         *       RUNNING = 7
         */
        switch(state)
        {
            case INSTANCE_STATE_RUNNING:
                return 7;
            case INSTANCE_STATE_MAINTENANCE:
                return 6;
            case INSTANCE_STATE_STOPPED:
                return 5;
            case INSTANCE_STATE_JOB_PENDING:
                return 4;
            case INSTANCE_STATE_AWAITING_APPROVAL:
                return 3;
            case INSTANCE_STATE_BUSY:
                return 2;
            case INSTANCE_STATE_UNKNOWN:
            default:
                return 1;
        }
    }
}
