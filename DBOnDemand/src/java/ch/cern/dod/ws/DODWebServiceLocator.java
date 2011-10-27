/**
 * DODWebServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws;

public class DODWebServiceLocator extends org.apache.axis.client.Service implements ch.cern.dod.ws.DODWebService {

    public DODWebServiceLocator() {
    }


    public DODWebServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public DODWebServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for DODWebServicePort
    private java.lang.String DODWebServicePort_address = "https://syscontrol.cern.ch/dod/webservices/dod.cgi";

    public java.lang.String getDODWebServicePortAddress() {
        return DODWebServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String DODWebServicePortWSDDServiceName = "DODWebServicePort";

    public java.lang.String getDODWebServicePortWSDDServiceName() {
        return DODWebServicePortWSDDServiceName;
    }

    public void setDODWebServicePortWSDDServiceName(java.lang.String name) {
        DODWebServicePortWSDDServiceName = name;
    }

    public ch.cern.dod.ws.DODWebServicePortType getDODWebServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(DODWebServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getDODWebServicePort(endpoint);
    }

    public ch.cern.dod.ws.DODWebServicePortType getDODWebServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ch.cern.dod.ws.DODWebServiceSoapBindingStub _stub = new ch.cern.dod.ws.DODWebServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getDODWebServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setDODWebServicePortEndpointAddress(java.lang.String address) {
        DODWebServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ch.cern.dod.ws.DODWebServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                ch.cern.dod.ws.DODWebServiceSoapBindingStub _stub = new ch.cern.dod.ws.DODWebServiceSoapBindingStub(new java.net.URL(DODWebServicePort_address), this);
                _stub.setPortName(getDODWebServicePortWSDDServiceName());
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
        if ("DODWebServicePort".equals(inputPortName)) {
            return getDODWebServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("https://syscontrol.cern.ch/Dod/", "DODWebService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("https://syscontrol.cern.ch/Dod/", "DODWebServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("DODWebServicePort".equals(portName)) {
            setDODWebServicePortEndpointAddress(address);
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
