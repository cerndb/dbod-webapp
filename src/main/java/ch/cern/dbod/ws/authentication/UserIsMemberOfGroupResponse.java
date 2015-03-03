
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
 *         &lt;element name="UserIsMemberOfGroupResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "userIsMemberOfGroupResult"
})
@XmlRootElement(name = "UserIsMemberOfGroupResponse")
public class UserIsMemberOfGroupResponse {

    @XmlElement(name = "UserIsMemberOfGroupResult")
    protected boolean userIsMemberOfGroupResult;

    /**
     * Gets the value of the userIsMemberOfGroupResult property.
     * 
     */
    public boolean isUserIsMemberOfGroupResult() {
        return userIsMemberOfGroupResult;
    }

    /**
     * Sets the value of the userIsMemberOfGroupResult property.
     * 
     */
    public void setUserIsMemberOfGroupResult(boolean value) {
        this.userIsMemberOfGroupResult = value;
    }

}
