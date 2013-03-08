
package ch.cern.dod.ws.egroups;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EgroupsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EgroupsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Egroup" type="{https://cra-ws.cern.ch/cra-ws/cra/}EgroupType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EgroupsType", namespace = "https://cra-ws.cern.ch/cra-ws/cra/", propOrder = {
    "egroup"
})
public class EgroupsType {

    @XmlElement(name = "Egroup")
    protected List<EgroupType> egroup;

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
     * {@link EgroupType }
     * 
     * 
     */
    public List<EgroupType> getEgroup() {
        if (egroup == null) {
            egroup = new ArrayList<EgroupType>();
        }
        return this.egroup;
    }

}
