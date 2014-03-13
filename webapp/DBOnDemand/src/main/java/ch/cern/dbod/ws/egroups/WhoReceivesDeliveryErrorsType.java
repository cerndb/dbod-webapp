
package ch.cern.dbod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for WhoReceivesDeliveryErrorsType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="WhoReceivesDeliveryErrorsType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="GroupOwner"/>
 *     &lt;enumeration value="Sender"/>
 *     &lt;enumeration value="None"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "WhoReceivesDeliveryErrorsType")
@XmlEnum
public enum WhoReceivesDeliveryErrorsType {

    @XmlEnumValue("GroupOwner")
    GROUP_OWNER("GroupOwner"),
    @XmlEnumValue("Sender")
    SENDER("Sender"),
    @XmlEnumValue("None")
    NONE("None");
    private final String value;

    WhoReceivesDeliveryErrorsType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static WhoReceivesDeliveryErrorsType fromValue(String v) {
        for (WhoReceivesDeliveryErrorsType c: WhoReceivesDeliveryErrorsType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
