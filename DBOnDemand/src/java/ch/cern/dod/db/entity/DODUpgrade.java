package ch.cern.dod.db.entity;

/**
 * Represents an upgrade to be added to the system.
 * @author Daniel Gomez Blanco
 */
public class DODUpgrade implements Comparable{
    /**
     * Database type (Oracle, MySQL, etc)
     */
   private String dbType;
   /**
     * Category of the instances (personal, test or official, max. 32)
     */
   private String category;
   /**
    * Version to which the update is from
    */
   private String versionFrom;
   /**
    * Version to which the update is to
    */
   private String versionTo;

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVersionFrom() {
        return versionFrom;
    }

    public void setVersionFrom(String versionFrom) {
        this.versionFrom = versionFrom;
    }

    public String getVersionTo() {
        return versionTo;
    }

    public void setVersionTo(String versionTo) {
        this.versionTo = versionTo;
    }
    
    public int compareTo(Object object) {
        DODUpgrade upgrade = (DODUpgrade) object;
        return this.getDbType().compareTo(upgrade.getDbType());
    }
    
    @Override
    public boolean equals(Object object) {
        DODUpgrade upgrade = (DODUpgrade) object;
        return this.getDbType().equals(upgrade.getDbType()) && this.getCategory().equals(upgrade.getCategory()) && this.getVersionFrom().equals(upgrade.getVersionFrom());
    }
}
