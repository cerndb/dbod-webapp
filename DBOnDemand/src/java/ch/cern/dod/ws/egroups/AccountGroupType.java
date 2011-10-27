/**
 * AccountGroupType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class AccountGroupType  implements java.io.Serializable {
    private ch.cern.dod.ws.egroups.AdminGroup adminGroup;

    public AccountGroupType() {
    }

    public AccountGroupType(
           ch.cern.dod.ws.egroups.AdminGroup adminGroup) {
           this.adminGroup = adminGroup;
    }


    /**
     * Gets the adminGroup value for this AccountGroupType.
     * 
     * @return adminGroup
     */
    public ch.cern.dod.ws.egroups.AdminGroup getAdminGroup() {
        return adminGroup;
    }


    /**
     * Sets the adminGroup value for this AccountGroupType.
     * 
     * @param adminGroup
     */
    public void setAdminGroup(ch.cern.dod.ws.egroups.AdminGroup adminGroup) {
        this.adminGroup = adminGroup;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AccountGroupType)) return false;
        AccountGroupType other = (AccountGroupType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.adminGroup==null && other.getAdminGroup()==null) || 
             (this.adminGroup!=null &&
              this.adminGroup.equals(other.getAdminGroup())));
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
        if (getAdminGroup() != null) {
            _hashCode += getAdminGroup().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AccountGroupType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "AccountGroupType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adminGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "AdminGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", ">AdminGroup"));
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
