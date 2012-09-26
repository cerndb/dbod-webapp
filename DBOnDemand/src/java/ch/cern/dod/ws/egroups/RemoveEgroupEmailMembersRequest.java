/**
 * RemoveEgroupEmailMembersRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class RemoveEgroupEmailMembersRequest  implements java.io.Serializable {
    private java.lang.String p_niceUserid;

    private java.lang.String p_password;

    private java.lang.String p_egroupName;

    private ch.cern.dod.ws.egroups.cra.EmailMembersType p_members;

    public RemoveEgroupEmailMembersRequest() {
    }

    public RemoveEgroupEmailMembersRequest(
           java.lang.String p_niceUserid,
           java.lang.String p_password,
           java.lang.String p_egroupName,
           ch.cern.dod.ws.egroups.cra.EmailMembersType p_members) {
           this.p_niceUserid = p_niceUserid;
           this.p_password = p_password;
           this.p_egroupName = p_egroupName;
           this.p_members = p_members;
    }


    /**
     * Gets the p_niceUserid value for this RemoveEgroupEmailMembersRequest.
     * 
     * @return p_niceUserid
     */
    public java.lang.String getP_niceUserid() {
        return p_niceUserid;
    }


    /**
     * Sets the p_niceUserid value for this RemoveEgroupEmailMembersRequest.
     * 
     * @param p_niceUserid
     */
    public void setP_niceUserid(java.lang.String p_niceUserid) {
        this.p_niceUserid = p_niceUserid;
    }


    /**
     * Gets the p_password value for this RemoveEgroupEmailMembersRequest.
     * 
     * @return p_password
     */
    public java.lang.String getP_password() {
        return p_password;
    }


    /**
     * Sets the p_password value for this RemoveEgroupEmailMembersRequest.
     * 
     * @param p_password
     */
    public void setP_password(java.lang.String p_password) {
        this.p_password = p_password;
    }


    /**
     * Gets the p_egroupName value for this RemoveEgroupEmailMembersRequest.
     * 
     * @return p_egroupName
     */
    public java.lang.String getP_egroupName() {
        return p_egroupName;
    }


    /**
     * Sets the p_egroupName value for this RemoveEgroupEmailMembersRequest.
     * 
     * @param p_egroupName
     */
    public void setP_egroupName(java.lang.String p_egroupName) {
        this.p_egroupName = p_egroupName;
    }


    /**
     * Gets the p_members value for this RemoveEgroupEmailMembersRequest.
     * 
     * @return p_members
     */
    public ch.cern.dod.ws.egroups.cra.EmailMembersType getP_members() {
        return p_members;
    }


    /**
     * Sets the p_members value for this RemoveEgroupEmailMembersRequest.
     * 
     * @param p_members
     */
    public void setP_members(ch.cern.dod.ws.egroups.cra.EmailMembersType p_members) {
        this.p_members = p_members;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RemoveEgroupEmailMembersRequest)) return false;
        RemoveEgroupEmailMembersRequest other = (RemoveEgroupEmailMembersRequest) obj;
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
            ((this.p_egroupName==null && other.getP_egroupName()==null) || 
             (this.p_egroupName!=null &&
              this.p_egroupName.equals(other.getP_egroupName()))) &&
            ((this.p_members==null && other.getP_members()==null) || 
             (this.p_members!=null &&
              this.p_members.equals(other.getP_members())));
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
        if (getP_egroupName() != null) {
            _hashCode += getP_egroupName().hashCode();
        }
        if (getP_members() != null) {
            _hashCode += getP_members().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RemoveEgroupEmailMembersRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupEmailMembersRequest"));
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
        elemField.setFieldName("p_egroupName");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "p_egroupName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("p_members");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "p_members"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EmailMembersType"));
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
