
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SelfsubscriptionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="SelfsubscriptionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Closed"/>
 *     &lt;enumeration value="Open"/>
 *     &lt;enumeration value="Members"/>
 *     &lt;enumeration value="Users"/>
 *     &lt;enumeration value="OpenWithAdminApproval"/>
 *     &lt;enumeration value="UsersWithAdminApproval"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SelfsubscriptionType")
@XmlEnum
public enum SelfsubscriptionType {

    @XmlEnumValue("Closed")
    CLOSED("Closed"),
    @XmlEnumValue("Open")
    OPEN("Open"),
    @XmlEnumValue("Members")
    MEMBERS("Members"),
    @XmlEnumValue("Users")
    USERS("Users"),
    @XmlEnumValue("OpenWithAdminApproval")
    OPEN_WITH_ADMIN_APPROVAL("OpenWithAdminApproval"),
    @XmlEnumValue("UsersWithAdminApproval")
    USERS_WITH_ADMIN_APPROVAL("UsersWithAdminApproval");
    private final String value;

    SelfsubscriptionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static SelfsubscriptionType fromValue(String v) {
        for (SelfsubscriptionType c: SelfsubscriptionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
