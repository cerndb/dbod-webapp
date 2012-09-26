/**
 * EgroupsType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups.cra;

public class EgroupsType  implements java.io.Serializable {
    private ch.cern.dod.ws.egroups.cra.EgroupType[] egroup;

    public EgroupsType() {
    }

    public EgroupsType(
           ch.cern.dod.ws.egroups.cra.EgroupType[] egroup) {
           this.egroup = egroup;
    }


    /**
     * Gets the egroup value for this EgroupsType.
     * 
     * @return egroup
     */
    public ch.cern.dod.ws.egroups.cra.EgroupType[] getEgroup() {
        return egroup;
    }


    /**
     * Sets the egroup value for this EgroupsType.
     * 
     * @param egroup
     */
    public void setEgroup(ch.cern.dod.ws.egroups.cra.EgroupType[] egroup) {
        this.egroup = egroup;
    }

    public ch.cern.dod.ws.egroups.cra.EgroupType getEgroup(int i) {
        return this.egroup[i];
    }

    public void setEgroup(int i, ch.cern.dod.ws.egroups.cra.EgroupType _value) {
        this.egroup[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EgroupsType)) return false;
        EgroupsType other = (EgroupsType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.egroup==null && other.getEgroup()==null) || 
             (this.egroup!=null &&
              java.util.Arrays.equals(this.egroup, other.getEgroup())));
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
        if (getEgroup() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEgroup());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEgroup(), i);
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
        new org.apache.axis.description.TypeDesc(EgroupsType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EgroupsType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("egroup");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Egroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EgroupType"));
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
