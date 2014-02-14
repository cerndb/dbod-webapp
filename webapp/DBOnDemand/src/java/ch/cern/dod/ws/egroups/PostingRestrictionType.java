
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PostingRestrictionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="PostingRestrictionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Everyone"/>
 *     &lt;enumeration value="CernUsers"/>
 *     &lt;enumeration value="OwnerAdminsAndOthers"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PostingRestrictionType")
@XmlEnum
public enum PostingRestrictionType {

    @XmlEnumValue("Everyone")
    EVERYONE("Everyone"),
    @XmlEnumValue("CernUsers")
    CERN_USERS("CernUsers"),
    @XmlEnumValue("OwnerAdminsAndOthers")
    OWNER_ADMINS_AND_OTHERS("OwnerAdminsAndOthers");
    private final String value;

    PostingRestrictionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PostingRestrictionType fromValue(String v) {
        for (PostingRestrictionType c: PostingRestrictionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
