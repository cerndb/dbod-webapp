
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for EgroupType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EgroupType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Aliases" type="{https://cra-ws.cern.ch/cra-ws/cra/}AliasesType" minOccurs="0"/>
 *         &lt;element name="ID" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Type" type="{https://cra-ws.cern.ch/cra-ws/cra/}EgroupTypeCode"/>
 *         &lt;element name="Status" type="{https://cra-ws.cern.ch/cra-ws/cra/}StatusCode" minOccurs="0"/>
 *         &lt;element name="BlockingReason" type="{https://cra-ws.cern.ch/cra-ws/cra/}BlockingReasonCode" minOccurs="0"/>
 *         &lt;element name="Usage" type="{https://cra-ws.cern.ch/cra-ws/cra/}UsageCode"/>
 *         &lt;element name="Topic" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Comments" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ExpiryDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *         &lt;element name="Owner" type="{https://cra-ws.cern.ch/cra-ws/cra/}UserType"/>
 *         &lt;element name="Administrator" type="{https://cra-ws.cern.ch/cra-ws/cra/}AdministratorType" minOccurs="0"/>
 *         &lt;element name="Privacy" type="{https://cra-ws.cern.ch/cra-ws/cra/}PrivacyType"/>
 *         &lt;element name="Selfsubscription" type="{https://cra-ws.cern.ch/cra-ws/cra/}SelfsubscriptionType"/>
 *         &lt;element name="SelfsubscriptionEgroups" type="{https://cra-ws.cern.ch/cra-ws/cra/}SelfsubscriptionEgroupsType" minOccurs="0"/>
 *         &lt;element name="Members" type="{https://cra-ws.cern.ch/cra-ws/cra/}MembersType" minOccurs="0"/>
 *         &lt;element name="EmailMembers" type="{https://cra-ws.cern.ch/cra-ws/cra/}EmailMembersType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EgroupType", namespace = "https://cra-ws.cern.ch/cra-ws/cra/", propOrder = {
    "name",
    "aliases",
    "id",
    "type",
    "status",
    "blockingReason",
    "usage",
    "topic",
    "description",
    "comments",
    "expiryDate",
    "owner",
    "administrator",
    "privacy",
    "selfsubscription",
    "selfsubscriptionEgroups",
    "members",
    "emailMembers"
})
public class EgroupType {

    @XmlElement(name = "Name", required = true)
    protected String name;
    @XmlElement(name = "Aliases")
    protected AliasesType aliases;
    @XmlElement(name = "ID")
    protected long id;
    @XmlElement(name = "Type", required = true)
    protected EgroupTypeCode type;
    @XmlElement(name = "Status")
    protected StatusCode status;
    @XmlElement(name = "BlockingReason")
    protected BlockingReasonCode blockingReason;
    @XmlElement(name = "Usage", required = true)
    protected UsageCode usage;
    @XmlElement(name = "Topic")
    protected String topic;
    @XmlElement(name = "Description", required = true)
    protected String description;
    @XmlElement(name = "Comments")
    protected String comments;
    @XmlElement(name = "ExpiryDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expiryDate;
    @XmlElement(name = "Owner", required = true)
    protected UserType owner;
    @XmlElement(name = "Administrator")
    protected AdministratorType administrator;
    @XmlElement(name = "Privacy", required = true)
    protected PrivacyType privacy;
    @XmlElement(name = "Selfsubscription", required = true)
    protected SelfsubscriptionType selfsubscription;
    @XmlElement(name = "SelfsubscriptionEgroups")
    protected SelfsubscriptionEgroupsType selfsubscriptionEgroups;
    @XmlElement(name = "Members")
    protected MembersType members;
    @XmlElement(name = "EmailMembers")
    protected EmailMembersType emailMembers;

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
     * Gets the value of the aliases property.
     * 
     * @return
     *     possible object is
     *     {@link AliasesType }
     *     
     */
    public AliasesType getAliases() {
        return aliases;
    }

    /**
     * Sets the value of the aliases property.
     * 
     * @param value
     *     allowed object is
     *     {@link AliasesType }
     *     
     */
    public void setAliases(AliasesType value) {
        this.aliases = value;
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
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link StatusCode }
     *     
     */
    public StatusCode getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link StatusCode }
     *     
     */
    public void setStatus(StatusCode value) {
        this.status = value;
    }

    /**
     * Gets the value of the blockingReason property.
     * 
     * @return
     *     possible object is
     *     {@link BlockingReasonCode }
     *     
     */
    public BlockingReasonCode getBlockingReason() {
        return blockingReason;
    }

    /**
     * Sets the value of the blockingReason property.
     * 
     * @param value
     *     allowed object is
     *     {@link BlockingReasonCode }
     *     
     */
    public void setBlockingReason(BlockingReasonCode value) {
        this.blockingReason = value;
    }

    /**
     * Gets the value of the usage property.
     * 
     * @return
     *     possible object is
     *     {@link UsageCode }
     *     
     */
    public UsageCode getUsage() {
        return usage;
    }

    /**
     * Sets the value of the usage property.
     * 
     * @param value
     *     allowed object is
     *     {@link UsageCode }
     *     
     */
    public void setUsage(UsageCode value) {
        this.usage = value;
    }

    /**
     * Gets the value of the topic property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Sets the value of the topic property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTopic(String value) {
        this.topic = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the comments property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the value of the comments property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setComments(String value) {
        this.comments = value;
    }

    /**
     * Gets the value of the expiryDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiryDate() {
        return expiryDate;
    }

    /**
     * Sets the value of the expiryDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiryDate(XMLGregorianCalendar value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the owner property.
     * 
     * @return
     *     possible object is
     *     {@link UserType }
     *     
     */
    public UserType getOwner() {
        return owner;
    }

    /**
     * Sets the value of the owner property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserType }
     *     
     */
    public void setOwner(UserType value) {
        this.owner = value;
    }

    /**
     * Gets the value of the administrator property.
     * 
     * @return
     *     possible object is
     *     {@link AdministratorType }
     *     
     */
    public AdministratorType getAdministrator() {
        return administrator;
    }

    /**
     * Sets the value of the administrator property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdministratorType }
     *     
     */
    public void setAdministrator(AdministratorType value) {
        this.administrator = value;
    }

    /**
     * Gets the value of the privacy property.
     * 
     * @return
     *     possible object is
     *     {@link PrivacyType }
     *     
     */
    public PrivacyType getPrivacy() {
        return privacy;
    }

    /**
     * Sets the value of the privacy property.
     * 
     * @param value
     *     allowed object is
     *     {@link PrivacyType }
     *     
     */
    public void setPrivacy(PrivacyType value) {
        this.privacy = value;
    }

    /**
     * Gets the value of the selfsubscription property.
     * 
     * @return
     *     possible object is
     *     {@link SelfsubscriptionType }
     *     
     */
    public SelfsubscriptionType getSelfsubscription() {
        return selfsubscription;
    }

    /**
     * Sets the value of the selfsubscription property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelfsubscriptionType }
     *     
     */
    public void setSelfsubscription(SelfsubscriptionType value) {
        this.selfsubscription = value;
    }

    /**
     * Gets the value of the selfsubscriptionEgroups property.
     * 
     * @return
     *     possible object is
     *     {@link SelfsubscriptionEgroupsType }
     *     
     */
    public SelfsubscriptionEgroupsType getSelfsubscriptionEgroups() {
        return selfsubscriptionEgroups;
    }

    /**
     * Sets the value of the selfsubscriptionEgroups property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelfsubscriptionEgroupsType }
     *     
     */
    public void setSelfsubscriptionEgroups(SelfsubscriptionEgroupsType value) {
        this.selfsubscriptionEgroups = value;
    }

    /**
     * Gets the value of the members property.
     * 
     * @return
     *     possible object is
     *     {@link MembersType }
     *     
     */
    public MembersType getMembers() {
        return members;
    }

    /**
     * Sets the value of the members property.
     * 
     * @param value
     *     allowed object is
     *     {@link MembersType }
     *     
     */
    public void setMembers(MembersType value) {
        this.members = value;
    }

    /**
     * Gets the value of the emailMembers property.
     * 
     * @return
     *     possible object is
     *     {@link EmailMembersType }
     *     
     */
    public EmailMembersType getEmailMembers() {
        return emailMembers;
    }

    /**
     * Sets the value of the emailMembers property.
     * 
     * @param value
     *     allowed object is
     *     {@link EmailMembersType }
     *     
     */
    public void setEmailMembers(EmailMembersType value) {
        this.emailMembers = value;
    }

}
