/**
 * CraEgroupsWebServiceBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class CraEgroupsWebServiceBindingStub extends org.apache.axis.client.Stub implements ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[10];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("changeExternalEmailAddress");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "changeExternalEmailAddressRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">changeExternalEmailAddressRequest"), ch.cern.dod.ws.egroups.ChangeExternalEmailAddressRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">changeExternalEmailAddressResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.ChangeExternalEmailAddressResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "changeExternalEmailAddressResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findEgroupById");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "findEgroupByIdRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">findEgroupByIdRequest"), ch.cern.dod.ws.egroups.FindEgroupByIdRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">findEgroupByIdResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.FindEgroupByIdResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "findEgroupByIdResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("findEgroupByName");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "findEgroupByNameRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">findEgroupByNameRequest"), ch.cern.dod.ws.egroups.FindEgroupByNameRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">findEgroupByNameResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.FindEgroupByNameResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "findEgroupByNameResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("synchronizeEgroup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "synchronizeEgroupRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">synchronizeEgroupRequest"), ch.cern.dod.ws.egroups.SynchronizeEgroupRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">synchronizeEgroupResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.SynchronizeEgroupResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "synchronizeEgroupResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("deleteEgroup");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "deleteEgroupRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">deleteEgroupRequest"), ch.cern.dod.ws.egroups.DeleteEgroupRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">deleteEgroupResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.DeleteEgroupResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "deleteEgroupResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addEgroupMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "addEgroupMembersRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">addEgroupMembersRequest"), ch.cern.dod.ws.egroups.AddEgroupMembersRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">addEgroupMembersResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.AddEgroupMembersResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "addEgroupMembersResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("addEgroupEmailMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "addEgroupEmailMembersRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">addEgroupEmailMembersRequest"), ch.cern.dod.ws.egroups.AddEgroupEmailMembersRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">addEgroupEmailMembersResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.AddEgroupEmailMembersResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "addEgroupEmailMembersResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeEgroupMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "removeEgroupMembersRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupMembersRequest"), ch.cern.dod.ws.egroups.RemoveEgroupMembersRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupMembersResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.RemoveEgroupMembersResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "removeEgroupMembersResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("removeEgroupEmailMembers");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "removeEgroupEmailMembersRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupEmailMembersRequest"), ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupEmailMembersResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "removeEgroupEmailMembersResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getEgroupsUserOwnOrManage");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "getEgroupsUserOwnOrManageRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">getEgroupsUserOwnOrManageRequest"), ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">getEgroupsUserOwnOrManageResponse"));
        oper.setReturnClass(ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "getEgroupsUserOwnOrManageResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    public CraEgroupsWebServiceBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public CraEgroupsWebServiceBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public CraEgroupsWebServiceBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "AdministratorType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.AdministratorType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "AdministratorTypeCode");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.AdministratorTypeCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "AliasesType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.AliasesType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "BlockingReasonCode");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.BlockingReasonCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EgroupsType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.EgroupsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EgroupType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.EgroupType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EgroupTypeCode");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.EgroupTypeCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EmailMembersType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.EmailMembersType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "EmailMemberType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.EmailMemberType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "GemType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.GemType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "MembersType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.MembersType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "MemberType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.MemberType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "MemberTypeCode");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.MemberTypeCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "PrivacyType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.PrivacyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "SelfsubscriptionEgroupsType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.SelfsubscriptionEgroupsType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "SelfsubscriptionEgroupType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.SelfsubscriptionEgroupType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "SelfsubscriptionType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.SelfsubscriptionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "StatusCode");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.StatusCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "UsageCode");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.UsageCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "UserType");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.UserType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/cra/", "UserTypeCode");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.cra.UserTypeCode.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">addEgroupEmailMembersRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.AddEgroupEmailMembersRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">addEgroupEmailMembersResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.AddEgroupEmailMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">addEgroupMembersRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.AddEgroupMembersRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">addEgroupMembersResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.AddEgroupMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">changeExternalEmailAddressRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.ChangeExternalEmailAddressRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">changeExternalEmailAddressResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.ChangeExternalEmailAddressResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">deleteEgroupRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.DeleteEgroupRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">deleteEgroupResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.DeleteEgroupResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">findEgroupByIdRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.FindEgroupByIdRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">findEgroupByIdResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.FindEgroupByIdResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">findEgroupByNameRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.FindEgroupByNameRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">findEgroupByNameResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.FindEgroupByNameResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">getEgroupsUserOwnOrManageRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">getEgroupsUserOwnOrManageResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupEmailMembersRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupEmailMembersResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupMembersRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.RemoveEgroupMembersRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">removeEgroupMembersResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.RemoveEgroupMembersResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">synchronizeEgroupRequest");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.SynchronizeEgroupRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", ">synchronizeEgroupResponse");
            cachedSerQNames.add(qName);
            cls = ch.cern.dod.ws.egroups.SynchronizeEgroupResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public ch.cern.dod.ws.egroups.ChangeExternalEmailAddressResponse changeExternalEmailAddress(ch.cern.dod.ws.egroups.ChangeExternalEmailAddressRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/changeExternalEmailAddress");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "changeExternalEmailAddress"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.ChangeExternalEmailAddressResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.ChangeExternalEmailAddressResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.ChangeExternalEmailAddressResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.FindEgroupByIdResponse findEgroupById(ch.cern.dod.ws.egroups.FindEgroupByIdRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/findEgroupById");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "findEgroupById"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.FindEgroupByIdResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.FindEgroupByIdResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.FindEgroupByIdResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.FindEgroupByNameResponse findEgroupByName(ch.cern.dod.ws.egroups.FindEgroupByNameRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/findEgroupByName");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "findEgroupByName"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.FindEgroupByNameResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.FindEgroupByNameResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.FindEgroupByNameResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.SynchronizeEgroupResponse synchronizeEgroup(ch.cern.dod.ws.egroups.SynchronizeEgroupRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/synchronizeEgroup");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "synchronizeEgroup"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.SynchronizeEgroupResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.SynchronizeEgroupResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.SynchronizeEgroupResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.DeleteEgroupResponse deleteEgroup(ch.cern.dod.ws.egroups.DeleteEgroupRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/deleteEgroup");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "deleteEgroup"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.DeleteEgroupResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.DeleteEgroupResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.DeleteEgroupResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.AddEgroupMembersResponse addEgroupMembers(ch.cern.dod.ws.egroups.AddEgroupMembersRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/addEgroupMembers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "addEgroupMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.AddEgroupMembersResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.AddEgroupMembersResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.AddEgroupMembersResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.AddEgroupEmailMembersResponse addEgroupEmailMembers(ch.cern.dod.ws.egroups.AddEgroupEmailMembersRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/addEgroupEmailMembers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "addEgroupEmailMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.AddEgroupEmailMembersResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.AddEgroupEmailMembersResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.AddEgroupEmailMembersResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.RemoveEgroupMembersResponse removeEgroupMembers(ch.cern.dod.ws.egroups.RemoveEgroupMembersRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/removeEgroupMembers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "removeEgroupMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.RemoveEgroupMembersResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.RemoveEgroupMembersResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.RemoveEgroupMembersResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersResponse removeEgroupEmailMembers(ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/removeEgroupEmailMembers");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "removeEgroupEmailMembers"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageResponse getEgroupsUserOwnOrManage(ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageRequest parameters) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService/getEgroupsUserOwnOrManage");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getEgroupsUserOwnOrManage"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parameters});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageResponse) org.apache.axis.utils.JavaUtils.convert(_resp, ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
