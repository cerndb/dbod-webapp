
package ch.cern.dod.ws.egroups;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EmailPropertiesType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EmailPropertiesType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="MailPostingRestrictions" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}MailPostingRestrictionType"/>
 *         &lt;element name="SenderAuthenticationEnabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="WhoReceivesDeliveryErrors" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}WhoReceivesDeliveryErrorsType"/>
 *         &lt;element name="MaxMailSize" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="ArchiveProperties" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}ArchivePropertiesType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EmailPropertiesType", propOrder = {
    "mailPostingRestrictions",
    "senderAuthenticationEnabled",
    "whoReceivesDeliveryErrors",
    "maxMailSize",
    "archiveProperties"
})
public class EmailPropertiesType {

    @XmlElement(name = "MailPostingRestrictions", required = true)
    protected MailPostingRestrictionType mailPostingRestrictions;
    @XmlElement(name = "SenderAuthenticationEnabled")
    protected boolean senderAuthenticationEnabled;
    @XmlElement(name = "WhoReceivesDeliveryErrors", required = true)
    protected WhoReceivesDeliveryErrorsType whoReceivesDeliveryErrors;
    @XmlElement(name = "MaxMailSize", required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger maxMailSize;
    @XmlElement(name = "ArchiveProperties", required = true)
    protected ArchivePropertiesType archiveProperties;

    /**
     * Gets the value of the mailPostingRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link MailPostingRestrictionType }
     *     
     */
    public MailPostingRestrictionType getMailPostingRestrictions() {
        return mailPostingRestrictions;
    }

    /**
     * Sets the value of the mailPostingRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link MailPostingRestrictionType }
     *     
     */
    public void setMailPostingRestrictions(MailPostingRestrictionType value) {
        this.mailPostingRestrictions = value;
    }

    /**
     * Gets the value of the senderAuthenticationEnabled property.
     * 
     */
    public boolean isSenderAuthenticationEnabled() {
        return senderAuthenticationEnabled;
    }

    /**
     * Sets the value of the senderAuthenticationEnabled property.
     * 
     */
    public void setSenderAuthenticationEnabled(boolean value) {
        this.senderAuthenticationEnabled = value;
    }

    /**
     * Gets the value of the whoReceivesDeliveryErrors property.
     * 
     * @return
     *     possible object is
     *     {@link WhoReceivesDeliveryErrorsType }
     *     
     */
    public WhoReceivesDeliveryErrorsType getWhoReceivesDeliveryErrors() {
        return whoReceivesDeliveryErrors;
    }

    /**
     * Sets the value of the whoReceivesDeliveryErrors property.
     * 
     * @param value
     *     allowed object is
     *     {@link WhoReceivesDeliveryErrorsType }
     *     
     */
    public void setWhoReceivesDeliveryErrors(WhoReceivesDeliveryErrorsType value) {
        this.whoReceivesDeliveryErrors = value;
    }

    /**
     * Gets the value of the maxMailSize property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxMailSize() {
        return maxMailSize;
    }

    /**
     * Sets the value of the maxMailSize property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxMailSize(BigInteger value) {
        this.maxMailSize = value;
    }

    /**
     * Gets the value of the archiveProperties property.
     * 
     * @return
     *     possible object is
     *     {@link ArchivePropertiesType }
     *     
     */
    public ArchivePropertiesType getArchiveProperties() {
        return archiveProperties;
    }

    /**
     * Sets the value of the archiveProperties property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArchivePropertiesType }
     *     
     */
    public void setArchiveProperties(ArchivePropertiesType value) {
        this.archiveProperties = value;
    }

}
