/**
 * SynchronizeEgroupRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class SynchronizeEgroupRequest  implements java.io.Serializable {
    private java.lang.String p_niceUserid;

    private java.lang.String p_password;

    private ch.cern.dod.ws.egroups.EgroupType p_egroup;

    public SynchronizeEgroupRequest() {
    }

    public SynchronizeEgroupRequest(
           java.lang.String p_niceUserid,
           java.lang.String p_password,
           ch.cern.dod.ws.egroups.EgroupType p_egroup) {
           this.p_niceUserid = p_niceUserid;
           this.p_password = p_password;
           this.p_egroup = p_egroup;
    }


    /**
     * Gets the p_niceUserid value for this SynchronizeEgroupRequest.
     * 
     * @return p_niceUserid
     */
    public java.lang.String getP_niceUserid() {
        return p_niceUserid;
    }


    /**
     * Sets the p_niceUserid value for this SynchronizeEgroupRequest.
     * 
     * @param p_niceUserid
     */
    public void setP_niceUserid(java.lang.String p_niceUserid) {
        this.p_niceUserid = p_niceUserid;
    }


    /**
     * Gets the p_password value for this SynchronizeEgroupRequest.
     * 
     * @return p_password
     */
    public java.lang.String getP_password() {
        return p_password;
    }


    /**
     * Sets the p_password value for this SynchronizeEgroupRequest.
     * 
     * @param p_password
     */
    public void setP_password(java.lang.String p_password) {
        this.p_password = p_password;
    }


    /**
     * Gets the p_egroup value for this SynchronizeEgroupRequest.
     * 
     * @return p_egroup
     */
    public ch.cern.dod.ws.egroups.EgroupType getP_egroup() {
        return p_egroup;
    }


    /**
     * Sets the p_egroup value for this SynchronizeEgroupRequest.
     * 
     * @param p_egroup
     */
    public void setP_egroup(ch.cern.dod.ws.egroups.EgroupType p_egroup) {
        this.p_egroup = p_egroup;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SynchronizeEgroupRequest)) return false;
        SynchronizeEgroupRequest other = (SynchronizeEgroupRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.p_niceUserid==null && other.getP_niceUserid()==null) || 
             (this.p_niceUserid!=null &&
              this.p_niceUserid.equals(other.getP_niceUserid()))) &&
            ((this.p_password==null && other.getP_password()==null) || 
             (this.p_password!=null &&
              this.p_password.equals(other.getP_password()))) &&
            ((this.p_egroup==null && other.getP_egroup()==null) || 
             (this.p_egroup!=null &&
              this.p_egroup.equals(other.getP_egroup())));
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
        if (getP_niceUserid() != null) {
            _hashCode += getP_niceUserid().hashCode();
        }
        if (getP_password() != null) {
            _hashCode += getP_password().hashCode();
        }
        if (getP_egroup() != null) {
            _hashCode += getP_egroup().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SynchronizeEgroupRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">synchronizeEgroupRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("p_niceUserid");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "p_niceUserid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("p_password");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "p_password"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("p_egroup");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "p_egroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "EgroupType"));
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
