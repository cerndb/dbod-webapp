
package ch.cern.dod.ws.authentication;

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
 *         &lt;element name="UserIsMemberOfListResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "userIsMemberOfListResult"
})
@XmlRootElement(name = "UserIsMemberOfListResponse")
public class UserIsMemberOfListResponse {

    @XmlElement(name = "UserIsMemberOfListResult")
    protected boolean userIsMemberOfListResult;

    /**
     * Gets the value of the userIsMemberOfListResult property.
     * 
     */
    public boolean isUserIsMemberOfListResult() {
        return userIsMemberOfListResult;
    }

    /**
     * Sets the value of the userIsMemberOfListResult property.
     * 
     */
    public void setUserIsMemberOfListResult(boolean value) {
        this.userIsMemberOfListResult = value;
    }

}
