/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.entity;

import java.util.Date;
import java.util.Objects;

/**
 * Represents a database in the system.
 * @author Daniel Gomez Blanco
 */
public class Instance implements Comparable, Cloneable{
    /**
     * Username creator of this instance (max. 32)
     */
    private String username;

    /**
     * DB name for this instance (max. 8)
     * 
     * The DB name limitation comes from the fact that Oracle SID's can only
     * be 8 characters
     */
    private String dbName;

    /**
     * DB e-Group (max. 256)
     */
    private String eGroup;

    /**
     * Category of the instances (personal, test or official, max. 32)
     */
    private String category;

    /**
     * Creation date
     */
    private Date creationDate;

    /**
     * Expiration date
     */
    private Date expiryDate;

    /**
     * Database type (Oracle, MySQL, etc)
     */
    private String dbType;

    /**
     * Database size (in GB)
     */
    private int dbSize;

    /**
     * Number of connections
     */
    private int noConnections;

    /**
     * Project
     */
    private String project;

    /**
     * Database description (max. 1024)
     */
    private String description;
    
    /**
     * Version of the database (max. 128)
     */
    private String version;
    
    /**
     * Upgrade available for the database (calculated field)
     */
    private String upgradeTo;

    /**
     * Logical status (active or inactive)
     */
    private boolean status;
    
    /**
     * DB name of the master (if slave)
     */
    private String master;
    
    /**
     * DB name of the slave (if master)
     */
    private String slave;
    
    /**
     * Host where instance is running
     */
    private String host;

    /**
     * State (On Creation, Running, Stopped, etc)
     */
    private String state;
    
    /**
     * If an instance is checked or not in the overview page for admins. If it
     * is checked it will be counted for collective actions.
     */
    private boolean checked;

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

    public String getEGroup() {
        return eGroup;
    }

    public void setEGroup(String eGroup) {
        this.eGroup = eGroup;
    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public int getDbSize() {
        return dbSize;
    }

    public void setDbSize(int dbSize) {
        this.dbSize = dbSize;
    }

    public int getNoConnections() {
        return noConnections;
    }

    public void setNoConnections(int noConnections) {
        this.noConnections = noConnections;
    }

    public String getProject() {
        return project;
    }


    public void setProject(String project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    

    public String getUpgradeTo() {
        return upgradeTo;
    }

    public void setUpgradeTo(String upgradeTo) {
        this.upgradeTo = upgradeTo;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getMaster() {
        return master;
    }

    public void setMaster(String master) {
        this.master = master;
    }

    public String getSlave() {
        return slave;
    }

    public void setSlave(String slave) {
        this.slave = slave;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int compareTo(Object object) {
        Instance instance = (Instance) object;
        return this.getDbName().compareTo(instance.getDbName());
    }

    @Override
    public Instance clone() {
        Instance clone = new Instance();
        clone.setCategory(category);
        clone.setCreationDate((Date) creationDate.clone());
        clone.setDbName(dbName);
        clone.setDbSize(dbSize);
        clone.setDbType(dbType);
        if (description != null)
            clone.setDescription(description);
        if (eGroup != null)
            clone.setEGroup(eGroup);
        if (expiryDate != null)
            clone.setExpiryDate((Date) expiryDate.clone());
        clone.setNoConnections(noConnections);
        if (project != null)
            clone.setProject(project);
        clone.setState(state);
        clone.setStatus(status);
        clone.setUsername(username);
        if (upgradeTo != null)
            clone.setUpgradeTo(upgradeTo);
        if (version != null)
            clone.setVersion(version);
        if (master != null)
            clone.setMaster(master);
        if (slave != null)
            clone.setSlave(slave);
        if (host != null)
            clone.setHost(host);
        return clone;
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof Instance) {
            Instance instance = (Instance) object;
            return this.getDbName().equals(instance.getDbName());
        }
        else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.username);
        hash = 29 * hash + Objects.hashCode(this.dbName);
        hash = 29 * hash + Objects.hashCode(this.eGroup);
        hash = 29 * hash + Objects.hashCode(this.category);
        hash = 29 * hash + Objects.hashCode(this.creationDate);
        hash = 29 * hash + Objects.hashCode(this.expiryDate);
        hash = 29 * hash + Objects.hashCode(this.dbType);
        hash = 29 * hash + this.dbSize;
        hash = 29 * hash + this.noConnections;
        hash = 29 * hash + Objects.hashCode(this.project);
        hash = 29 * hash + Objects.hashCode(this.description);
        hash = 29 * hash + Objects.hashCode(this.version);
        hash = 29 * hash + Objects.hashCode(this.upgradeTo);
        hash = 29 * hash + (this.status ? 1 : 0);
        hash = 29 * hash + Objects.hashCode(this.master);
        hash = 29 * hash + Objects.hashCode(this.slave);
        hash = 29 * hash + Objects.hashCode(this.host);
        hash = 29 * hash + Objects.hashCode(this.state);
        hash = 29 * hash + (this.checked ? 1 : 0);
        return hash;
    }
}