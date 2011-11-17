/**
 * AuthenticationSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ch.cern.dod.ws.authentication;

public interface AuthenticationSoap extends java.rmi.Remote {

    /**
     * Authenticates user from login and password. Login can be email
     * address or NICE login.
     */
    public ch.cern.dod.ws.authentication.UserInfo getUserInfo(java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException;

    /**
     * Authenticates user from login and password. Login can be email
     * address or NICE login. Group membership is verified at the same time,
     * multiple groups can be specified, separated with ','.
     */
    public ch.cern.dod.ws.authentication.UserInfo getUserInfoEx(java.lang.String userName, java.lang.String password, java.lang.String groupName) throws java.rmi.RemoteException;

    /**
     * DEPRECATED - Verify this CCID belongs to a Nice account. Returns
     * login or -1 if not found.
     */
    public java.lang.String CCIDisNice(int CCID) throws java.rmi.RemoteException;

    /**
     * Check if one user is member of specified NICE Group. UserName
     * is NICE Login or Email.
     */
    public boolean userIsMemberOfGroup(java.lang.String userName, java.lang.String groupName) throws java.rmi.RemoteException;

    /**
     * Check if one user is member of specified simba list. UserName
     * is NICE Login or Email. Listname can be 'listname' or 'listname@cern.ch'.
     */
    public boolean userIsMemberOfList(java.lang.String userName, java.lang.String listName) throws java.rmi.RemoteException;

    /**
     * Returns a string array containing Groups the specified User
     * is member of. UserName is NICE Login or Email. Listname can be 'listname'
     * or 'listname@cern.ch'.
     */
    public java.lang.String[] getGroupsForUser(java.lang.String userName) throws java.rmi.RemoteException;

    /**
     * Search for a group, based on given pattern. 3 characters minimum
     * are required. Search is done with: *pattern*.
     */
    public java.lang.String[] searchGroups(java.lang.String pattern) throws java.rmi.RemoteException;

    /**
     * Search users with given display name. Display name is firstname
     * + lastname, or email, and can contain *.
     */
    public ch.cern.dod.ws.authentication.UserInfo[] listUsers(java.lang.String displayName) throws java.rmi.RemoteException;

    /**
     * Get user information from login. Login can be email address
     * or NICE login.
     */
    public ch.cern.dod.ws.authentication.UserInfo getUserInfoFromLogin(java.lang.String userName) throws java.rmi.RemoteException;

    /**
     * TEST ONLY, DO NOT USE. Returns a string array containing Groups
     * and nested Groups the specified User is member of. This includes mailing
     * lists. UserName is NICE Login or Email. Listname can be 'listname'
     * or 'listname@cern.ch'.
     */
    public java.lang.String[] getRecursiveGroupsForUser(java.lang.String userName) throws java.rmi.RemoteException;

    /**
     * Returns the user UPN or external UPN from the given Email address.
     */
    public java.lang.String getUPNFromEmail(java.lang.String email) throws java.rmi.RemoteException;

    /**
     * Returns the members list of the provided mailinglist (use mailinglist's
     * email address). Returned members list also contains members of nested
     * lists. Result is an email address array.
     */
    public java.lang.String[] getListMembers(java.lang.String listName) throws java.rmi.RemoteException;

    /**
     * Returns the members list of the provided mailinglist (use mailinglist's
     * email address). Returned members list also contains members of nested
     * lists. Result is a login array, and contains only CERN Accounts, NO
     * Externals!!!.
     */
    public java.lang.String[] getListMembersLogins_CERNAccountOnly(java.lang.String listName) throws java.rmi.RemoteException;
}
