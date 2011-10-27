/**
 * EgroupType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class EgroupType  implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String[] aliases;

    private long ID;

    private ch.cern.dod.ws.egroups.EgroupTypeCode type;

    private ch.cern.dod.ws.egroups.StatusCode status;

    private java.lang.String topic;

    private java.lang.String description;

    private java.lang.String comments;

    private ch.cern.dod.ws.egroups.UserType owner;

    private ch.cern.dod.ws.egroups.AdministratorType administrator;

    private ch.cern.dod.ws.egroups.PrivacyType privacy;

    private ch.cern.dod.ws.egroups.SelfsubscriptionType selfsubscription;

    private ch.cern.dod.ws.egroups.SelfsubscriptionEgroupType[] selfsubscriptionEgroups;

    private ch.cern.dod.ws.egroups.MemberType[] members;

    private ch.cern.dod.ws.egroups.EmailMemberType[] emailMembers;

    public EgroupType() {
    }

    public EgroupType(
           java.lang.String name,
           java.lang.String[] aliases,
           long ID,
           ch.cern.dod.ws.egroups.EgroupTypeCode type,
           ch.cern.dod.ws.egroups.StatusCode status,
           java.lang.String topic,
           java.lang.String description,
           java.lang.String comments,
           ch.cern.dod.ws.egroups.UserType owner,
           ch.cern.dod.ws.egroups.AdministratorType administrator,
           ch.cern.dod.ws.egroups.PrivacyType privacy,
           ch.cern.dod.ws.egroups.SelfsubscriptionType selfsubscription,
           ch.cern.dod.ws.egroups.SelfsubscriptionEgroupType[] selfsubscriptionEgroups,
           ch.cern.dod.ws.egroups.MemberType[] members,
           ch.cern.dod.ws.egroups.EmailMemberType[] emailMembers) {
           this.name = name;
           this.aliases = aliases;
           this.ID = ID;
           this.type = type;
           this.status = status;
           this.topic = topic;
           this.description = description;
           this.comments = comments;
           this.owner = owner;
           this.administrator = administrator;
           this.privacy = privacy;
           this.selfsubscription = selfsubscription;
           this.selfsubscriptionEgroups = selfsubscriptionEgroups;
           this.members = members;
           this.emailMembers = emailMembers;
    }


    /**
     * Gets the name value for this EgroupType.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this EgroupType.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the aliases value for this EgroupType.
     * 
     * @return aliases
     */
    public java.lang.String[] getAliases() {
        return aliases;
    }


    /**
     * Sets the aliases value for this EgroupType.
     * 
     * @param aliases
     */
    public void setAliases(java.lang.String[] aliases) {
        this.aliases = aliases;
    }


    /**
     * Gets the ID value for this EgroupType.
     * 
     * @return ID
     */
    public long getID() {
        return ID;
    }


    /**
     * Sets the ID value for this EgroupType.
     * 
     * @param ID
     */
    public void setID(long ID) {
        this.ID = ID;
    }


    /**
     * Gets the type value for this EgroupType.
     * 
     * @return type
     */
    public ch.cern.dod.ws.egroups.EgroupTypeCode getType() {
        return type;
    }


    /**
     * Sets the type value for this EgroupType.
     * 
     * @param type
     */
    public void setType(ch.cern.dod.ws.egroups.EgroupTypeCode type) {
        this.type = type;
    }


    /**
     * Gets the status value for this EgroupType.
     * 
     * @return status
     */
    public ch.cern.dod.ws.egroups.StatusCode getStatus() {
        return status;
    }


    /**
     * Sets the status value for this EgroupType.
     * 
     * @param status
     */
    public void setStatus(ch.cern.dod.ws.egroups.StatusCode status) {
        this.status = status;
    }


    /**
     * Gets the topic value for this EgroupType.
     * 
     * @return topic
     */
    public java.lang.String getTopic() {
        return topic;
    }


    /**
     * Sets the topic value for this EgroupType.
     * 
     * @param topic
     */
    public void setTopic(java.lang.String topic) {
        this.topic = topic;
    }


    /**
     * Gets the description value for this EgroupType.
     * 
     * @return description
     */
    public java.lang.String getDescription() {
        return description;
    }


    /**
     * Sets the description value for this EgroupType.
     * 
     * @param description
     */
    public void setDescription(java.lang.String description) {
        this.description = description;
    }


    /**
     * Gets the comments value for this EgroupType.
     * 
     * @return comments
     */
    public java.lang.String getComments() {
        return comments;
    }


    /**
     * Sets the comments value for this EgroupType.
     * 
     * @param comments
     */
    public void setComments(java.lang.String comments) {
        this.comments = comments;
    }


    /**
     * Gets the owner value for this EgroupType.
     * 
     * @return owner
     */
    public ch.cern.dod.ws.egroups.UserType getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this EgroupType.
     * 
     * @param owner
     */
    public void setOwner(ch.cern.dod.ws.egroups.UserType owner) {
        this.owner = owner;
    }


    /**
     * Gets the administrator value for this EgroupType.
     * 
     * @return administrator
     */
    public ch.cern.dod.ws.egroups.AdministratorType getAdministrator() {
        return administrator;
    }


    /**
     * Sets the administrator value for this EgroupType.
     * 
     * @param administrator
     */
    public void setAdministrator(ch.cern.dod.ws.egroups.AdministratorType administrator) {
        this.administrator = administrator;
    }


    /**
     * Gets the privacy value for this EgroupType.
     * 
     * @return privacy
     */
    public ch.cern.dod.ws.egroups.PrivacyType getPrivacy() {
        return privacy;
    }


    /**
     * Sets the privacy value for this EgroupType.
     * 
     * @param privacy
     */
    public void setPrivacy(ch.cern.dod.ws.egroups.PrivacyType privacy) {
        this.privacy = privacy;
    }


    /**
     * Gets the selfsubscription value for this EgroupType.
     * 
     * @return selfsubscription
     */
    public ch.cern.dod.ws.egroups.SelfsubscriptionType getSelfsubscription() {
        return selfsubscription;
    }


    /**
     * Sets the selfsubscription value for this EgroupType.
     * 
     * @param selfsubscription
     */
    public void setSelfsubscription(ch.cern.dod.ws.egroups.SelfsubscriptionType selfsubscription) {
        this.selfsubscription = selfsubscription;
    }


    /**
     * Gets the selfsubscriptionEgroups value for this EgroupType.
     * 
     * @return selfsubscriptionEgroups
     */
    public ch.cern.dod.ws.egroups.SelfsubscriptionEgroupType[] getSelfsubscriptionEgroups() {
        return selfsubscriptionEgroups;
    }


    /**
     * Sets the selfsubscriptionEgroups value for this EgroupType.
     * 
     * @param selfsubscriptionEgroups
     */
    public void setSelfsubscriptionEgroups(ch.cern.dod.ws.egroups.SelfsubscriptionEgroupType[] selfsubscriptionEgroups) {
        this.selfsubscriptionEgroups = selfsubscriptionEgroups;
    }


    /**
     * Gets the members value for this EgroupType.
     * 
     * @return members
     */
    public ch.cern.dod.ws.egroups.MemberType[] getMembers() {
        return members;
    }


    /**
     * Sets the members value for this EgroupType.
     * 
     * @param members
     */
    public void setMembers(ch.cern.dod.ws.egroups.MemberType[] members) {
        this.members = members;
    }


    /**
     * Gets the emailMembers value for this EgroupType.
     * 
     * @return emailMembers
     */
    public ch.cern.dod.ws.egroups.EmailMemberType[] getEmailMembers() {
        return emailMembers;
    }


    /**
     * Sets the emailMembers value for this EgroupType.
     * 
     * @param emailMembers
     */
    public void setEmailMembers(ch.cern.dod.ws.egroups.EmailMemberType[] emailMembers) {
        this.emailMembers = emailMembers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof EgroupType)) return false;
        EgroupType other = (EgroupType) obj;
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
            ((this.aliases==null && other.getAliases()==null) || 
             (this.aliases!=null &&
              java.util.Arrays.equals(this.aliases, other.getAliases()))) &&
            this.ID == other.getID() &&
            ((this.type==null && other.getType()==null) || 
             (this.type!=null &&
              this.type.equals(other.getType()))) &&
            ((this.status==null && other.getStatus()==null) || 
             (this.status!=null &&
              this.status.equals(other.getStatus()))) &&
            ((this.topic==null && other.getTopic()==null) || 
             (this.topic!=null &&
              this.topic.equals(other.getTopic()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              this.comments.equals(other.getComments()))) &&
            ((this.owner==null && other.getOwner()==null) || 
             (this.owner!=null &&
              this.owner.equals(other.getOwner()))) &&
            ((this.administrator==null && other.getAdministrator()==null) || 
             (this.administrator!=null &&
              this.administrator.equals(other.getAdministrator()))) &&
            ((this.privacy==null && other.getPrivacy()==null) || 
             (this.privacy!=null &&
              this.privacy.equals(other.getPrivacy()))) &&
            ((this.selfsubscription==null && other.getSelfsubscription()==null) || 
             (this.selfsubscription!=null &&
              this.selfsubscription.equals(other.getSelfsubscription()))) &&
            ((this.selfsubscriptionEgroups==null && other.getSelfsubscriptionEgroups()==null) || 
             (this.selfsubscriptionEgroups!=null &&
              java.util.Arrays.equals(this.selfsubscriptionEgroups, other.getSelfsubscriptionEgroups()))) &&
            ((this.members==null && other.getMembers()==null) || 
             (this.members!=null &&
              java.util.Arrays.equals(this.members, other.getMembers()))) &&
            ((this.emailMembers==null && other.getEmailMembers()==null) || 
             (this.emailMembers!=null &&
              java.util.Arrays.equals(this.emailMembers, other.getEmailMembers())));
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
        if (getAliases() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAliases());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAliases(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += new Long(getID()).hashCode();
        if (getType() != null) {
            _hashCode += getType().hashCode();
        }
        if (getStatus() != null) {
            _hashCode += getStatus().hashCode();
        }
        if (getTopic() != null) {
            _hashCode += getTopic().hashCode();
        }
        if (getDescription() != null) {
            _hashCode += getDescription().hashCode();
        }
        if (getComments() != null) {
            _hashCode += getComments().hashCode();
        }
        if (getOwner() != null) {
            _hashCode += getOwner().hashCode();
        }
        if (getAdministrator() != null) {
            _hashCode += getAdministrator().hashCode();
        }
        if (getPrivacy() != null) {
            _hashCode += getPrivacy().hashCode();
        }
        if (getSelfsubscription() != null) {
            _hashCode += getSelfsubscription().hashCode();
        }
        if (getSelfsubscriptionEgroups() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getSelfsubscriptionEgroups());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getSelfsubscriptionEgroups(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getMembers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getMembers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getMembers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getEmailMembers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getEmailMembers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getEmailMembers(), i);
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
        new org.apache.axis.description.TypeDesc(EgroupType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "EgroupType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aliases");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Aliases"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Alias"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ID");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "ID"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "EgroupTypeCode"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "StatusCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("topic");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Topic"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comments");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Comments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "UserType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("administrator");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Administrator"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "AdministratorType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("privacy");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Privacy"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "PrivacyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("selfsubscription");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Selfsubscription"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "SelfsubscriptionType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("selfsubscriptionEgroups");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "SelfsubscriptionEgroups"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "SelfsubscriptionEgroupType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Egroup"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("members");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Members"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "MemberType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Member"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailMembers");
        elemField.setXmlName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "EmailMembers"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "EmailMemberType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "Member"));
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
