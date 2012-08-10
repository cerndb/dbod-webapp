/**
 * EgroupType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups.cra;

public class EgroupType  implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String[] aliases;

    private long ID;

    private ch.cern.dod.ws.egroups.cra.EgroupTypeCode type;

    private ch.cern.dod.ws.egroups.cra.StatusCode status;

    private ch.cern.dod.ws.egroups.cra.BlockingReasonCode blockingReason;

    private ch.cern.dod.ws.egroups.cra.UsageCode usage;

    private java.lang.String topic;

    private java.lang.String description;

    private java.lang.String comments;

    private java.util.Date expiryDate;

    private ch.cern.dod.ws.egroups.cra.UserType owner;

    private ch.cern.dod.ws.egroups.cra.AdministratorType administrator;

    private ch.cern.dod.ws.egroups.cra.PrivacyType privacy;

    private ch.cern.dod.ws.egroups.cra.SelfsubscriptionType selfsubscription;

    private ch.cern.dod.ws.egroups.cra.SelfsubscriptionEgroupType[] selfsubscriptionEgroups;

    private ch.cern.dod.ws.egroups.cra.MemberType[] members;

    private ch.cern.dod.ws.egroups.cra.EmailMemberType[] emailMembers;

    public EgroupType() {
    }

    public EgroupType(
           java.lang.String name,
           java.lang.String[] aliases,
           long ID,
           ch.cern.dod.ws.egroups.cra.EgroupTypeCode type,
           ch.cern.dod.ws.egroups.cra.StatusCode status,
           ch.cern.dod.ws.egroups.cra.BlockingReasonCode blockingReason,
           ch.cern.dod.ws.egroups.cra.UsageCode usage,
           java.lang.String topic,
           java.lang.String description,
           java.lang.String comments,
           java.util.Date expiryDate,
           ch.cern.dod.ws.egroups.cra.UserType owner,
           ch.cern.dod.ws.egroups.cra.AdministratorType administrator,
           ch.cern.dod.ws.egroups.cra.PrivacyType privacy,
           ch.cern.dod.ws.egroups.cra.SelfsubscriptionType selfsubscription,
           ch.cern.dod.ws.egroups.cra.SelfsubscriptionEgroupType[] selfsubscriptionEgroups,
           ch.cern.dod.ws.egroups.cra.MemberType[] members,
           ch.cern.dod.ws.egroups.cra.EmailMemberType[] emailMembers) {
           this.name = name;
           this.aliases = aliases;
           this.ID = ID;
           this.type = type;
           this.status = status;
           this.blockingReason = blockingReason;
           this.usage = usage;
           this.topic = topic;
           this.description = description;
           this.comments = comments;
           this.expiryDate = expiryDate;
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
    public ch.cern.dod.ws.egroups.cra.EgroupTypeCode getType() {
        return type;
    }


    /**
     * Sets the type value for this EgroupType.
     * 
     * @param type
     */
    public void setType(ch.cern.dod.ws.egroups.cra.EgroupTypeCode type) {
        this.type = type;
    }


    /**
     * Gets the status value for this EgroupType.
     * 
     * @return status
     */
    public ch.cern.dod.ws.egroups.cra.StatusCode getStatus() {
        return status;
    }


    /**
     * Sets the status value for this EgroupType.
     * 
     * @param status
     */
    public void setStatus(ch.cern.dod.ws.egroups.cra.StatusCode status) {
        this.status = status;
    }


    /**
     * Gets the blockingReason value for this EgroupType.
     * 
     * @return blockingReason
     */
    public ch.cern.dod.ws.egroups.cra.BlockingReasonCode getBlockingReason() {
        return blockingReason;
    }


    /**
     * Sets the blockingReason value for this EgroupType.
     * 
     * @param blockingReason
     */
    public void setBlockingReason(ch.cern.dod.ws.egroups.cra.BlockingReasonCode blockingReason) {
        this.blockingReason = blockingReason;
    }


    /**
     * Gets the usage value for this EgroupType.
     * 
     * @return usage
     */
    public ch.cern.dod.ws.egroups.cra.UsageCode getUsage() {
        return usage;
    }


    /**
     * Sets the usage value for this EgroupType.
     * 
     * @param usage
     */
    public void setUsage(ch.cern.dod.ws.egroups.cra.UsageCode usage) {
        this.usage = usage;
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
     * Gets the expiryDate value for this EgroupType.
     * 
     * @return expiryDate
     */
    public java.util.Date getExpiryDate() {
        return expiryDate;
    }


    /**
     * Sets the expiryDate value for this EgroupType.
     * 
     * @param expiryDate
     */
    public void setExpiryDate(java.util.Date expiryDate) {
        this.expiryDate = expiryDate;
    }


    /**
     * Gets the owner value for this EgroupType.
     * 
     * @return owner
     */
    public ch.cern.dod.ws.egroups.cra.UserType getOwner() {
        return owner;
    }


    /**
     * Sets the owner value for this EgroupType.
     * 
     * @param owner
     */
    public void setOwner(ch.cern.dod.ws.egroups.cra.UserType owner) {
        this.owner = owner;
    }


    /**
     * Gets the administrator value for this EgroupType.
     * 
     * @return administrator
     */
    public ch.cern.dod.ws.egroups.cra.AdministratorType getAdministrator() {
        return administrator;
    }


    /**
     * Sets the administrator value for this EgroupType.
     * 
     * @param administrator
     */
    public void setAdministrator(ch.cern.dod.ws.egroups.cra.AdministratorType administrator) {
        this.administrator = administrator;
    }


    /**
     * Gets the privacy value for this EgroupType.
     * 
     * @return privacy
     */
    public ch.cern.dod.ws.egroups.cra.PrivacyType getPrivacy() {
        return privacy;
    }


    /**
     * Sets the privacy value for this EgroupType.
     * 
     * @param privacy
     */
    public void setPrivacy(ch.cern.dod.ws.egroups.cra.PrivacyType privacy) {
        this.privacy = privacy;
    }


    /**
     * Gets the selfsubscription value for this EgroupType.
     * 
     * @return selfsubscription
     */
    public ch.cern.dod.ws.egroups.cra.SelfsubscriptionType getSelfsubscription() {
        return selfsubscription;
    }


    /**
     * Sets the selfsubscription value for this EgroupType.
     * 
     * @param selfsubscription
     */
    public void setSelfsubscription(ch.cern.dod.ws.egroups.cra.SelfsubscriptionType selfsubscription) {
        this.selfsubscription = selfsubscription;
    }


    /**
     * Gets the selfsubscriptionEgroups value for this EgroupType.
     * 
     * @return selfsubscriptionEgroups
     */
    public ch.cern.dod.ws.egroups.cra.SelfsubscriptionEgroupType[] getSelfsubscriptionEgroups() {
        return selfsubscriptionEgroups;
    }


    /**
     * Sets the selfsubscriptionEgroups value for this EgroupType.
     * 
     * @param selfsubscriptionEgroups
     */
    public void setSelfsubscriptionEgroups(ch.cern.dod.ws.egroups.cra.SelfsubscriptionEgroupType[] selfsubscriptionEgroups) {
        this.selfsubscriptionEgroups = selfsubscriptionEgroups;
    }


    /**
     * Gets the members value for this EgroupType.
     * 
     * @return members
     */
    public ch.cern.dod.ws.egroups.cra.MemberType[] getMembers() {
        return members;
    }


    /**
     * Sets the members value for this EgroupType.
     * 
     * @param members
     */
    public void setMembers(ch.cern.dod.ws.egroups.cra.MemberType[] members) {
        this.members = members;
    }


    /**
     * Gets the emailMembers value for this EgroupType.
     * 
     * @return emailMembers
     */
    public ch.cern.dod.ws.egroups.cra.EmailMemberType[] getEmailMembers() {
        return emailMembers;
    }


    /**
     * Sets the emailMembers value for this EgroupType.
     * 
     * @param emailMembers
     */
    public void setEmailMembers(ch.cern.dod.ws.egroups.cra.EmailMemberType[] emailMembers) {
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
            ((this.blockingReason==null && other.getBlockingReason()==null) || 
             (this.blockingReason!=null &&
              this.blockingReason.equals(other.getBlockingReason()))) &&
            ((this.usage==null && other.getUsage()==null) || 
             (this.usage!=null &&
              this.usage.equals(other.getUsage()))) &&
            ((this.topic==null && other.getTopic()==null) || 
             (this.topic!=null &&
              this.topic.equals(other.getTopic()))) &&
            ((this.description==null && other.getDescription()==null) || 
             (this.description!=null &&
              this.description.equals(other.getDescription()))) &&
            ((this.comments==null && other.getComments()==null) || 
             (this.comments!=null &&
              this.comments.equals(other.getComments()))) &&
            ((this.expiryDate==null && other.getExpiryDate()==null) || 
             (this.expiryDate!=null &&
              this.expiryDate.equals(other.getExpiryDate()))) &&
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
        if (getBlockingReason() != null) {
            _hashCode += getBlockingReason().hashCode();
        }
        if (getUsage() != null) {
            _hashCode += getUsage().hashCode();
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
        if (getExpiryDate() != null) {
            _hashCode += getExpiryDate().hashCode();
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
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EgroupType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("aliases");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Aliases"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Alias"));
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
        elemField.setFieldName("status");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Status"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "StatusCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("blockingReason");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "BlockingReason"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "BlockingReasonCode"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("usage");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Usage"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "UsageCode"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("topic");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Topic"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("description");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Description"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("comments");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Comments"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("expiryDate");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "ExpiryDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "date"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("owner");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Owner"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "UserType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("administrator");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Administrator"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "AdministratorType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("privacy");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Privacy"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "PrivacyType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("selfsubscription");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Selfsubscription"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "SelfsubscriptionType"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("selfsubscriptionEgroups");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "SelfsubscriptionEgroups"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "SelfsubscriptionEgroupType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Egroup"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("members");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Members"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "MemberType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Member"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("emailMembers");
        elemField.setXmlName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EmailMembers"));
        elemField.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EmailMemberType"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "Member"));
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
