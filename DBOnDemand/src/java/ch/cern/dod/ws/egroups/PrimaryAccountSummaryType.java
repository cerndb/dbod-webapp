/**
 * PrimaryAccountSummaryType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class PrimaryAccountSummaryType  extends ch.cern.dod.ws.egroups.AccountSummaryType  implements java.io.Serializable {
    private boolean hasMailbox;

    public PrimaryAccountSummaryType() {
    }

    public PrimaryAccountSummaryType(
           long ID,
           java.lang.String name,
           ch.cern.dod.ws.egroups.ResourceStatusCode status,
           java.lang.String service,
           ch.cern.dod.ws.egroups.AccountGroupQuotaType primaryAccountGroup,
           boolean hasMailbox) {
        super(
            ID,
            name,
            status,
            service,
            primaryAccountGroup);
        this.hasMailbox = hasMailbox;
    }


    /**
     * Gets the hasMailbox value for this PrimaryAccountSummaryType.
     * 
     * @return hasMailbox
     */
    public boolean isHasMailbox() {
        return hasMailbox;
    }


    /**
     * Sets the hasMailbox value for this PrimaryAccountSummaryType.
     * 
     * @param hasMailbox
     */
    public void setHasMailbox(boolean hasMailbox) {
        this.hasMailbox = hasMailbox;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof PrimaryAccountSummaryType)) return false;
        PrimaryAccountSummaryType other = (PrimaryAccountSummaryType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.hasMailbox == other.isHasMailbox();
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
        _hashCode += (isHasMailbox() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PrimaryAccountSummaryType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "PrimaryAccountSummaryType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hasMailbox");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "HasMailbox"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
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
