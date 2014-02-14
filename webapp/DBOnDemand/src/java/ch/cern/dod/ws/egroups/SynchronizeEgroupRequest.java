
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
 *         &lt;element name="egroup" type="{https://foundservices.cern.ch/ws/egroups/v1/schema/EgroupsServicesSchema}EgroupType"/>
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
    "egroup"
})
@XmlRootElement(name = "SynchronizeEgroupRequest")
public class SynchronizeEgroupRequest {

    @XmlElement(required = true)
    protected EgroupType egroup;

    /**
     * Gets the value of the egroup property.
     * 
     * @return
     *     possible object is
     *     {@link EgroupType }
     *     
     */
    public EgroupType getEgroup() {
        return egroup;
    }

    /**
     * Sets the value of the egroup property.
     * 
     * @param value
     *     allowed object is
     *     {@link EgroupType }
     *     
     */
    public void setEgroup(EgroupType value) {
        this.egroup = value;
    }

}
