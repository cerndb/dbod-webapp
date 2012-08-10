/**
 * CraEgroupsWebService_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.egroups;

public interface CraEgroupsWebService_PortType extends java.rmi.Remote {

    /**
     * Change External Email Address
     */
    public ch.cern.dod.ws.egroups.ChangeExternalEmailAddressResponse changeExternalEmailAddress(ch.cern.dod.ws.egroups.ChangeExternalEmailAddressRequest parameters) throws java.rmi.RemoteException;

    /**
     * Find Egroup By ID
     */
    public ch.cern.dod.ws.egroups.FindEgroupByIdResponse findEgroupById(ch.cern.dod.ws.egroups.FindEgroupByIdRequest parameters) throws java.rmi.RemoteException;

    /**
     * Find Egroup By Name
     */
    public ch.cern.dod.ws.egroups.FindEgroupByNameResponse findEgroupByName(ch.cern.dod.ws.egroups.FindEgroupByNameRequest parameters) throws java.rmi.RemoteException;

    /**
     * Create or reload an e-group
     */
    public ch.cern.dod.ws.egroups.SynchronizeEgroupResponse synchronizeEgroup(ch.cern.dod.ws.egroups.SynchronizeEgroupRequest parameters) throws java.rmi.RemoteException;

    /**
     * Delete an e-group
     */
    public ch.cern.dod.ws.egroups.DeleteEgroupResponse deleteEgroup(ch.cern.dod.ws.egroups.DeleteEgroupRequest parameters) throws java.rmi.RemoteException;

    /**
     * Add members to an e-group
     */
    public ch.cern.dod.ws.egroups.AddEgroupMembersResponse addEgroupMembers(ch.cern.dod.ws.egroups.AddEgroupMembersRequest parameters) throws java.rmi.RemoteException;

    /**
     * Add email members to an e-group
     */
    public ch.cern.dod.ws.egroups.AddEgroupEmailMembersResponse addEgroupEmailMembers(ch.cern.dod.ws.egroups.AddEgroupEmailMembersRequest parameters) throws java.rmi.RemoteException;

    /**
     * Remove members from an e-group
     */
    public ch.cern.dod.ws.egroups.RemoveEgroupMembersResponse removeEgroupMembers(ch.cern.dod.ws.egroups.RemoveEgroupMembersRequest parameters) throws java.rmi.RemoteException;

    /**
     * Add email members from an e-group
     */
    public ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersResponse removeEgroupEmailMembers(ch.cern.dod.ws.egroups.RemoveEgroupEmailMembersRequest parameters) throws java.rmi.RemoteException;

    /**
     * Get all egroups an user own or manage
     */
    public ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageResponse getEgroupsUserOwnOrManage(ch.cern.dod.ws.egroups.GetEgroupsUserOwnOrManageRequest parameters) throws java.rmi.RemoteException;
}
