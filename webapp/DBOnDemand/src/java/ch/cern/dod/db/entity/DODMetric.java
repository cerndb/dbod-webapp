package ch.cern.dod.db.entity;

/**
 * Represents a metric in the system. It is used to render the different metrics that can be obtained from an instance.
 * @author Daniel Gomez Blanco
 * @version 22/11/2011
 */
public class DODMetric implements Comparable {
    /**
     * Id of the metric
     */
    private String id;
    /**
     * Description of the metric
     */
    private String description;
    /**
     * Type of metric
     */
    private String type;

    public DODMetric() {
    }

    public DODMetric(String id, String description, String type) {
        this.id = id;
        this.description = description;
        this.type = type;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int compareTo(Object o) {
        return this.description.compareTo(((DODMetric)o).description);
    }
}
