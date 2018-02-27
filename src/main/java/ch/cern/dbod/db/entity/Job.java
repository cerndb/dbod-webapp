/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.entity;

import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * Represents a job that has to be carried out on a database, like startup, shutdown, etc.
 * @author Daniel Gomez Blanco
 */

public class Job {
    
    /**
     * Username creator of this job (max. 32)
     */
    private String username;

    /**
     * DB name for the instance on which the job is going to be performed (max. 8)
     */
    @SerializedName("db_name")
    private String dbName;

    /**
     * Command to be executed (max. 64)
     */
    @SerializedName("command_name")
    private String commandName;

    /**
     * Type of command (max. 64)
     */
    private String type;

    /**
     * Creation time of this job
     */
    @SerializedName("creation_date")
    private Date creationDate;

    /**
     * Completion time of this job
     */
    @SerializedName("completion_date")
    private Date completionDate;

    /**
     * Username requester of this job (max. 32)
     */
    private String requester;

    /**
     * Admin flag (0 indicates is not an admin job, 1 indicates is an admin job,
     * 2 indicates that it is scheduled job)
     */
    @SerializedName("admin_action")
    private int adminAction;

    /**
     * State of the job
     */
    private String state;

    /**
     * Result of the job
     */
    private String result;
    
    /**
     * Job ID.
     */
    private int id;
    
    /**
     * Instance ID.
     */
    private int instance_id;
    

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public int getAdminAction() {
        return adminAction;
    }

    public void setAdminAction(int adminAction) {
        this.adminAction = adminAction;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getInstance_id()
    {
        return instance_id;
    }

    public void setInstance_id(int instance_id)
    {
        this.instance_id = instance_id;
    }
}
