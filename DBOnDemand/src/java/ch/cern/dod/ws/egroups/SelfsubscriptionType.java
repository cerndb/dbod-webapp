/**
 * SelfsubscriptionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class SelfsubscriptionType implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected SelfsubscriptionType(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Closed = "Closed";
    public static final java.lang.String _Open = "Open";
    public static final java.lang.String _Members = "Members";
    public static final java.lang.String _Users = "Users";
    public static final java.lang.String _OpenWithAdminApproval = "OpenWithAdminApproval";
    public static final java.lang.String _UsersWithAdminApproval = "UsersWithAdminApproval";
    public static final SelfsubscriptionType Closed = new SelfsubscriptionType(_Closed);
    public static final SelfsubscriptionType Open = new SelfsubscriptionType(_Open);
    public static final SelfsubscriptionType Members = new SelfsubscriptionType(_Members);
    public static final SelfsubscriptionType Users = new SelfsubscriptionType(_Users);
    public static final SelfsubscriptionType OpenWithAdminApproval = new SelfsubscriptionType(_OpenWithAdminApproval);
    public static final SelfsubscriptionType UsersWithAdminApproval = new SelfsubscriptionType(_UsersWithAdminApproval);
    public java.lang.String getValue() { return _value_;}
    public static SelfsubscriptionType fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        SelfsubscriptionType enumeration = (SelfsubscriptionType)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static SelfsubscriptionType fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SelfsubscriptionType.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://cra.web.cern.ch/cra/xml", "SelfsubscriptionType"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
