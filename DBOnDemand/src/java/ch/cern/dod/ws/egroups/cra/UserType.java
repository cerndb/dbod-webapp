/**
 * UserType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups.cra;

public class UserType  implements java.io.Serializable {
    private long CCID;

    private ch.cern.dod.ws.egroups.cra.UserTypeCode type;

    private java.lang.String name;

    private java.lang.Boolean computingRulesSigned;

    private java.lang.String pem;

    private ch.cern.dod.ws.egroups.cra.GemType primaryGem;

    private java.lang.String cernUnit;

    private java.lang.String cernDepartment;

    private java.lang.String cernGroup;

    private java.lang.String telephone1;

    private java.lang.String fax;

    private java.lang.String building;

    private java.lang.String floor;

    private java.lang.String room;

    private java.lang.String mailbox;

    public UserType() {
    }

    public UserType(
           long CCID,
           ch.cern.dod.ws.egroups.cra.UserTypeCode type,
           java.lang.String name,
           java.lang.Boolean computingRulesSigned,
           java.lang.String pem,
           ch.cern.dod.ws.egroups.cra.GemType primaryGem,
           java.lang.String cernUnit,
           java.lang.String cernDepartment,
           java.lang.String cernGroup,
           java.lang.String telephone1,
           java.lang.String fax,
           java.lang.String building,
           java.lang.String floor,
           java.lang.String room,
           java.lang.String mailbox) {
           this.CCID = CCID;
           this.type = type;
           this.name = name;
           this.computingRulesSigned = computingRulesSigned;
           this.pem = pem;
           this.primaryGem = primaryGem;
           this.cernUnit = cernUnit;
           this.cernDepartment = cernDepartment;
           this.cernGroup = cernGroup;
           this.telephone1 = telephone1;
           this.fax = fax;
           this.building = building;
           this.floor = floor;
           this.room = room;
           this.mailbox = mailbox;
    }


    /**
     * Gets the CCID value for this UserType.
     * 
     * @return CCID
     */
    public long getCCID() {
        return CCID;
    }


    /**
     * Sets the CCID value for this UserType.
     * 
     * @param CCID
     */
    public void setCCID(long CCID) {
        this.CCID = CCID;
    }


    /**
     * Gets the type value for this UserType.
     * 
     * @return type
     */
    public ch.cern.dod.ws.egroups.cra.UserTypeCode getType() {
        return type;
    }


    /**
     * Sets the type value for this UserType.
     * 
     * @param type
     */
    public void setType(ch.cern.dod.ws.egroups.cra.UserTypeCode type) {
        this.type = type;
    }


    /**
     * Gets the name value for this UserType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this UserType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the computingRulesSigned value for this UserType.
     * 
     * @return computingRulesSigned
     */
    public java.lang.Boolean getComputingRulesSigned() {
        return computingRulesSigned;
    }


    /**
     * Sets the computingRulesSigned value for this UserType.
     * 
     * @param computingRulesSigned
     */
    public void setComputingRulesSigned(java.lang.Boolean computingRulesSigned) {
        this.computingRulesSigned = computingRulesSigned;
    }


    /**
     * Gets the pem value for this UserType.
     * 
     * @return pem
     */
    public java.lang.String getPem() {
        return pem;
    }


    /**
     * Sets the pem value for this UserType.
     * 
     * @param pem
     */
    public void setPem(java.lang.String pem) {
        this.pem = pem;
    }


    /**
     * Gets the primaryGem value for this UserType.
     * 
     * @return primaryGem
     */
    public ch.cern.dod.ws.egroups.cra.GemType getPrimaryGem() {
        return primaryGem;
    }


    /**
     * Sets the primaryGem value for this UserType.
     * 
     * @param primaryGem
     */
    public void setPrimaryGem(ch.cern.dod.ws.egroups.cra.GemType primaryGem) {
        this.primaryGem = primaryGem;
    }


    /**
     * Gets the cernUnit value for this UserType.
     * 
     * @return cernUnit
     */
    public java.lang.String getCernUnit() {
        return cernUnit;
    }


    /**
     * Sets the cernUnit value for this UserType.
     * 
     * @param cernUnit
     */
    public void setCernUnit(java.lang.String cernUnit) {
        this.cernUnit = cernUnit;
    }


    /**
     * Gets the cernDepartment value for this UserType.
     * 
     * @return cernDepartment
     */
    public java.lang.String getCernDepartment() {
        return cernDepartment;
    }


    /**
     * Sets the cernDepartment value for this UserType.
     * 
     * @param cernDepartment
     */
    public void setCernDepartment(java.lang.String cernDepartment) {
        this.cernDepartment = cernDepartment;
    }


    /**
     * Gets the cernGroup value for this UserType.
     * 
     * @return cernGroup
     */
    public java.lang.String getCernGroup() {
        return cernGroup;
    }


    /**
     * Sets the cernGroup value for this UserType.
     * 
     * @param cernGroup
     */
    public void setCernGroup(java.lang.String cernGroup) {
        this.cernGroup = cernGroup;
    }


    /**
     * Gets the telephone1 value for this UserType.
     * 
     * @return telephone1
     */
    public java.lang.String getTelephone1() {
        return telephone1;
    }


    /**
     * Sets the telephone1 value for this UserType.
     * 
     * @param telephone1
     */
    public void setTelephone1(java.lang.String telephone1) {
        this.telephone1 = telephone1;
    }


    /**
     * Gets the fax value for this UserType.
     * 
     * @return fax
     */
    public java.lang.String getFax() {
        return fax;
    }


    /**
     * Sets the fax value for this UserType.
     * 
     * @param fax
     */
    public void setFax(java.lang.String fax) {
        this.fax = fax;
    }


    /**
     * Gets the building value for this UserType.
     * 
     * @return building
     */
    public java.lang.String getBuilding() {
        return building;
    }


    /**
     * Sets the building value for this UserType.
     * 
     * @param building
     */
    public void setBuilding(java.lang.String building) {
        this.building = building;
    }


    /**
     * Gets the floor value for this UserType.
     * 
     * @return floor
     */
    public java.lang.String getFloor() {
        return floor;
    }


    /**
     * Sets the floor value for this UserType.
     * 
     * @param floor
     */
    public void setFloor(java.lang.String floor) {
        this.floor = floor;
    }


    /**
     * Gets the room value for this UserType.
     * 
     * @return room
     */
    public java.lang.String getRoom() {
        return room;
    }


    /**
     * Sets the room value for this UserType.
     * 
     * @param room
     */
    public void setRoom(java.lang.String room) {
        this.room = room;
    }


    /**
     * Gets the mailbox value for this UserType.
     * 
     * @return mailbox
     */
    public java.lang.String getMailbox() {
        return mailbox;
    }


    /**
     * Sets the mailbox value for this UserType.
     * 
     * @param mailbox
     */
    public void setMailbox(java.lang.String mailbox) {
        this.mailbox = mailbox;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof UserType)) return false;
        UserType other = (UserType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.CCID == other.getCCID() &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.computingRulesSigned==null && other.getComputingRulesSigned()==null) || 
             (this.computingRulesSigned!=null &&
              this.computingRulesSigned.equals(other.getComputingRulesSigned()))) &&
            ((this.pem==null && other.getPem()==null) || 
             (this.pem!=null &&
              this.pem.equals(other.getPem()))) &&
            ((this.primaryGem==null && other.getPrimaryGem()==null) || 
             (this.primaryGem!=null &&
              this.primaryGem.equals(other.getPrimaryGem()))) &&
            ((this.cernUnit==null && other.getCernUnit()==null) || 
             (this.cernUnit!=null &&
              this.cernUnit.equals(other.getCernUnit()))) &&
            ((this.cernDepartment==null && other.getCernDepartment()==null) || 
             (this.cernDepartment!=null &&
              this.cernDepartment.equals(other.getCernDepartment()))) &&
            ((this.cernGroup==null && other.getCernGroup()==null) || 
             (this.cernGroup!=null &&
              this.cernGroup.equals(other.getCernGroup()))) &&
            ((this.telephone1==null && other.getTelephone1()==null) || 
             (this.telephone1!=null &&
              this.telephone1.equals(other.getTelephone1()))) &&
            ((this.fax==null && other.getFax()==null) || 
             (this.fax!=null &&
              this.fax.equals(other.getFax()))) &&
            ((this.building==null && other.getBuilding()==null) || 
             (this.building!=null &&
              this.building.equals(other.getBuilding()))) &&
            ((this.floor==null && other.getFloor()==null) || 
             (this.floor!=null &&
              this.floor.equals(other.getFloor()))) &&
            ((this.room==null && other.getRoom()==null) || 
             (this.room!=null &&
              this.room.equals(other.getRoom()))) &&
            ((this.mailbox==null && other.getMailbox()==null) || 
             (this.mailbox!=null &&
              this.mailbox.equals(other.getMailbox())));
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
        _hashCode += new Long(getCCID()).hashCode();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getComputingRulesSigned() != null) {
            _hashCode += getComputingRulesSigned().hashCode();
        }
        if (getPem() != null) {
            _hashCode += getPem().hashCode();
        }
        if (getPrimaryGem() != null) {
            _hashCode += getPrimaryGem().hashCode();
        }
        if (getCernUnit() != null) {
            _hashCode += getCernUnit().hashCode();
        }
        if (getCernDepartment() != null) {
            _hashCode += getCernDepartment().hashCode();
        }
        if (getCernGroup() != null) {
            _hashCode += getCernGroup().hashCode();
        }
        if (getTelephone1() != null) {
            _hashCode += getTelephone1().hashCode();
        }
        if (getFax() != null) {
            _hashCode += getFax().hashCode();
        }
        if (getBuilding() != null) {
            _hashCode += getBuilding().hashCode();
        }
        if (getFloor() != null) {
            _hashCode += getFloor().hashCode();
        }
        if (getRoom() != null) {
            _hashCode += getRoom().hashCode();
        }
        if (getMailbox() != null) {
            _hashCode += getMailbox().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(UserType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "UserType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("CCID");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "CCID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "UserTypeCode"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("computingRulesSigned");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "ComputingRulesSigned"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pem");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Pem"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("primaryGem");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "PrimaryGem"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "GemType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cernUnit");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "CernUnit"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cernDepartment");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "CernDepartment"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cernGroup");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "CernGroup"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telephone1");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Telephone1"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fax");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Fax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("building");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Building"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("floor");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Floor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("room");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Room"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mailbox");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Mailbox"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
