/**
 * SelfsubscriptionEgroupType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups.cra;

public class SelfsubscriptionEgroupType  implements java.io.Serializable {
    private java.lang.String name;

    private long ID;

    private ch.cern.dod.ws.egroups.cra.EgroupTypeCode type;

    private boolean approvalNeeded;

    public SelfsubscriptionEgroupType() {
    }

    public SelfsubscriptionEgroupType(
           java.lang.String name,
           long ID,
           ch.cern.dod.ws.egroups.cra.EgroupTypeCode type,
           boolean approvalNeeded) {
           this.name = name;
           this.ID = ID;
           this.type = type;
           this.approvalNeeded = approvalNeeded;
    }


    /**
     * Gets the name value for this SelfsubscriptionEgroupType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this SelfsubscriptionEgroupType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the ID value for this SelfsubscriptionEgroupType.
     * 
     * @return ID
     */
    public long getID() {
        return ID;
    }


    /**
     * Sets the ID value for this SelfsubscriptionEgroupType.
     * 
     * @param ID
     */
    public void setID(long ID) {
        this.ID = ID;
    }


    /**
     * Gets the type value for this SelfsubscriptionEgroupType.
     * 
     * @return type
     */
    public ch.cern.dod.ws.egroups.cra.EgroupTypeCode getType() {
        return type;
    }


    /**
     * Sets the type value for this SelfsubscriptionEgroupType.
     * 
     * @param type
     */
    public void setType(ch.cern.dod.ws.egroups.cra.EgroupTypeCode type) {
        this.type = type;
    }


    /**
     * Gets the approvalNeeded value for this SelfsubscriptionEgroupType.
     * 
     * @return approvalNeeded
     */
    public boolean isApprovalNeeded() {
        return approvalNeeded;
    }


    /**
     * Sets the approvalNeeded value for this SelfsubscriptionEgroupType.
     * 
     * @param approvalNeeded
     */
    public void setApprovalNeeded(boolean approvalNeeded) {
        this.approvalNeeded = approvalNeeded;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SelfsubscriptionEgroupType)) return false;
        SelfsubscriptionEgroupType other = (SelfsubscriptionEgroupType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            this.ID == other.getID() &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            this.approvalNeeded == other.isApprovalNeeded();
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        _hashCode += new Long(getID()).hashCode();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        _hashCode += (isApprovalNeeded() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SelfsubscriptionEgroupType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "SelfsubscriptionEgroupType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EgroupTypeCode"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("approvalNeeded");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "ApprovalNeeded"));
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
