/**
 * CraEgroupsWebService_Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public interface CraEgroupsWebService_Service extends javax.xml.rpc.Service {
    public java.lang.String getCraEgroupsWebServiceSoap12Address();

    public ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType getCraEgroupsWebServiceSoap12() throws javax.xml.rpc.ServiceException;

    public ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType getCraEgroupsWebServiceSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getCraEgroupsWebServiceAddress();

    public ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType getCraEgroupsWebService() throws javax.xml.rpc.ServiceException;

    public ch.cern.dod.ws.egroups.CraEgroupsWebService_PortType getCraEgroupsWebService(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
