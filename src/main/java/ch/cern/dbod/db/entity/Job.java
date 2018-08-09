/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * Represents a job that has to be carried out on a database, like startup, shutdown, etc.
 * @author Daniel Gomez Blanco
 */

public class Job {
    
    /**
     * Job ID.
     */
    private int id;
    
    /**
     * Instance ID.
     */
    private int instance_id;

    /**
     * Command to be executed (max. 64)
     */
    @SerializedName("command_name")
    private String commandName;

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
    
    @SerializedName("name")
    @Expose(serialize = false)
    private String instance_name;
    
    @SerializedName("type")
    @Expose(serialize = false)
    private String instance_type;

    /**
     * State of the job
     */
    private String state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(int instance_id) {
        this.instance_id = instance_id;
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
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

    public String getInstance_name()
    {
        return instance_name;
    }

    public void setInstance_name(String instance_name)
    {
        this.instance_name = instance_name;
    }

    public String getInstance_type()
    {
        return instance_type;
    }

    public void setInstance_type(String instance_type)
    {
        this.instance_type = instance_type;
    }
}
