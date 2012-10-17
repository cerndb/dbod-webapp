
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for UserTypeCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="UserTypeCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Person"/>
 *     &lt;enumeration value="ServiceProvider"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "UserTypeCode", namespace = "https://cra-ws.cern.ch/cra-ws/cra/")
@XmlEnum
public enum UserTypeCode {

    @XmlEnumValue("Person")
    PERSON("Person"),
    @XmlEnumValue("ServiceProvider")
    SERVICE_PROVIDER("ServiceProvider");
    private final String value;

    UserTypeCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static UserTypeCode fromValue(String v) {
        for (UserTypeCode c: UserTypeCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
