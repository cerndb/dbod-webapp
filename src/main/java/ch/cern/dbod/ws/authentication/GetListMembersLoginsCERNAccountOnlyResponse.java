/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ws.authentication;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetListMembersLogins_CERNAccountOnlyResult" type="{https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx}ArrayOfString" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getListMembersLoginsCERNAccountOnlyResult"
})
@XmlRootElement(name = "GetListMembersLogins_CERNAccountOnlyResponse")
public class GetListMembersLoginsCERNAccountOnlyResponse {

    @XmlElement(name = "GetListMembersLogins_CERNAccountOnlyResult")
    protected ArrayOfString getListMembersLoginsCERNAccountOnlyResult;

    /**
     * Gets the value of the getListMembersLoginsCERNAccountOnlyResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getGetListMembersLoginsCERNAccountOnlyResult() {
        return getListMembersLoginsCERNAccountOnlyResult;
    }

    /**
     * Sets the value of the getListMembersLoginsCERNAccountOnlyResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setGetListMembersLoginsCERNAccountOnlyResult(ArrayOfString value) {
        this.getListMembersLoginsCERNAccountOnlyResult = value;
    }

}
