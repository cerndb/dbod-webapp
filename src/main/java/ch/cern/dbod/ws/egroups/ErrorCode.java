
package ch.cern.dbod.ws.egroups;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ErrorCode.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="ErrorCode">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NOT_VALID_USER"/>
 *     &lt;enumeration value="NOT_FOUND"/>
 *     &lt;enumeration value="EGROUP_NAME_BAD_FORMATED"/>
 *     &lt;enumeration value="INSUFFICIENT_PRIVILEGES"/>
 *     &lt;enumeration value="PERSON_ID_NOT_FOUND"/>
 *     &lt;enumeration value="EXPIRE_DATE_BAD_FORMATTED"/>
 *     &lt;enumeration value="NAME_AND_ID_NOT_CORRESPONDS"/>
 *     &lt;enumeration value="EGROUP_RECENTLY_DELETED"/>
 *     &lt;enumeration value="EMAIL_PROP_ARCHIVE_NOT_VALID"/>
 *     &lt;enumeration value="EMAIL_PROP_MAIL_SIZE_NOT_VALID"/>
 *     &lt;enumeration value="EXPIRATION_DATE_MANDATORY"/>
 *     &lt;enumeration value="EXPIRATION_DATE_NOT_VALID"/>
 *     &lt;enumeration value="INTERNAL_DB_ERROR"/>
 *     &lt;enumeration value="DATABASE_CONFIGURATION_NOT_FOUND"/>
 *     &lt;enumeration value="EGROUP_MEMBER_OF_ANOTHER"/>
 *     &lt;enumeration value="NOT_MAILING_SEGURITY_USAGE"/>
 *     &lt;enumeration value="IS_ALREADY_MEMBER"/>
 *     &lt;enumeration value="TOPIC_ALREADY_EXISTS"/>
 *     &lt;enumeration value="ALIAS_MUST_HAVE_HYPHEN"/>
 *     &lt;enumeration value="ALIAS_ALREADY_EXISTS"/>
 *     &lt;enumeration value="NAME_ALREADY_RESERVED"/>
 *     &lt;enumeration value="NAME_TOO_LONG"/>
 *     &lt;enumeration value="EGROUP_ALREADY_EXISTS"/>
 *     &lt;enumeration value="MEMBER_NOT_FOUND"/>
 *     &lt;enumeration value="ALIAS_NOT_FOUND"/>
 *     &lt;enumeration value="SELF_EGROUP_ALREADY_EXISTS"/>
 *     &lt;enumeration value="ALREADY_ACTIVE"/>
 *     &lt;enumeration value="IS_BLOCKED"/>
 *     &lt;enumeration value="MUST_BE_MODERATOR"/>
 *     &lt;enumeration value="STATUS_CHANGE_NOT_ALLOWED"/>
 *     &lt;enumeration value="EXPIRATION_DATE_CANT_BE_PROLONGUED"/>
 *     &lt;enumeration value="BLOCKING_REASON_UNDEFINED"/>
 *     &lt;enumeration value="USAGE_TYPE_NOT_VALID"/>
 *     &lt;enumeration value="NOT_LOGGED"/>
 *     &lt;enumeration value="ALREADY_DELETED"/>
 *     &lt;enumeration value="NAME_MUST_HAVE_HYPHEN"/>
 *     &lt;enumeration value="IS_ALREADY_ALLOWED_TO_POST"/>
 *     &lt;enumeration value="OWNER_ID_NOT_FOUND"/>
 *     &lt;enumeration value="WARNING"/>
 *     &lt;enumeration value="UNEXPECTED_ERROR"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "ErrorCode")
@XmlEnum
public enum ErrorCode {

    NOT_VALID_USER,
    NOT_FOUND,
    EGROUP_NAME_BAD_FORMATED,
    INSUFFICIENT_PRIVILEGES,
    PERSON_ID_NOT_FOUND,
    EXPIRE_DATE_BAD_FORMATTED,
    NAME_AND_ID_NOT_CORRESPONDS,
    EGROUP_RECENTLY_DELETED,
    EMAIL_PROP_ARCHIVE_NOT_VALID,
    EMAIL_PROP_MAIL_SIZE_NOT_VALID,
    EXPIRATION_DATE_MANDATORY,
    EXPIRATION_DATE_NOT_VALID,
    INTERNAL_DB_ERROR,
    DATABASE_CONFIGURATION_NOT_FOUND,
    EGROUP_MEMBER_OF_ANOTHER,
    NOT_MAILING_SEGURITY_USAGE,
    IS_ALREADY_MEMBER,
    TOPIC_ALREADY_EXISTS,
    ALIAS_MUST_HAVE_HYPHEN,
    ALIAS_ALREADY_EXISTS,
    NAME_ALREADY_RESERVED,
    NAME_TOO_LONG,
    EGROUP_ALREADY_EXISTS,
    MEMBER_NOT_FOUND,
    ALIAS_NOT_FOUND,
    SELF_EGROUP_ALREADY_EXISTS,
    ALREADY_ACTIVE,
    IS_BLOCKED,
    MUST_BE_MODERATOR,
    STATUS_CHANGE_NOT_ALLOWED,
    EXPIRATION_DATE_CANT_BE_PROLONGUED,
    BLOCKING_REASON_UNDEFINED,
    USAGE_TYPE_NOT_VALID,
    NOT_LOGGED,
    ALREADY_DELETED,
    NAME_MUST_HAVE_HYPHEN,
    IS_ALREADY_ALLOWED_TO_POST,
    OWNER_ID_NOT_FOUND,
    WARNING,
    UNEXPECTED_ERROR;

    public String value() {
        return name();
    }

    public static ErrorCode fromValue(String v) {
        return valueOf(v);
    }

}
