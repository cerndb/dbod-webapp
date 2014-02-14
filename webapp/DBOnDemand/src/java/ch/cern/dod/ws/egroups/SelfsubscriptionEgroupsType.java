
package ch.cern.dod.ws.egroups;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SelfsubscriptionEgroupsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SelfsubscriptionEgroupsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="SelfsubscriptionEgroups" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}SelfsubscriptionEgroupType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SelfsubscriptionEgroupsType", propOrder = {
    "selfsubscriptionEgroups"
})
public class SelfsubscriptionEgroupsType {

    @XmlElement(name = "SelfsubscriptionEgroups")
    protected List<SelfsubscriptionEgroupType> selfsubscriptionEgroups;

    /**
     * Gets the value of the selfsubscriptionEgroups property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the selfsubscriptionEgroups property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSelfsubscriptionEgroups().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SelfsubscriptionEgroupType }
     * 
     * 
     */
    public List<SelfsubscriptionEgroupType> getSelfsubscriptionEgroups() {
        if (selfsubscriptionEgroups == null) {
            selfsubscriptionEgroups = new ArrayList<SelfsubscriptionEgroupType>();
        }
        return this.selfsubscriptionEgroups;
    }

}
