/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.entity;

/**
 * This class represents a job statistic.
 * @author Daniel Gomez Blanco
 */
public class JobStat implements Comparable{
    /**
     * DB name of the instance.
     */
    private String dbName;
    /**
     * Name of the command executed.
     */
    private String commandName;
    /**
     * Count of the executed commands
     */
    private int count;
    /**
     * Mean duration of the command executions
     */
    private int meanDuration;

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getMeanDuration() {
        return meanDuration;
    }

    public void setMeanDuration(int meanDuration) {
        this.meanDuration = meanDuration;
    }

    @Override
    public int compareTo(Object o) {
        return dbName.compareTo(((JobStat)o).getDbName());
    }
}
