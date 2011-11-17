/**
 * UserInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.authentication;

public class UserInfo  implements java.io.Serializable {
    private int auth;

    private int ccid;

    private int respccid;

    private java.lang.String login;

    private java.lang.String email;

    private java.lang.String name;

    private java.lang.String firstname;

    private java.lang.String lastname;

    private java.lang.String telephonenumber;

    private java.lang.String company;

    private java.lang.String department;

    private java.lang.String shortaffiliation;

    private java.lang.String errorMessage;

    public UserInfo() {
    }

    public UserInfo(
           int auth,
           int ccid,
           int respccid,
           java.lang.String login,
           java.lang.String email,
           java.lang.String name,
           java.lang.String firstname,
           java.lang.String lastname,
           java.lang.String telephonenumber,
           java.lang.String company,
           java.lang.String department,
           java.lang.String shortaffiliation,
           java.lang.String errorMessage) {
           this.auth = auth;
           this.ccid = ccid;
           this.respccid = respccid;
           this.login = login;
           this.email = email;
           this.name = name;
           this.firstname = firstname;
           this.lastname = lastname;
           this.telephonenumber = telephonenumber;
           this.company = company;
           this.department = department;
           this.shortaffiliation = shortaffiliation;
           this.errorMessage = errorMessage;
    }


    /**
     * Gets the auth value for this UserInfo.
     * 
     * @return auth
     */
    public int getAuth() {
        return auth;
    }


    /**
     * Sets the auth value for this UserInfo.
     * 
     * @param auth
     */
    public void setAuth(int auth) {
        this.auth = auth;
    }


    /**
     * Gets the ccid value for this UserInfo.
     * 
     * @return ccid
     */
    public int getCcid() {
        return ccid;
    }


    /**
     * Sets the ccid value for this UserInfo.
     * 
     * @param ccid
     */
    public void setCcid(int ccid) {
        this.ccid = ccid;
    }


    /**
     * Gets the respccid value for this UserInfo.
     * 
     * @return respccid
     */
    public int getRespccid() {
        return respccid;
    }


    /**
     * Sets the respccid value for this UserInfo.
     * 
     * @param respccid
     */
    public void setRespccid(int respccid) {
        this.respccid = respccid;
    }


    /**
     * Gets the login value for this UserInfo.
     * 
     * @return login
     */
    public java.lang.String getLogin() {
        return login;
    }


    /**
     * Sets the login value for this UserInfo.
     * 
     * @param login
     */
    public void setLogin(java.lang.String login) {
        this.login = login;
    }


    /**
     * Gets the email value for this UserInfo.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this UserInfo.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the name value for this UserInfo.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this UserInfo.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the firstname value for this UserInfo.
     * 
     * @return firstname
     */
    public java.lang.String getFirstname() {
        return firstname;
    }


    /**
     * Sets the firstname value for this UserInfo.
     * 
     * @param firstname
     */
    public void setFirstname(java.lang.String firstname) {
        this.firstname = firstname;
    }


    /**
     * Gets the lastname value for this UserInfo.
     * 
     * @return lastname
     */
    public java.lang.String getLastname() {
        return lastname;
    }


    /**
     * Sets the lastname value for this UserInfo.
     * 
     * @param lastname
     */
    public void setLastname(java.lang.String lastname) {
        this.lastname = lastname;
    }


    /**
     * Gets the telephonenumber value for this UserInfo.
     * 
     * @return telephonenumber
     */
    public java.lang.String getTelephonenumber() {
        return telephonenumber;
    }


    /**
     * Sets the telephonenumber value for this UserInfo.
     * 
     * @param telephonenumber
     */
    public void setTelephonenumber(java.lang.String telephonenumber) {
        this.telephonenumber = telephonenumber;
    }


    /**
     * Gets the company value for this UserInfo.
     * 
     * @return company
     */
    public java.lang.String getCompany() {
        return company;
    }


    /**
     * Sets the company value for this UserInfo.
     * 
     * @param company
     */
    public void setCompany(java.lang.String company) {
        this.company = company;
    }


    /**
     * Gets the department value for this UserInfo.
     * 
     * @return department
     */
    public java.lang.String getDepartment() {
        return department;
    }


    /**
     * Sets the department value for this UserInfo.
     * 
     * @param department
     */
    public void setDepartment(java.lang.String department) {
        this.department = department;
    }


    /**
     * Gets the shortaffiliation value for this UserInfo.
     * 
     * @return shortaffiliation
     */
    public java.lang.String getShortaffiliation() {
        return shortaffiliation;
    }


    /**
     * Sets the shortaffiliation value for this UserInfo.
     * 
     * @param shortaffiliation
     */
    public void setShortaffiliation(java.lang.String shortaffiliation) {
        this.shortaffiliation = shortaffiliation;
    }


    /**
     * Gets the errorMessage value for this UserInfo.
     * 
     * @return errorMessage
     */
    public java.lang.String getErrorMessage() {
        return errorMessage;
    }


    /**
     * Sets the errorMessage value for this UserInfo.
     * 
     * @param errorMessage
     */
    public void setErrorMessage(java.lang.String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UserInfo)) return false;
        UserInfo other = (UserInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.auth == other.getAuth() &&
            this.ccid == other.getCcid() &&
            this.respccid == other.getRespccid() &&
            ((this.login==null && other.getLogin()==null) || 
             (this.login!=null &&
              this.login.equals(other.getLogin()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.firstname==null && other.getFirstname()==null) || 
             (this.firstname!=null &&
              this.firstname.equals(other.getFirstname()))) &&
            ((this.lastname==null && other.getLastname()==null) || 
             (this.lastname!=null &&
              this.lastname.equals(other.getLastname()))) &&
            ((this.telephonenumber==null && other.getTelephonenumber()==null) || 
             (this.telephonenumber!=null &&
              this.telephonenumber.equals(other.getTelephonenumber()))) &&
            ((this.company==null && other.getCompany()==null) || 
             (this.company!=null &&
              this.company.equals(other.getCompany()))) &&
            ((this.department==null && other.getDepartment()==null) || 
             (this.department!=null &&
              this.department.equals(other.getDepartment()))) &&
            ((this.shortaffiliation==null && other.getShortaffiliation()==null) || 
             (this.shortaffiliation!=null &&
              this.shortaffiliation.equals(other.getShortaffiliation()))) &&
            ((this.errorMessage==null && other.getErrorMessage()==null) || 
             (this.errorMessage!=null &&
              this.errorMessage.equals(other.getErrorMessage())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getAuth();
        _hashCode += getCcid();
        _hashCode += getRespccid();
        if (getLogin() != null) {
            _hashCode += getLogin().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getFirstname() != null) {
            _hashCode += getFirstname().hashCode();
        }
        if (getLastname() != null) {
            _hashCode += getLastname().hashCode();
        }
        if (getTelephonenumber() != null) {
            _hashCode += getTelephonenumber().hashCode();
        }
        if (getCompany() != null) {
            _hashCode += getCompany().hashCode();
        }
        if (getDepartment() != null) {
            _hashCode += getDepartment().hashCode();
        }
        if (getShortaffiliation() != null) {
            _hashCode += getShortaffiliation().hashCode();
        }
        if (getErrorMessage() != null) {
            _hashCode += getErrorMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UserInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "userInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auth");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "auth"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ccid");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "ccid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("respccid");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "respccid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("login");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "login"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstname");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "firstname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastname");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "lastname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telephonenumber");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "telephonenumber"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("company");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "company"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("department");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "department"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("shortaffiliation");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "shortaffiliation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("errorMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "ErrorMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
