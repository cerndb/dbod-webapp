
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
 *         &lt;element name="Egroup" type="{https://cra-ws.cern.ch/cra-ws/cra/}SelfsubscriptionEgroupType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SelfsubscriptionEgroupsType", namespace = "https://cra-ws.cern.ch/cra-ws/cra/", propOrder = {
    "egroup"
})
public class SelfsubscriptionEgroupsType {

    @XmlElement(name = "Egroup", required = true)
    protected List<SelfsubscriptionEgroupType> egroup;

    /**
     * Gets the value of the egroup property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the egroup property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEgroup().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SelfsubscriptionEgroupType }
     * 
     * 
     */
    public List<SelfsubscriptionEgroupType> getEgroup() {
        if (egroup == null) {
            egroup = new ArrayList<SelfsubscriptionEgroupType>();
        }
        return this.egroup;
    }

}
