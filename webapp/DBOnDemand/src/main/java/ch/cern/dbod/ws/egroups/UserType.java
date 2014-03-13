
package ch.cern.dbod.ws.egroups;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UserType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="UserType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PersonId" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;element name="Name" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ComputingRulesSigned" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *         &lt;element name="Pem" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PrimaryGem" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}GemType" minOccurs="0"/>
 *         &lt;element name="CernUnit" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CernDepartment" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="CernGroup" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Telephone1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Fax" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Building" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Floor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Room" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Mailbox" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserType", propOrder = {
    "personId",
    "name",
    "computingRulesSigned",
    "pem",
    "primaryGem",
    "cernUnit",
    "cernDepartment",
    "cernGroup",
    "telephone1",
    "fax",
    "building",
    "floor",
    "room",
    "mailbox"
})
public class UserType {

    @XmlElement(name = "PersonId")
    protected long personId;
    @XmlElement(name = "Name")
    protected String name;
    @XmlElement(name = "ComputingRulesSigned")
    protected Boolean computingRulesSigned;
    @XmlElement(name = "Pem")
    protected String pem;
    @XmlElement(name = "PrimaryGem")
    protected GemType primaryGem;
    @XmlElement(name = "CernUnit")
    protected String cernUnit;
    @XmlElement(name = "CernDepartment")
    protected String cernDepartment;
    @XmlElement(name = "CernGroup")
    protected String cernGroup;
    @XmlElement(name = "Telephone1")
    protected String telephone1;
    @XmlElement(name = "Fax")
    protected String fax;
    @XmlElement(name = "Building")
    protected String building;
    @XmlElement(name = "Floor")
    protected String floor;
    @XmlElement(name = "Room")
    protected String room;
    @XmlElement(name = "Mailbox")
    protected String mailbox;

    /**
     * Gets the value of the personId property.
     * 
     */
    public long getPersonId() {
        return personId;
    }

    /**
     * Sets the value of the personId property.
     * 
     */
    public void setPersonId(long value) {
        this.personId = value;
    }

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
     * Gets the value of the computingRulesSigned property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isComputingRulesSigned() {
        return computingRulesSigned;
    }

    /**
     * Sets the value of the computingRulesSigned property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setComputingRulesSigned(Boolean value) {
        this.computingRulesSigned = value;
    }

    /**
     * Gets the value of the pem property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPem() {
        return pem;
    }

    /**
     * Sets the value of the pem property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPem(String value) {
        this.pem = value;
    }

    /**
     * Gets the value of the primaryGem property.
     * 
     * @return
     *     possible object is
     *     {@link GemType }
     *     
     */
    public GemType getPrimaryGem() {
        return primaryGem;
    }

    /**
     * Sets the value of the primaryGem property.
     * 
     * @param value
     *     allowed object is
     *     {@link GemType }
     *     
     */
    public void setPrimaryGem(GemType value) {
        this.primaryGem = value;
    }

    /**
     * Gets the value of the cernUnit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCernUnit() {
        return cernUnit;
    }

    /**
     * Sets the value of the cernUnit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCernUnit(String value) {
        this.cernUnit = value;
    }

    /**
     * Gets the value of the cernDepartment property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCernDepartment() {
        return cernDepartment;
    }

    /**
     * Sets the value of the cernDepartment property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCernDepartment(String value) {
        this.cernDepartment = value;
    }

    /**
     * Gets the value of the cernGroup property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCernGroup() {
        return cernGroup;
    }

    /**
     * Sets the value of the cernGroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCernGroup(String value) {
        this.cernGroup = value;
    }

    /**
     * Gets the value of the telephone1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelephone1() {
        return telephone1;
    }

    /**
     * Sets the value of the telephone1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelephone1(String value) {
        this.telephone1 = value;
    }

    /**
     * Gets the value of the fax property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFax() {
        return fax;
    }

    /**
     * Sets the value of the fax property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFax(String value) {
        this.fax = value;
    }

    /**
     * Gets the value of the building property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBuilding() {
        return building;
    }

    /**
     * Sets the value of the building property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBuilding(String value) {
        this.building = value;
    }

    /**
     * Gets the value of the floor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFloor() {
        return floor;
    }

    /**
     * Sets the value of the floor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFloor(String value) {
        this.floor = value;
    }

    /**
     * Gets the value of the room property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the value of the room property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoom(String value) {
        this.room = value;
    }

    /**
     * Gets the value of the mailbox property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailbox() {
        return mailbox;
    }

    /**
     * Sets the value of the mailbox property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailbox(String value) {
        this.mailbox = value;
    }

}
