/**
 * AccountGroupQuotaType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class AccountGroupQuotaType  extends ch.cern.dod.ws.egroups.AccountGroupType  implements java.io.Serializable {
    private java.lang.Long allocatedQuota;

    public AccountGroupQuotaType() {
    }

    public AccountGroupQuotaType(
           ch.cern.dod.ws.egroups.AdminGroup adminGroup,
           java.lang.Long allocatedQuota) {
        super(
            adminGroup);
        this.allocatedQuota = allocatedQuota;
    }


    /**
     * Gets the allocatedQuota value for this AccountGroupQuotaType.
     * 
     * @return allocatedQuota
     */
    public java.lang.Long getAllocatedQuota() {
        return allocatedQuota;
    }


    /**
     * Sets the allocatedQuota value for this AccountGroupQuotaType.
     * 
     * @param allocatedQuota
     */
    public void setAllocatedQuota(java.lang.Long allocatedQuota) {
        this.allocatedQuota = allocatedQuota;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AccountGroupQuotaType)) return false;
        AccountGroupQuotaType other = (AccountGroupQuotaType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.allocatedQuota==null && other.getAllocatedQuota()==null) || 
             (this.allocatedQuota!=null &&
              this.allocatedQuota.equals(other.getAllocatedQuota())));
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
        if (getAllocatedQuota() != null) {
            _hashCode += getAllocatedQuota().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AccountGroupQuotaType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "AccountGroupQuotaType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("allocatedQuota");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "AllocatedQuota"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
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
