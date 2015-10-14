/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.db.entity;

import java.util.Objects;

/**
 * Represents an upgrade to be added to the system.
 * @author Daniel Gomez Blanco
 */
public class Upgrade implements Comparable{
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
    
    @Override
    public int compareTo(Object object) {
        Upgrade upgrade = (Upgrade) object;
        return this.getDbType().compareTo(upgrade.getDbType());
    }
    
    @Override
    public boolean equals(Object object) {
        if (object instanceof Upgrade) {
            Upgrade upgrade = (Upgrade) object;
            return this.getDbType().equals(upgrade.getDbType()) && this.getCategory().equals(upgrade.getCategory()) && this.getVersionFrom().equals(upgrade.getVersionFrom());
        }
        else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.dbType);
        hash = 67 * hash + Objects.hashCode(this.category);
        hash = 67 * hash + Objects.hashCode(this.versionFrom);
        return hash;
    }
}
