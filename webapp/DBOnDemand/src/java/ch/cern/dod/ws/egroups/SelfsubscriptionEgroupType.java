
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SelfsubscriptionEgroupType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SelfsubscriptionEgroupType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="ApprovalNeeded" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SelfsubscriptionEgroupType", propOrder = {
    "id",
    "approvalNeeded"
})
public class SelfsubscriptionEgroupType {

    @XmlElement(name = "ID")
    protected long id;
    @XmlElement(name = "ApprovalNeeded")
    protected boolean approvalNeeded;

    /**
     * Gets the value of the id property.
     * 
     */
    public long getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setID(long value) {
        this.id = value;
    }

    /**
     * Gets the value of the approvalNeeded property.
     * 
     */
    public boolean isApprovalNeeded() {
        return approvalNeeded;
    }

    /**
     * Sets the value of the approvalNeeded property.
     * 
     */
    public void setApprovalNeeded(boolean value) {
        this.approvalNeeded = value;
    }

}
