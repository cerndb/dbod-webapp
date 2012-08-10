/**
 * GemType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups.cra;

public class GemType  implements java.io.Serializable {
    private java.lang.String emailAddress;

    private boolean displayInPhonebook;

    public GemType() {
    }

    public GemType(
           java.lang.String emailAddress,
           boolean displayInPhonebook) {
           this.emailAddress = emailAddress;
           this.displayInPhonebook = displayInPhonebook;
    }


    /**
     * Gets the emailAddress value for this GemType.
     * 
     * @return emailAddress
     */
    public java.lang.String getEmailAddress() {
        return emailAddress;
    }


    /**
     * Sets the emailAddress value for this GemType.
     * 
     * @param emailAddress
     */
    public void setEmailAddress(java.lang.String emailAddress) {
        this.emailAddress = emailAddress;
    }


    /**
     * Gets the displayInPhonebook value for this GemType.
     * 
     * @return displayInPhonebook
     */
    public boolean isDisplayInPhonebook() {
        return displayInPhonebook;
    }


    /**
     * Sets the displayInPhonebook value for this GemType.
     * 
     * @param displayInPhonebook
     */
    public void setDisplayInPhonebook(boolean displayInPhonebook) {
        this.displayInPhonebook = displayInPhonebook;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof GemType)) return false;
        GemType other = (GemType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.emailAddress==null && other.getEmailAddress()==null) || 
             (this.emailAddress!=null &&
              this.emailAddress.equals(other.getEmailAddress()))) &&
            this.displayInPhonebook == other.isDisplayInPhonebook();
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
        if (getEmailAddress() != null) {
            _hashCode += getEmailAddress().hashCode();
        }
        _hashCode += (isDisplayInPhonebook() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GemType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "GemType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailAddress");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EmailAddress"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("displayInPhonebook");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "DisplayInPhonebook"));
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
