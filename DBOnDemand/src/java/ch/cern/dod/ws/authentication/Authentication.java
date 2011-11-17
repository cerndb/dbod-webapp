/**
 * Authentication.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.authentication;

public interface Authentication extends javax.xml.rpc.Service {

/**
 * <h2>CERN Lightweight Registration - Authentication WebService</h2>Detailed
 * documentation can be found in this document: <a href="http://cern.ch/CERNAccount/Help/?kbid=011020"
 * target=_blank>http://cern.ch/CERNAccount/Help/?kbid=011020</a><br>&nbsp;<br>&nbsp;<br>
 */
    public java.lang.String getAuthenticationSoap12Address();

    public ch.cern.dod.ws.authentication.AuthenticationSoap getAuthenticationSoap12() throws javax.xml.rpc.ServiceException;

    public ch.cern.dod.ws.authentication.AuthenticationSoap getAuthenticationSoap12(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
    public java.lang.String getAuthenticationSoapAddress();

    public ch.cern.dod.ws.authentication.AuthenticationSoap getAuthenticationSoap() throws javax.xml.rpc.ServiceException;

    public ch.cern.dod.ws.authentication.AuthenticationSoap getAuthenticationSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
