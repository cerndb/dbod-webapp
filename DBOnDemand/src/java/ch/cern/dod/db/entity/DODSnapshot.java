package ch.cern.dod.db.entity;

import java.util.Date;

/**
 *
 * @author Daniel Gomez Blanc
 */
public class DODSnapshot implements Comparable {
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

    public int compareTo(Object object) {
        DODSnapshot snapshot = (DODSnapshot) object;
        return creationDate.compareTo(snapshot.getCreationDate());
    }
}
