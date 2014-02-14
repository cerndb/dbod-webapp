
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="egroupName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="members" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}EmailsType"/>
 *         &lt;element name="overwriteMembers" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "egroupName",
    "members",
    "overwriteMembers"
})
@XmlRootElement(name = "AddEgroupEmailMembersRequest")
public class AddEgroupEmailMembersRequest {

    @XmlElement(required = true)
    protected String egroupName;
    @XmlElement(required = true)
    protected EmailsType members;
    protected boolean overwriteMembers;

    /**
     * Gets the value of the egroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEgroupName() {
        return egroupName;
    }

    /**
     * Sets the value of the egroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEgroupName(String value) {
        this.egroupName = value;
    }

    /**
     * Gets the value of the members property.
     * 
     * @return
     *     possible object is
     *     {@link EmailsType }
     *     
     */
    public EmailsType getMembers() {
        return members;
    }

    /**
     * Sets the value of the members property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailsType }
     *     
     */
    public void setMembers(EmailsType value) {
        this.members = value;
    }

    /**
     * Gets the value of the overwriteMembers property.
     * 
     */
    public boolean isOverwriteMembers() {
        return overwriteMembers;
    }

    /**
     * Sets the value of the overwriteMembers property.
     * 
     */
    public void setOverwriteMembers(boolean value) {
        this.overwriteMembers = value;
    }

}
