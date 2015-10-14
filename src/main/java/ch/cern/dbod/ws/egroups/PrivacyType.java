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
@XmlType(name = "PrivacyType")
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
