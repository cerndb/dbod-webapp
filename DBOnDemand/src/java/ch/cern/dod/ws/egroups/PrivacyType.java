
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrivacyType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PrivacyType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Members"/>
 *     &lt;enumeration value="Open"/>
 *     &lt;enumeration value="Administrators"/>
 *     &lt;enumeration value="Users"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PrivacyType", namespace = "https://cra-ws.cern.ch/cra-ws/cra/")
@XmlEnum
public enum PrivacyType {

    @XmlEnumValue("Members")
    MEMBERS("Members"),
    @XmlEnumValue("Open")
    OPEN("Open"),
    @XmlEnumValue("Administrators")
    ADMINISTRATORS("Administrators"),
    @XmlEnumValue("Users")
    USERS("Users");
    private final String value;

    PrivacyType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PrivacyType fromValue(String v) {
        for (PrivacyType c: PrivacyType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
