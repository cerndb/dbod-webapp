/**
 * UserTypeWithAccounts.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class UserTypeWithAccounts  extends ch.cern.dod.ws.egroups.UserType  implements java.io.Serializable {
    private ch.cern.dod.ws.egroups.PrimaryAccountSummaryType primaryAccount;

    private ch.cern.dod.ws.egroups.AccountSummaryType[] secondaryAccount;

    public UserTypeWithAccounts() {
    }

    public UserTypeWithAccounts(
           long CCID,
           ch.cern.dod.ws.egroups.UserTypeCode type,
           java.lang.String name,
           java.lang.Boolean computingRulesSigned,
           java.lang.String pem,
           ch.cern.dod.ws.egroups.GemType primaryGem,
           ch.cern.dod.ws.egroups.GemType[] secondaryGems,
           java.lang.String cernUnit,
           java.lang.String cernDepartment,
           java.lang.String cernGroup,
           java.lang.String telephone1,
           java.lang.String fax,
           java.lang.String building,
           java.lang.String floor,
           java.lang.String room,
           java.lang.String mailbox,
           ch.cern.dod.ws.egroups.PrimaryAccountSummaryType primaryAccount,
           ch.cern.dod.ws.egroups.AccountSummaryType[] secondaryAccount) {
        super(
            CCID,
            type,
            name,
            computingRulesSigned,
            pem,
            primaryGem,
            secondaryGems,
            cernUnit,
            cernDepartment,
            cernGroup,
            telephone1,
            fax,
            building,
            floor,
            room,
            mailbox);
        this.primaryAccount = primaryAccount;
        this.secondaryAccount = secondaryAccount;
    }


    /**
     * Gets the primaryAccount value for this UserTypeWithAccounts.
     * 
     * @return primaryAccount
     */
    public ch.cern.dod.ws.egroups.PrimaryAccountSummaryType getPrimaryAccount() {
        return primaryAccount;
    }


    /**
     * Sets the primaryAccount value for this UserTypeWithAccounts.
     * 
     * @param primaryAccount
     */
    public void setPrimaryAccount(ch.cern.dod.ws.egroups.PrimaryAccountSummaryType primaryAccount) {
        this.primaryAccount = primaryAccount;
    }


    /**
     * Gets the secondaryAccount value for this UserTypeWithAccounts.
     * 
     * @return secondaryAccount
     */
    public ch.cern.dod.ws.egroups.AccountSummaryType[] getSecondaryAccount() {
        return secondaryAccount;
    }


    /**
     * Sets the secondaryAccount value for this UserTypeWithAccounts.
     * 
     * @param secondaryAccount
     */
    public void setSecondaryAccount(ch.cern.dod.ws.egroups.AccountSummaryType[] secondaryAccount) {
        this.secondaryAccount = secondaryAccount;
    }

    public ch.cern.dod.ws.egroups.AccountSummaryType getSecondaryAccount(int i) {
        return this.secondaryAccount[i];
    }

    public void setSecondaryAccount(int i, ch.cern.dod.ws.egroups.AccountSummaryType _value) {
        this.secondaryAccount[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UserTypeWithAccounts)) return false;
        UserTypeWithAccounts other = (UserTypeWithAccounts) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.primaryAccount==null && other.getPrimaryAccount()==null) || 
             (this.primaryAccount!=null &&
              this.primaryAccount.equals(other.getPrimaryAccount()))) &&
            ((this.secondaryAccount==null && other.getSecondaryAccount()==null) || 
             (this.secondaryAccount!=null &&
              java.util.Arrays.equals(this.secondaryAccount, other.getSecondaryAccount())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getPrimaryAccount() != null) {
            _hashCode += getPrimaryAccount().hashCode();
        }
        if (getSecondaryAccount() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSecondaryAccount());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSecondaryAccount(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UserTypeWithAccounts.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "UserTypeWithAccounts"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryAccount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "PrimaryAccount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "PrimaryAccountSummaryType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("secondaryAccount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "SecondaryAccount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "AccountSummaryType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
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
