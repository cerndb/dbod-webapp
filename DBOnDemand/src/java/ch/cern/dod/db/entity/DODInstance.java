package ch.cern.dod.db.entity;

import java.util.Date;

/**
 * Represents a database in the system.
 * @author Daniel Gomez Blanco
 * @version 31/08/2011
 */
public class DODInstance implements Comparable, Cloneable{
    /**
     * Username creator of this instance (max. 32)
     */
    private String username;

    /**
     * DB name for this instance (max. 128)
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
     * State (On Creation, Running, Stopped, etc)
     */
    private String state;

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

    public int compareTo(Object object) {
        DODInstance instance = (DODInstance) object;
        return this.getDbName().compareTo(instance.getDbName());
    }

    @Override
    public DODInstance clone() {
        DODInstance clone = new DODInstance();
        clone.setCategory(new String(category));
        clone.setCreationDate((Date) creationDate.clone());
        clone.setDbName(new String(dbName));
        clone.setDbSize(dbSize);
        clone.setDbType(new String(dbType));
        if (description != null)
            clone.setDescription(new String(description));
        if (eGroup != null)
            clone.setEGroup(new String(eGroup));
        if (expiryDate != null)
            clone.setExpiryDate((Date) expiryDate.clone());
        clone.setNoConnections(noConnections);
        if (project != null)
            clone.setProject(new String(project));
        clone.setState(new String(state));
        clone.setStatus(status);
        clone.setUsername(new String(username));
        if (upgradeTo != null)
            clone.setUpgradeTo(new String(upgradeTo));
        if (version != null)
            clone.setVersion(new String(version));
        return clone;
    }
    
    @Override
    public boolean equals(Object object) {
        DODInstance instance = (DODInstance) object;
        return this.getDbName().equals(instance.getDbName());
    }
}
