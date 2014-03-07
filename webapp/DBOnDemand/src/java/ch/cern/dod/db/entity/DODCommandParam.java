package ch.cern.dod.db.entity;

import java.util.Date;

/**
 * Represents a parameter job that has to be carried out on a database. In the
 * parameters we include things like the timestamp for a recovery, the config
 * file to upload, etc.
 * @author Daniel Gomez Blanco
 */

public class DODCommandParam{
    /**
     * Username creator of this instance (max. 32)
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
     * Creation time
     */
    private Date creationDate;

    /**
     * Type of command (max. 64)
     */
    private String name;

    /**
     * Value of this param (max. 128)
     */
    private String value;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
