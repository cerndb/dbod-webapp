
package ch.cern.dbod.ws.egroups;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MailPostingRestrictionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MailPostingRestrictionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="PostingRestrictions" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}PostingRestrictionType"/>
 *         &lt;element name="OtherRecipientsAllowedToPost" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}MemberType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailPostingRestrictionType", propOrder = {
    "postingRestrictions",
    "otherRecipientsAllowedToPost"
})
public class MailPostingRestrictionType {

    @XmlElement(name = "PostingRestrictions", required = true)
    protected PostingRestrictionType postingRestrictions;
    @XmlElement(name = "OtherRecipientsAllowedToPost")
    protected List<MemberType> otherRecipientsAllowedToPost;

    /**
     * Gets the value of the postingRestrictions property.
     * 
     * @return
     *     possible object is
     *     {@link PostingRestrictionType }
     *     
     */
    public PostingRestrictionType getPostingRestrictions() {
        return postingRestrictions;
    }

    /**
     * Sets the value of the postingRestrictions property.
     * 
     * @param value
     *     allowed object is
     *     {@link PostingRestrictionType }
     *     
     */
    public void setPostingRestrictions(PostingRestrictionType value) {
        this.postingRestrictions = value;
    }

    /**
     * Gets the value of the otherRecipientsAllowedToPost property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the otherRecipientsAllowedToPost property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOtherRecipientsAllowedToPost().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MemberType }
     * 
     * 
     */
    public List<MemberType> getOtherRecipientsAllowedToPost() {
        if (otherRecipientsAllowedToPost == null) {
            otherRecipientsAllowedToPost = new ArrayList<MemberType>();
        }
        return this.otherRecipientsAllowedToPost;
    }

}
