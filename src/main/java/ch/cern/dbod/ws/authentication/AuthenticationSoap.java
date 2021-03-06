/*
 * Copyright (C) 2015, CERN
 * This software is distributed under the terms of the GNU General Public
 * Licence version 3 (GPL Version 3), copied verbatim in the file "LICENSE".
 * In applying this license, CERN does not waive the privileges and immunities
 * granted to it by virtue of its status as Intergovernmental Organization
 * or submit itself to any jurisdiction.
 */

package ch.cern.dbod.ws.authentication;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "AuthenticationSoap", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface AuthenticationSoap {


    /**
     * Authenticates user from login and password. Login can be email address or NICE login.
     * 
     * @param userName
     * @param password
     * @return
     *     returns ch.cern.dod.ws.authentication.UserInfo
     */
    @WebMethod(operationName = "GetUserInfo", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/GetUserInfo")
    @WebResult(name = "GetUserInfoResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "GetUserInfo", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetUserInfo")
    @ResponseWrapper(localName = "GetUserInfoResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetUserInfoResponse")
    public UserInfo getUserInfo(
        @WebParam(name = "UserName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String userName,
        @WebParam(name = "Password", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String password);

    /**
     * Authenticates user from login and password. Login can be email address or NICE login. Group membership is verified at the same time, multiple groups can be specified, separated with ','.
     * 
     * @param groupName
     * @param userName
     * @param password
     * @return
     *     returns ch.cern.dod.ws.authentication.UserInfo
     */
    @WebMethod(operationName = "GetUserInfoEx", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/GetUserInfoEx")
    @WebResult(name = "GetUserInfoExResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "GetUserInfoEx", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetUserInfoEx")
    @ResponseWrapper(localName = "GetUserInfoExResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetUserInfoExResponse")
    public UserInfo getUserInfoEx(
        @WebParam(name = "UserName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String userName,
        @WebParam(name = "Password", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String password,
        @WebParam(name = "GroupName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String groupName);

    /**
     * DEPRECATED - Verify this CCID belongs to a Nice account. Returns login or -1 if not found.
     * 
     * @param ccid
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "CCIDisNice", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/CCIDisNice")
    @WebResult(name = "CCIDisNiceResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "CCIDisNice", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.CCIDisNice")
    @ResponseWrapper(localName = "CCIDisNiceResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.CCIDisNiceResponse")
    public String cciDisNice(
        @WebParam(name = "CCID", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        int ccid);

    /**
     * Check if one user is member of specified NICE Group. UserName is NICE Login or Email.
     * 
     * @param groupName
     * @param userName
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "UserIsMemberOfGroup", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/UserIsMemberOfGroup")
    @WebResult(name = "UserIsMemberOfGroupResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "UserIsMemberOfGroup", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.UserIsMemberOfGroup")
    @ResponseWrapper(localName = "UserIsMemberOfGroupResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.UserIsMemberOfGroupResponse")
    public boolean userIsMemberOfGroup(
        @WebParam(name = "UserName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String userName,
        @WebParam(name = "GroupName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String groupName);

    /**
     * Check if one user is member of specified simba list. UserName is NICE Login or Email. Listname can be 'listname' or 'listname@cern.ch'.
     * 
     * @param listName
     * @param userName
     * @return
     *     returns boolean
     */
    @WebMethod(operationName = "UserIsMemberOfList", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/UserIsMemberOfList")
    @WebResult(name = "UserIsMemberOfListResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "UserIsMemberOfList", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.UserIsMemberOfList")
    @ResponseWrapper(localName = "UserIsMemberOfListResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.UserIsMemberOfListResponse")
    public boolean userIsMemberOfList(
        @WebParam(name = "UserName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String userName,
        @WebParam(name = "ListName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String listName);

    /**
     * Returns a string array containing Groups the specified User is member of. UserName is NICE Login or Email. Listname can be 'listname' or 'listname@cern.ch'.
     * 
     * @param userName
     * @return
     *     returns ch.cern.dod.ws.authentication.ArrayOfString
     */
    @WebMethod(operationName = "GetGroupsForUser", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/GetGroupsForUser")
    @WebResult(name = "GetGroupsForUserResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "GetGroupsForUser", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetGroupsForUser")
    @ResponseWrapper(localName = "GetGroupsForUserResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetGroupsForUserResponse")
    public ArrayOfString getGroupsForUser(
        @WebParam(name = "UserName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String userName);

    /**
     * Search for a group, based on given pattern. 3 characters minimum are required. Search is done with: *pattern*.
     * 
     * @param pattern
     * @return
     *     returns ch.cern.dod.ws.authentication.ArrayOfString
     */
    @WebMethod(operationName = "SearchGroups", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/SearchGroups")
    @WebResult(name = "SearchGroupsResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "SearchGroups", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.SearchGroups")
    @ResponseWrapper(localName = "SearchGroupsResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.SearchGroupsResponse")
    public ArrayOfString searchGroups(
        @WebParam(name = "pattern", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String pattern);

    /**
     * Search users with given display name. Display name is firstname + lastname, or email, and can contain *.
     * 
     * @param displayName
     * @return
     *     returns ch.cern.dod.ws.authentication.ArrayOfUserInfo
     */
    @WebMethod(operationName = "ListUsers", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/ListUsers")
    @WebResult(name = "ListUsersResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "ListUsers", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.ListUsers")
    @ResponseWrapper(localName = "ListUsersResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.ListUsersResponse")
    public ArrayOfUserInfo listUsers(
        @WebParam(name = "DisplayName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String displayName);

    /**
     * Get user information from login. Login can be email address or NICE login.
     * 
     * @param userName
     * @return
     *     returns ch.cern.dod.ws.authentication.UserInfo
     */
    @WebMethod(operationName = "GetUserInfoFromLogin", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/GetUserInfoFromLogin")
    @WebResult(name = "GetUserInfoFromLoginResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "GetUserInfoFromLogin", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetUserInfoFromLogin")
    @ResponseWrapper(localName = "GetUserInfoFromLoginResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetUserInfoFromLoginResponse")
    public UserInfo getUserInfoFromLogin(
        @WebParam(name = "UserName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String userName);

    /**
     * TEST ONLY, DO NOT USE. Returns a string array containing Groups and nested Groups the specified User is member of. This includes mailing lists. UserName is NICE Login or Email. Listname can be 'listname' or 'listname@cern.ch'.
     * 
     * @param userName
     * @return
     *     returns ch.cern.dod.ws.authentication.ArrayOfString
     */
    @WebMethod(operationName = "GetRecursiveGroupsForUser", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/GetRecursiveGroupsForUser")
    @WebResult(name = "GetRecursiveGroupsForUserResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "GetRecursiveGroupsForUser", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetRecursiveGroupsForUser")
    @ResponseWrapper(localName = "GetRecursiveGroupsForUserResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetRecursiveGroupsForUserResponse")
    public ArrayOfString getRecursiveGroupsForUser(
        @WebParam(name = "UserName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String userName);

    /**
     * Returns the user UPN or external UPN from the given Email address.
     * 
     * @param email
     * @return
     *     returns java.lang.String
     */
    @WebMethod(operationName = "GetUPNFromEmail", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/GetUPNFromEmail")
    @WebResult(name = "GetUPNFromEmailResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "GetUPNFromEmail", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetUPNFromEmail")
    @ResponseWrapper(localName = "GetUPNFromEmailResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetUPNFromEmailResponse")
    public String getUPNFromEmail(
        @WebParam(name = "email", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String email);

    /**
     * Returns the members list of the provided mailinglist (use mailinglist's email address). Returned members list also contains members of nested lists. Result is an email address array.
     * 
     * @param listName
     * @return
     *     returns ch.cern.dod.ws.authentication.ArrayOfString
     */
    @WebMethod(operationName = "GetListMembers", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/GetListMembers")
    @WebResult(name = "GetListMembersResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "GetListMembers", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetListMembers")
    @ResponseWrapper(localName = "GetListMembersResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetListMembersResponse")
    public ArrayOfString getListMembers(
        @WebParam(name = "ListName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String listName);

    /**
     * Returns the members list of the provided mailinglist (use mailinglist's email address). Returned members list also contains members of nested lists. Result is a login array, and contains only CERN Accounts, NO Externals!!!.
     * 
     * @param listName
     * @return
     *     returns ch.cern.dod.ws.authentication.ArrayOfString
     */
    @WebMethod(operationName = "GetListMembersLogins_CERNAccountOnly", action = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx/GetListMembersLogins_CERNAccountOnly")
    @WebResult(name = "GetListMembersLogins_CERNAccountOnlyResult", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
    @RequestWrapper(localName = "GetListMembersLogins_CERNAccountOnly", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetListMembersLoginsCERNAccountOnly")
    @ResponseWrapper(localName = "GetListMembersLogins_CERNAccountOnlyResponse", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", className = "ch.cern.dod.ws.authentication.GetListMembersLoginsCERNAccountOnlyResponse")
    public ArrayOfString getListMembersLoginsCERNAccountOnly(
        @WebParam(name = "ListName", targetNamespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx")
        String listName);

}
