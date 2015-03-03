package ch.cern.dbod.db.entity;

import java.util.Date;

/**
 * Represents a snapshot with its creation date and the file name. 
 * @author Daniel Gomez Blanco
 */
public class Snapshot implements Comparable {
    /**
     * Snapshot creation date
     */
    private Date creationDate;
    /**
     * Snapshot file locator
     */
    private String fileLocator;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getFileLocator() {
        return fileLocator;
    }

    public void setFileLocator(String fileLocator) {
        this.fileLocator = fileLocator;
    }

    @Override
    public int compareTo(Object object) {
        Snapshot snapshot = (Snapshot) object;
        return creationDate.compareTo(snapshot.getCreationDate());
    }
}
