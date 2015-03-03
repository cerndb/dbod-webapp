
package ch.cern.dbod.ws.authentication;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ch.cern.dbod.ws.authentication package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ArrayOfUserInfo_QNAME = new QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "ArrayOfUserInfo");
    private final static QName _String_QNAME = new QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "string");
    private final static QName _Boolean_QNAME = new QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "boolean");
    private final static QName _ArrayOfString_QNAME = new QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "ArrayOfString");
    private final static QName _UserInfo_QNAME = new QName("https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", "userInfo");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ch.cern.dbod.ws.authentication
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetUserInfoFromLogin }
     * 
     */
    public GetUserInfoFromLogin createGetUserInfoFromLogin() {
        return new GetUserInfoFromLogin();
    }

    /**
     * Create an instance of {@link UserIsMemberOfList }
     * 
     */
    public UserIsMemberOfList createUserIsMemberOfList() {
        return new UserIsMemberOfList();
    }

    /**
     * Create an instance of {@link ArrayOfUserInfo }
     * 
     */
    public ArrayOfUserInfo createArrayOfUserInfo() {
        return new ArrayOfUserInfo();
    }

    /**
     * Create an instance of {@link ListUsers }
     * 
     */
    public ListUsers createListUsers() {
        return new ListUsers();
    }

    /**
     * Create an instance of {@link GetListMembersLoginsCERNAccountOnlyResponse }
     * 
     */
    public GetListMembersLoginsCERNAccountOnlyResponse createGetListMembersLoginsCERNAccountOnlyResponse() {
        return new GetListMembersLoginsCERNAccountOnlyResponse();
    }

    /**
     * Create an instance of {@link ArrayOfString }
     * 
     */
    public ArrayOfString createArrayOfString() {
        return new ArrayOfString();
    }

    /**
     * Create an instance of {@link SearchGroupsResponse }
     * 
     */
    public SearchGroupsResponse createSearchGroupsResponse() {
        return new SearchGroupsResponse();
    }

    /**
     * Create an instance of {@link CCIDisNice }
     * 
     */
    public CCIDisNice createCCIDisNice() {
        return new CCIDisNice();
    }

    /**
     * Create an instance of {@link GetRecursiveGroupsForUser }
     * 
     */
    public GetRecursiveGroupsForUser createGetRecursiveGroupsForUser() {
        return new GetRecursiveGroupsForUser();
    }

    /**
     * Create an instance of {@link UserInfo }
     * 
     */
    public UserInfo createUserInfo() {
        return new UserInfo();
    }

    /**
     * Create an instance of {@link GetListMembersResponse }
     * 
     */
    public GetListMembersResponse createGetListMembersResponse() {
        return new GetListMembersResponse();
    }

    /**
     * Create an instance of {@link UserIsMemberOfGroup }
     * 
     */
    public UserIsMemberOfGroup createUserIsMemberOfGroup() {
        return new UserIsMemberOfGroup();
    }

    /**
     * Create an instance of {@link GetUserInfo }
     * 
     */
    public GetUserInfo createGetUserInfo() {
        return new GetUserInfo();
    }

    /**
     * Create an instance of {@link GetUPNFromEmail }
     * 
     */
    public GetUPNFromEmail createGetUPNFromEmail() {
        return new GetUPNFromEmail();
    }

    /**
     * Create an instance of {@link SearchGroups }
     * 
     */
    public SearchGroups createSearchGroups() {
        return new SearchGroups();
    }

    /**
     * Create an instance of {@link ListUsersResponse }
     * 
     */
    public ListUsersResponse createListUsersResponse() {
        return new ListUsersResponse();
    }

    /**
     * Create an instance of {@link GetListMembers }
     * 
     */
    public GetListMembers createGetListMembers() {
        return new GetListMembers();
    }

    /**
     * Create an instance of {@link GetListMembersLoginsCERNAccountOnly }
     * 
     */
    public GetListMembersLoginsCERNAccountOnly createGetListMembersLoginsCERNAccountOnly() {
        return new GetListMembersLoginsCERNAccountOnly();
    }

    /**
     * Create an instance of {@link GetUserInfoExResponse }
     * 
     */
    public GetUserInfoExResponse createGetUserInfoExResponse() {
        return new GetUserInfoExResponse();
    }

    /**
     * Create an instance of {@link UserIsMemberOfGroupResponse }
     * 
     */
    public UserIsMemberOfGroupResponse createUserIsMemberOfGroupResponse() {
        return new UserIsMemberOfGroupResponse();
    }

    /**
     * Create an instance of {@link GetUserInfoFromLoginResponse }
     * 
     */
    public GetUserInfoFromLoginResponse createGetUserInfoFromLoginResponse() {
        return new GetUserInfoFromLoginResponse();
    }

    /**
     * Create an instance of {@link GetGroupsForUserResponse }
     * 
     */
    public GetGroupsForUserResponse createGetGroupsForUserResponse() {
        return new GetGroupsForUserResponse();
    }

    /**
     * Create an instance of {@link GetUserInfoResponse }
     * 
     */
    public GetUserInfoResponse createGetUserInfoResponse() {
        return new GetUserInfoResponse();
    }

    /**
     * Create an instance of {@link GetUPNFromEmailResponse }
     * 
     */
    public GetUPNFromEmailResponse createGetUPNFromEmailResponse() {
        return new GetUPNFromEmailResponse();
    }

    /**
     * Create an instance of {@link CCIDisNiceResponse }
     * 
     */
    public CCIDisNiceResponse createCCIDisNiceResponse() {
        return new CCIDisNiceResponse();
    }

    /**
     * Create an instance of {@link GetRecursiveGroupsForUserResponse }
     * 
     */
    public GetRecursiveGroupsForUserResponse createGetRecursiveGroupsForUserResponse() {
        return new GetRecursiveGroupsForUserResponse();
    }

    /**
     * Create an instance of {@link GetUserInfoEx }
     * 
     */
    public GetUserInfoEx createGetUserInfoEx() {
        return new GetUserInfoEx();
    }

    /**
     * Create an instance of {@link GetGroupsForUser }
     * 
     */
    public GetGroupsForUser createGetGroupsForUser() {
        return new GetGroupsForUser();
    }

    /**
     * Create an instance of {@link UserIsMemberOfListResponse }
     * 
     */
    public UserIsMemberOfListResponse createUserIsMemberOfListResponse() {
        return new UserIsMemberOfListResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfUserInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", name = "ArrayOfUserInfo")
    public JAXBElement<ArrayOfUserInfo> createArrayOfUserInfo(ArrayOfUserInfo value) {
        return new JAXBElement<ArrayOfUserInfo>(_ArrayOfUserInfo_QNAME, ArrayOfUserInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", name = "string")
    public JAXBElement<String> createString(String value) {
        return new JAXBElement<String>(_String_QNAME, String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", name = "boolean")
    public JAXBElement<Boolean> createBoolean(Boolean value) {
        return new JAXBElement<Boolean>(_Boolean_QNAME, Boolean.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfString }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", name = "ArrayOfString")
    public JAXBElement<ArrayOfString> createArrayOfString(ArrayOfString value) {
        return new JAXBElement<ArrayOfString>(_ArrayOfString_QNAME, ArrayOfString.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UserInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "https://winservices-soap.web.cern.ch/winservices-soap/Generic/Authentication.asmx", name = "userInfo")
    public JAXBElement<UserInfo> createUserInfo(UserInfo value) {
        return new JAXBElement<UserInfo>(_UserInfo_QNAME, UserInfo.class, null, value);
    }

}
