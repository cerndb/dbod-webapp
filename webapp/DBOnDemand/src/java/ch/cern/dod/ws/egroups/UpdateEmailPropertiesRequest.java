
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
 *         &lt;element name="EmailProperties" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}EmailPropertiesType"/>
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
    "emailProperties"
})
@XmlRootElement(name = "UpdateEmailPropertiesRequest")
public class UpdateEmailPropertiesRequest {

    @XmlElement(required = true)
    protected String egroupName;
    @XmlElement(name = "EmailProperties", required = true)
    protected EmailPropertiesType emailProperties;

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
     * Gets the value of the emailProperties property.
     * 
     * @return
     *     possible object is
     *     {@link EmailPropertiesType }
     *     
     */
    public EmailPropertiesType getEmailProperties() {
        return emailProperties;
    }

    /**
     * Sets the value of the emailProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailPropertiesType }
     *     
     */
    public void setEmailProperties(EmailPropertiesType value) {
        this.emailProperties = value;
    }

}
