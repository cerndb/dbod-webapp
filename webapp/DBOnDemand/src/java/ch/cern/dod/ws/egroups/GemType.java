
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DisplayInPhonebook" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GemType", namespace = "https://cra-ws.cern.ch/cra-ws/cra/", propOrder = {
    "emailAddress",
    "displayInPhonebook"
})
public class GemType {

    @XmlElement(name = "EmailAddress", required = true)
    protected String emailAddress;
    @XmlElement(name = "DisplayInPhonebook")
    protected boolean displayInPhonebook;

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the displayInPhonebook property.
     * 
     */
    public boolean isDisplayInPhonebook() {
        return displayInPhonebook;
    }

    /**
     * Sets the value of the displayInPhonebook property.
     * 
     */
    public void setDisplayInPhonebook(boolean value) {
        this.displayInPhonebook = value;
    }

}
