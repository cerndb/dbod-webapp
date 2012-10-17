
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
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Type" type="{https://cra-ws.cern.ch/cra-ws/cra/}EgroupTypeCode"/>
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
@XmlType(name = "SelfsubscriptionEgroupType", namespace = "https://cra-ws.cern.ch/cra-ws/cra/", propOrder = {
    "name",
    "id",
    "type",
    "approvalNeeded"
})
public class SelfsubscriptionEgroupType {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "ID")
    protected long id;
    @XmlElement(name = "Type", required = true)
    protected EgroupTypeCode type;
    @XmlElement(name = "ApprovalNeeded")
    protected boolean approvalNeeded;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EgroupTypeCode }
     *     
     */
    public EgroupTypeCode getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EgroupTypeCode }
     *     
     */
    public void setType(EgroupTypeCode value) {
        this.type = value;
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
