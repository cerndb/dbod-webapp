/**
 * CraEgroupsWebService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public class CraEgroupsWebService_ServiceLocator extends org.apache.axis.client.Service implements ch.cern.dod.ws.egroups.CraEgroupsWebService_Service {

    public CraEgroupsWebService_ServiceLocator() {
    }


    public CraEgroupsWebService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CraEgroupsWebService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CraEgroupsWebServiceSoap12
    private java.lang.String CraEgroupsWebServiceSoap12_address = "https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService";

    public java.lang.String getCraEgroupsWebServiceSoap12Address() {
        return CraEgroupsWebServiceSoap12_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CraEgroupsWebServiceSoap12WSDDServiceName = "CraEgroupsWebServiceSoap12";

    public java.lang.String getCraEgroupsWebServiceSoap12WSDDServiceName() {
        return CraEgroupsWebServiceSoap12WSDDServiceName;
    }

    public void setCraEgroupsWebServiceSoap12WSDDServiceName(java.lang.String name) {
        CraEgroupsWebServiceSoap12WSDDServiceName = name;
    }

    public ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType getCraEgroupsWebServiceSoap12() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CraEgroupsWebServiceSoap12_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCraEgroupsWebServiceSoap12(endpoint);
    }

    public ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType getCraEgroupsWebServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ch.cern.dod.ws.egroups.CraEgroupsWebServiceBindingSoap12Stub _stub = new ch.cern.dod.ws.egroups.CraEgroupsWebServiceBindingSoap12Stub(portAddress, this);
            _stub.setPortName(getCraEgroupsWebServiceSoap12WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCraEgroupsWebServiceSoap12EndpointAddress(java.lang.String address) {
        CraEgroupsWebServiceSoap12_address = address;
    }


    // Use to get a proxy class for CraEgroupsWebService
    private java.lang.String CraEgroupsWebService_address = "https://cra-ws.cern.ch/cra-ws/CraEgroupsWebService";

    public java.lang.String getCraEgroupsWebServiceAddress() {
        return CraEgroupsWebService_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CraEgroupsWebServiceWSDDServiceName = "CraEgroupsWebService";

    public java.lang.String getCraEgroupsWebServiceWSDDServiceName() {
        return CraEgroupsWebServiceWSDDServiceName;
    }

    public void setCraEgroupsWebServiceWSDDServiceName(java.lang.String name) {
        CraEgroupsWebServiceWSDDServiceName = name;
    }

    public ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType getCraEgroupsWebService() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CraEgroupsWebService_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCraEgroupsWebService(endpoint);
    }

    public ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType getCraEgroupsWebService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ch.cern.dod.ws.egroups.CraEgroupsWebServiceBindingStub _stub = new ch.cern.dod.ws.egroups.CraEgroupsWebServiceBindingStub(portAddress, this);
            _stub.setPortName(getCraEgroupsWebServiceWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCraEgroupsWebServiceEndpointAddress(java.lang.String address) {
        CraEgroupsWebService_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     * This service has multiple ports for a given interface;
     * the proxy implementation returned may be indeterminate.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                ch.cern.dod.ws.egroups.CraEgroupsWebServiceBindingSoap12Stub _stub = new ch.cern.dod.ws.egroups.CraEgroupsWebServiceBindingSoap12Stub(new java.net.URL(CraEgroupsWebServiceSoap12_address), this);
                _stub.setPortName(getCraEgroupsWebServiceSoap12WSDDServiceName());
                return _stub;
            }
            if (ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                ch.cern.dod.ws.egroups.CraEgroupsWebServiceBindingStub _stub = new ch.cern.dod.ws.egroups.CraEgroupsWebServiceBindingStub(new java.net.URL(CraEgroupsWebService_address), this);
                _stub.setPortName(getCraEgroupsWebServiceWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CraEgroupsWebServiceSoap12".equals(inputPortName)) {
            return getCraEgroupsWebServiceSoap12();
        }
        else if ("CraEgroupsWebService".equals(inputPortName)) {
            return getCraEgroupsWebService();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "CraEgroupsWebService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "CraEgroupsWebServiceSoap12"));
            ports.add(new javax.xml.namespace.QName("https://cra-ws.cern.ch/cra-ws/", "CraEgroupsWebService"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CraEgroupsWebServiceSoap12".equals(portName)) {
            setCraEgroupsWebServiceSoap12EndpointAddress(address);
        }
        else 
if ("CraEgroupsWebService".equals(portName)) {
            setCraEgroupsWebServiceEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
