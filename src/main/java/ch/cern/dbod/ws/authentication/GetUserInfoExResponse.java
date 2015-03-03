
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
 *         &lt;element name="GetUserInfoExResult" type="{https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx}userInfo" minOccurs="0"/>
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
    "getUserInfoExResult"
})
@XmlRootElement(name = "GetUserInfoExResponse")
public class GetUserInfoExResponse {

    @XmlElement(name = "GetUserInfoExResult")
    protected UserInfo getUserInfoExResult;

    /**
     * Gets the value of the getUserInfoExResult property.
     * 
     * @return
     *     possible object is
     *     {@link UserInfo }
     *     
     */
    public UserInfo getGetUserInfoExResult() {
        return getUserInfoExResult;
    }

    /**
     * Sets the value of the getUserInfoExResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserInfo }
     *     
     */
    public void setGetUserInfoExResult(UserInfo value) {
        this.getUserInfoExResult = value;
    }

}
