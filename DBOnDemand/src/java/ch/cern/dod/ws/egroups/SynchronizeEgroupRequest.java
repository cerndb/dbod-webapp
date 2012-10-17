
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
 *         &lt;element name="p_egroup" type="{https://cra-ws.cern.ch/cra-ws/cra/}EgroupType"/>
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
    "pEgroup"
})
@XmlRootElement(name = "synchronizeEgroupRequest")
public class SynchronizeEgroupRequest {

    @XmlElement(name = "p_niceUserid", required = true)
    protected String pNiceUserid;
    @XmlElement(name = "p_password", required = true)
    protected String pPassword;
    @XmlElement(name = "p_egroup", required = true)
    protected EgroupType pEgroup;

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
     * Gets the value of the pEgroup property.
     * 
     * @return
     *     possible object is
     *     {@link EgroupType }
     *     
     */
    public EgroupType getPEgroup() {
        return pEgroup;
    }

    /**
     * Sets the value of the pEgroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link EgroupType }
     *     
     */
    public void setPEgroup(EgroupType value) {
        this.pEgroup = value;
    }

}
