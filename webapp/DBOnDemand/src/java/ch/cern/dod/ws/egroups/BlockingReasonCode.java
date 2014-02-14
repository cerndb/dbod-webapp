
package ch.cern.dod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BlockingReasonCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="BlockingReasonCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Manual"/>
 *     &lt;enumeration value="Expired"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "BlockingReasonCode")
@XmlEnum
public enum BlockingReasonCode {

    @XmlEnumValue("Manual")
    MANUAL("Manual"),
    @XmlEnumValue("Expired")
    EXPIRED("Expired");
    private final String value;

    BlockingReasonCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static BlockingReasonCode fromValue(String v) {
        for (BlockingReasonCode c: BlockingReasonCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
