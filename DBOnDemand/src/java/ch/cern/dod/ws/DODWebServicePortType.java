/**
 * DODWebServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws;

public interface DODWebServicePortType extends java.rmi.Remote {
    public java.lang.String getSnapshots(java.lang.String instance) throws java.rmi.RemoteException;
    public java.lang.String getSlowLogs(java.lang.String instance) throws java.rmi.RemoteException;
    public java.lang.String getFile(java.lang.String instance, java.lang.String filePath) throws java.rmi.RemoteException;
    public org.apache.axis.types.URI serveFile(java.lang.String instance, java.lang.String filePath) throws java.rmi.RemoteException;
    public java.lang.String getParam(java.lang.String instance, java.lang.String param) throws java.rmi.RemoteException;
}
