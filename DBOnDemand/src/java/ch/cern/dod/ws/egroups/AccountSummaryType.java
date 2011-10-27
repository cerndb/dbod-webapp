/**
 * AccountSummaryType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class AccountSummaryType  extends ch.cern.dod.ws.egroups.ResourceSummaryType  implements java.io.Serializable {
    private java.lang.String service;

    private ch.cern.dod.ws.egroups.AccountGroupQuotaType primaryAccountGroup;

    public AccountSummaryType() {
    }

    public AccountSummaryType(
           long ID,
           java.lang.String name,
           ch.cern.dod.ws.egroups.ResourceStatusCode status,
           java.lang.String service,
           ch.cern.dod.ws.egroups.AccountGroupQuotaType primaryAccountGroup) {
        super(
            ID,
            name,
            status);
        this.service = service;
        this.primaryAccountGroup = primaryAccountGroup;
    }


    /**
     * Gets the service value for this AccountSummaryType.
     * 
     * @return service
     */
    public java.lang.String getService() {
        return service;
    }


    /**
     * Sets the service value for this AccountSummaryType.
     * 
     * @param service
     */
    public void setService(java.lang.String service) {
        this.service = service;
    }


    /**
     * Gets the primaryAccountGroup value for this AccountSummaryType.
     * 
     * @return primaryAccountGroup
     */
    public ch.cern.dod.ws.egroups.AccountGroupQuotaType getPrimaryAccountGroup() {
        return primaryAccountGroup;
    }


    /**
     * Sets the primaryAccountGroup value for this AccountSummaryType.
     * 
     * @param primaryAccountGroup
     */
    public void setPrimaryAccountGroup(ch.cern.dod.ws.egroups.AccountGroupQuotaType primaryAccountGroup) {
        this.primaryAccountGroup = primaryAccountGroup;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AccountSummaryType)) return false;
        AccountSummaryType other = (AccountSummaryType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.service==null && other.getService()==null) || 
             (this.service!=null &&
              this.service.equals(other.getService()))) &&
            ((this.primaryAccountGroup==null && other.getPrimaryAccountGroup()==null) || 
             (this.primaryAccountGroup!=null &&
              this.primaryAccountGroup.equals(other.getPrimaryAccountGroup())));
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
        if (getService() != null) {
            _hashCode += getService().hashCode();
        }
        if (getPrimaryAccountGroup() != null) {
            _hashCode += getPrimaryAccountGroup().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AccountSummaryType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "AccountSummaryType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("service");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Service"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryAccountGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "PrimaryAccountGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "AccountGroupQuotaType"));
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
