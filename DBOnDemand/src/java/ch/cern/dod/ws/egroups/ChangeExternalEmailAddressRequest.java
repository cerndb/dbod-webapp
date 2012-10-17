
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
 *         &lt;element name="p_niceUserid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="p_password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="p_oldEmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="p_newEmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "pNiceUserid",
    "pPassword",
    "pOldEmailAddress",
    "pNewEmailAddress"
})
@XmlRootElement(name = "changeExternalEmailAddressRequest")
public class ChangeExternalEmailAddressRequest {

    @XmlElement(name = "p_niceUserid", required = true)
    protected String pNiceUserid;
    @XmlElement(name = "p_password", required = true)
    protected String pPassword;
    @XmlElement(name = "p_oldEmailAddress", required = true)
    protected String pOldEmailAddress;
    @XmlElement(name = "p_newEmailAddress", required = true)
    protected String pNewEmailAddress;

    /**
     * Gets the value of the pNiceUserid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPNiceUserid() {
        return pNiceUserid;
    }

    /**
     * Sets the value of the pNiceUserid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPNiceUserid(String value) {
        this.pNiceUserid = value;
    }

    /**
     * Gets the value of the pPassword property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPPassword() {
        return pPassword;
    }

    /**
     * Sets the value of the pPassword property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPPassword(String value) {
        this.pPassword = value;
    }

    /**
     * Gets the value of the pOldEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPOldEmailAddress() {
        return pOldEmailAddress;
    }

    /**
     * Sets the value of the pOldEmailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPOldEmailAddress(String value) {
        this.pOldEmailAddress = value;
    }

    /**
     * Gets the value of the pNewEmailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPNewEmailAddress() {
        return pNewEmailAddress;
    }

    /**
     * Sets the value of the pNewEmailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPNewEmailAddress(String value) {
        this.pNewEmailAddress = value;
    }

}
