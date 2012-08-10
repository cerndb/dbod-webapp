/**
 * MemberTypeCode.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups.cra;

public class MemberTypeCode implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected MemberTypeCode(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _External = "External";
    public static final java.lang.String _Person = "Person";
    public static final java.lang.String _ServiceProvider = "ServiceProvider";
    public static final java.lang.String _StaticEgroup = "StaticEgroup";
    public static final java.lang.String _DynamicEgroup = "DynamicEgroup";
    public static final java.lang.String _Account = "Account";
    public static final MemberTypeCode External = new MemberTypeCode(_External);
    public static final MemberTypeCode Person = new MemberTypeCode(_Person);
    public static final MemberTypeCode ServiceProvider = new MemberTypeCode(_ServiceProvider);
    public static final MemberTypeCode StaticEgroup = new MemberTypeCode(_StaticEgroup);
    public static final MemberTypeCode DynamicEgroup = new MemberTypeCode(_DynamicEgroup);
    public static final MemberTypeCode Account = new MemberTypeCode(_Account);
    public java.lang.String getValue() { return _value_;}
    public static MemberTypeCode fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        MemberTypeCode enumeration = (MemberTypeCode)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static MemberTypeCode fromString(java.lang.String value)
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
        new org.apache.axis.description.TypeDesc(MemberTypeCode.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "MemberTypeCode"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
