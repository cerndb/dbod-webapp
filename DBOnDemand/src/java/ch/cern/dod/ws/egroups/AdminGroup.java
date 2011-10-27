/**
 * AdminGroup.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class AdminGroup  implements java.io.Serializable {
    private java.lang.String ID;

    private long GID;

    private ch.cern.dod.ws.egroups.AdminGroupProject project;

    public AdminGroup() {
    }

    public AdminGroup(
           java.lang.String ID,
           long GID,
           ch.cern.dod.ws.egroups.AdminGroupProject project) {
           this.ID = ID;
           this.GID = GID;
           this.project = project;
    }


    /**
     * Gets the ID value for this AdminGroup.
     * 
     * @return ID
     */
    public java.lang.String getID() {
        return ID;
    }


    /**
     * Sets the ID value for this AdminGroup.
     * 
     * @param ID
     */
    public void setID(java.lang.String ID) {
        this.ID = ID;
    }


    /**
     * Gets the GID value for this AdminGroup.
     * 
     * @return GID
     */
    public long getGID() {
        return GID;
    }


    /**
     * Sets the GID value for this AdminGroup.
     * 
     * @param GID
     */
    public void setGID(long GID) {
        this.GID = GID;
    }


    /**
     * Gets the project value for this AdminGroup.
     * 
     * @return project
     */
    public ch.cern.dod.ws.egroups.AdminGroupProject getProject() {
        return project;
    }


    /**
     * Sets the project value for this AdminGroup.
     * 
     * @param project
     */
    public void setProject(ch.cern.dod.ws.egroups.AdminGroupProject project) {
        this.project = project;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AdminGroup)) return false;
        AdminGroup other = (AdminGroup) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.ID==null && other.getID()==null) || 
             (this.ID!=null &&
              this.ID.equals(other.getID()))) &&
            this.GID == other.getGID() &&
            ((this.project==null && other.getProject()==null) || 
             (this.project!=null &&
              this.project.equals(other.getProject())));
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
        if (getID() != null) {
            _hashCode += getID().hashCode();
        }
        _hashCode += new Long(getGID()).hashCode();
        if (getProject() != null) {
            _hashCode += getProject().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AdminGroup.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", ">AdminGroup"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("GID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "GID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("project");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Project"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", ">>AdminGroup>Project"));
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
