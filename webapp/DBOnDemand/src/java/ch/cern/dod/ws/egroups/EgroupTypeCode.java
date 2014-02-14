
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for EgroupTypeCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="EgroupTypeCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="StaticEgroup"/>
 *     &lt;enumeration value="DynamicEgroup"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "EgroupTypeCode")
@XmlEnum
public enum EgroupTypeCode {

    @XmlEnumValue("StaticEgroup")
    STATIC_EGROUP("StaticEgroup"),
    @XmlEnumValue("DynamicEgroup")
    DYNAMIC_EGROUP("DynamicEgroup");
    private final String value;

    EgroupTypeCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EgroupTypeCode fromValue(String v) {
        for (EgroupTypeCode c: EgroupTypeCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
