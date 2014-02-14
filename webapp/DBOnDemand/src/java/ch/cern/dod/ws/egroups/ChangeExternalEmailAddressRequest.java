
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
 *         &lt;element name="oldEmail" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="newEmail" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "oldEmail",
    "newEmail"
})
@XmlRootElement(name = "ChangeExternalEmailAddressRequest")
public class ChangeExternalEmailAddressRequest {

    @XmlElement(required = true)
    protected String oldEmail;
    @XmlElement(required = true)
    protected String newEmail;

    /**
     * Gets the value of the oldEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOldEmail() {
        return oldEmail;
    }

    /**
     * Sets the value of the oldEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOldEmail(String value) {
        this.oldEmail = value;
    }

    /**
     * Gets the value of the newEmail property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNewEmail() {
        return newEmail;
    }

    /**
     * Sets the value of the newEmail property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNewEmail(String value) {
        this.newEmail = value;
    }

}
