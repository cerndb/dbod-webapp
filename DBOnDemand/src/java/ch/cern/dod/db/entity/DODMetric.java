package ch.cern.dod.db.entity;

/**
 * Represents a metric in the system. It is used to render the different metrics that can be obtained from an instance.
 * @author Daniel Gomez Blanco
 * @version 22/11/2011
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
