/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MemberTypeCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="MemberTypeCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="External"/>
 *     &lt;enumeration value="Person"/>
 *     &lt;enumeration value="ServiceProvider"/>
 *     &lt;enumeration value="StaticEgroup"/>
 *     &lt;enumeration value="DynamicEgroup"/>
 *     &lt;enumeration value="Account"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "MemberTypeCode")
@XmlEnum
public enum MemberTypeCode {

    @XmlEnumValue("External")
    EXTERNAL("External"),
    @XmlEnumValue("Person")
    PERSON("Person"),
    @XmlEnumValue("ServiceProvider")
    SERVICE_PROVIDER("ServiceProvider"),
    @XmlEnumValue("StaticEgroup")
    STATIC_EGROUP("StaticEgroup"),
    @XmlEnumValue("DynamicEgroup")
    DYNAMIC_EGROUP("DynamicEgroup"),
    @XmlEnumValue("Account")
    ACCOUNT("Account");
    private final String value;

    MemberTypeCode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static MemberTypeCode fromValue(String v) {
        for (MemberTypeCode c: MemberTypeCode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
