package ch.cern.dbod.db.entity;

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
    private String dbName;

    /**
     * Command to be executed (max. 64)
     */
    private String commandName;

    /**
     * Type of command (max. 64)
     */
    private String type;

    /**
     * Creation time of this job
     */
    private Date creationDate;

    /**
     * Completion time of this job
     */
    private Date completionDate;

    /**
     * Username requester of this job (max. 32)
     */
    private String requester;

    /**
     * Admin flag (0 indicates is not an admin job, 1 indicates is an admin job,
     * 2 indicates that it is scheduled job)
     */
    private int adminAction;

    /**
     * State of the job
     */
    private String state;

    /**
     * Result of the job
     */
    private String result;
    

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
}
