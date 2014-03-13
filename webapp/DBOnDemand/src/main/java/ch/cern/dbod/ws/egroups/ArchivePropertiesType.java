
package ch.cern.dbod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArchivePropertiesType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ArchivePropertiesType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DoesNotExist"/>
 *     &lt;enumeration value="Active"/>
 *     &lt;enumeration value="NotActive"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ArchivePropertiesType")
@XmlEnum
public enum ArchivePropertiesType {

    @XmlEnumValue("DoesNotExist")
    DOES_NOT_EXIST("DoesNotExist"),
    @XmlEnumValue("Active")
    ACTIVE("Active"),
    @XmlEnumValue("NotActive")
    NOT_ACTIVE("NotActive");
    private final String value;

    ArchivePropertiesType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ArchivePropertiesType fromValue(String v) {
        for (ArchivePropertiesType c: ArchivePropertiesType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
