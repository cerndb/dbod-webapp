package ch.cern.dod.db.entity;

/**
 * Represents a metric in the system
 * @author Daniel Gomez Blanco
 */
public class DODMetric {
    /**
     * Id of the metric
     */
    private String id;
    /**
     * Description of the metric
     */
    private String description;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
