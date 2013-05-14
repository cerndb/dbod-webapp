package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODMetric;

/**
 * Class containing constants for DBOnDemand.
 * @author Daniel Gomez Blanco
 * @version 22/11/2011
 */
public class DODConstants {
    
    //Session attributes
    public static final String ATTRIBUTE_SHOW_ALL = "showAll";
    public static final String ATTRIBUTE_SHOW_ALL_TO_DESTROY = "showAllToDestroy";
    public static final String ATTRIBUTE_SHOW_ALL_UPGRADES = "showAllUpgrades";
    public static final String ATTRIBUTE_SHOW_ALL_JOB_STATS = "showAllJobStats";
    public static final String ATTRIBUTE_SHOW_ALL_COMMAND_STATS = "showAllCommandStats";
    public static final String ATTRIBUTE_ADMIN_FILTER_DB_NAME = "adminFilterDbName";
    public static final String ATTRIBUTE_ADMIN_FILTER_HOST= "adminFilterHost";
    public static final String ATTRIBUTE_ADMIN_FILTER_USERNAME = "adminFilterUsername";
    public static final String ATTRIBUTE_ADMIN_FILTER_E_GROUP = "adminFilterEGroup";
    public static final String ATTRIBUTE_ADMIN_FILTER_CATEGORY = "adminFilterCategory";
    public static final String ATTRIBUTE_ADMIN_FILTER_PROJECT = "adminFilterProject";
    public static final String ATTRIBUTE_ADMIN_FILTER_DB_TYPE = "adminFilterDbType";
    public static final String ATTRIBUTE_ADMIN_FILTER_ACTIONS = "adminFilterActions";
    public static final String ATTRIBUTE_ADMIN_FILTER_JOB_DB_NAME = "adminFilterJobDbName";
    public static final String ATTRIBUTE_ADMIN_FILTER_JOB_COMMAND_NAME = "adminFilterJobCommandName";
    public static final String ATTRIBUTE_USER_FILTER_DB_NAME = "userFilterDbName";
    public static final String ATTRIBUTE_USER_FILTER_HOST= "userFilterHost";
    public static final String ATTRIBUTE_USER_FILTER_USERNAME = "userFilterUsername";
    public static final String ATTRIBUTE_USER_FILTER_E_GROUP = "userFilterEGroup";
    public static final String ATTRIBUTE_USER_FILTER_CATEGORY = "userFilterCategory";
    public static final String ATTRIBUTE_USER_FILTER_PROJECT = "userFilterProject";
    public static final String ATTRIBUTE_USER_FILTER_DB_TYPE = "userFilterDbType";
    public static final String ATTRIBUTE_USER_FILTER_ACTIONS = "userFilterActions";
    public static final String ATTRIBUTE_USER_FILTER_JOB_DB_NAME = "userFilterJobDbName";
    public static final String ATTRIBUTE_USER_FILTER_JOB_COMMAND_NAME = "userFilterJobCommandName";
    
    //Variables
    public static final String ENVIRONMENT_CONTEXT = "java:/comp/env";
    public static final String DATA_SOURCE_DBOD = "jdbc/dbondemand";
    public static final String DATA_SOURCE_MONITORING = "jdbc/monitoring";
    public static final String WS_USER_PSWD = "wsUserPswd";
    public static final String WS_USER = "wsUser";
    public static final String WS_PSWD = "wsPswd";
    public static final String ADMIN_E_GROUP = "dbondemand-admin";
    public static final String LOCALE_COOKIE = "dbondemand.locale";
    public static final String ADFS_FULLNAME = "ADFS_FULLNAME";
    public static final String ADFS_LOGIN = "ADFS_LOGIN";
    public static final String ADFS_CCID = "ADFS_PERSONID";
    public static final String ADFS_GROUP = "ADFS_GROUP";
    public static final String DATE_FORMAT = "dd/MM/yyyy";
    public static final String DATE_TIME_FORMAT = "dd/MM/yyyy H:mm:ss";
    public static final String DATE_TIME_FORMAT_PITR = "yyyy-MM-dd_HH:mm:ss";
    public static final String TIME_FORMAT = "H:mm:ss";
    public static final String INSTANCE = "instance";
    public static final String MASTER = "master";
    public static final String SLAVE = "slave";
    public static final String SHARED_INSTANCE_LIST = "sharedInstanceList";
    public static final String MONITORING_TYPE = "tgtt";
    public static final String MONITORING_TYPE_MYSQL = "M";
    public static final String MONITORING_TYPE_NODE = "N";
    public static final String MONITORING_TYPE_ORACLE = "C";
    public static final String LEMON_URL = "http://lemonweb.cern.ch/lemon-web/info.php?entity=";
    public static final String PARAM_HOST = "host";
    public static final int MAX_SIZE_CONFIG_FILE = 512;
    public static final int MAX_DB_SIZE = 1000;
    public static final int MAX_NO_CONNECTIONS = 1000;
    public static final int MAX_USERNAME_LENGTH = 32;
    public static final int MAX_DB_NAME_LENGTH = 8;
    public static final int MAX_E_GROUP_LENGTH = 256;
    public static final int MAX_PROJECT_LENGTH = 128;
    public static final int MAX_HOST_LENGTH = 128;
    public static final int MAX_DESCRIPTION_LENGTH = 1024;
    public static final int MAX_VERSION_LENGTH = 128;
    public static final int MIN_INTERVAL_HOURS = 6;
    public static final int DEFAULT_INTERVAL_HOURS = 24;
    public static final DODMetric[] MYSQL_OVERVIEW_METRICS = {new DODMetric("Threads_connected","Number of currently open connections","M",""),
                                                                new DODMetric("Aborted_connects","Number of aborted connections","M",""),
                                                                new DODMetric("Innodb_buffer_pool_reads","Number of logical reads that innoDB had to read directly from the disk","M",""),
                                                                new DODMetric("Innodb_data_reads","Total number of data reads","M",""),
                                                                new DODMetric("Innodb_buffer_pool_write_requests","Number of writes done to the innoDB buffer pool","M",""),
                                                                new DODMetric("Innodb_data_writes","Total number of data writes","M",""),
                                                                new DODMetric("Innodb_data_read","Amount of data read since the server was started","M",""),
                                                                new DODMetric("Innodb_data_written","Amount of data written","M",""),
                                                                new DODMetric("Qcache_hits","Number of query cache hits","M",""),
                                                                new DODMetric("Qcache_not_cached","Number of noncached queries","M","")};
    public static final DODMetric[] ORACLE_OVERVIEW_METRICS = {new DODMetric("2147","Average Active Sessions","C","Active Sessions"),
                                                                new DODMetric("2003","User Transaction Per Sec","C","Transactions Per Second"),
                                                                new DODMetric("2093","Physical Read Total Bytes Per Sec","C","Bytes Per Second"),
                                                                new DODMetric("2092","Physical Read Total IO Requests Per Sec","C","Requests Per Second"),
                                                                new DODMetric("2124","Physical Write Total Bytes Per Sec","C","Bytes Per Second"),
                                                                new DODMetric("2100","Physical Write Total IO Requests Per Sec","C","Requests Per Second"),
                                                                new DODMetric("2106","SQL Service Response Time","C","CentiSeconds Per Call"),
                                                                new DODMetric("2057","Host CPU Utilization (%)","C","% Busy/(Idle+Busy)"),
                                                                new DODMetric("2016","Redo Generated Per Sec","C","Bytes Per Second"),
                                                                new DODMetric("2034","Redo Writes Per Sec","C","Writes Per Second")};
    public static final int MONITORING_DAYS = 15;
    public static final int MONITORING_OVERVIEW_DAYS = 7;

    //DB values
    public static final String INSTANCE_STATE_AWAITING_APPROVAL = "AWAITING_APPROVAL";
    public static final String INSTANCE_STATE_JOB_PENDING = "JOB_PENDING";
    public static final String INSTANCE_STATE_RUNNING = "RUNNING";
    public static final String INSTANCE_STATE_STOPPED = "STOPPED";
    public static final String INSTANCE_STATE_MAINTENANCE = "MAINTENANCE";
    public static final String INSTANCE_STATE_BUSY = "BUSY";
    public static final String INSTANCE_STATE_UNKNOWN = "UNKNOWN";
    public static final String JOB_STATE_PENDING = "PENDING";
    public static final String JOB_STATE_RUNNING = "RUNNING";
    public static final String JOB_STATE_FINISHED_OK = "FINISHED_OK";
    public static final String JOB_STATE_FINISHED_FAIL = "FINISHED_FAIL";
    public static final String JOB_STATE_FINISHED_WARNING = "FINISHED_WARNING";
    public static final String JOB_STARTUP = "STARTUP";
    public static final String JOB_SHUTDOWN = "SHUTDOWN";
    public static final String JOB_UPLOAD = "UPLOAD_CONFIG";
    public static final String JOB_BACKUP = "BACKUP";
    public static final String JOB_BACKUP_TO_TAPE = "BACKUP_TO_TAPE";
    public static final String JOB_ENABLE_BACKUPS_TO_TAPE = "ENABLE_BACKUPS_TO_TAPE";
    public static final String JOB_DISABLE_BACKUPS_TO_TAPE = "DISABLE_BACKUPS_TO_TAPE";
    public static final String JOB_DISABLE_BACKUPS = "DISABLE_AUTOMATIC_BACKUPS";
    public static final String JOB_ENABLE_BACKUPS = "ENABLE_AUTOMATIC_BACKUPS";
    public static final String JOB_RESTORE = "RESTORE";
    public static final String JOB_UPGRADE = "UPGRADE";
    public static final String JOB_MONITOR = "MONITOR";
    public static final String JOB_CLEANUP = "CLEANUP";
    public static final String DB_TYPE_ORACLE = "ORACLE";
    public static final String DB_TYPE_MYSQL = "MYSQL";
    public static final String PARAM_INSTANCE_NAME = "INSTANCE_NAME";
    public static final String PREFIX_INSTANCE_NAME = "dod_";
    public static final String PARAM_SNAPSHOT = "SNAPSHOT";
    public static final String PARAM_CONFIG_FILE = "CONFIG_FILE";
    public static final String CONFIG_FILE_MY_CNF = "MY_CNF";
    public static final String PARAM_CONFIG_PATH = "CONFIG_PATH";
    public static final String CONFIG_PATH_MY_CNF = "my.cnf";
    public static final String PARAM_BACKUP_INTERVAL = "BACKUP_INTERVAL";
    public static final String PARAM_PITR_TIME = "TIMESTAMP";
    public static final String CATEGORY_OFFICIAL = "OFFICIAL";
    public static final String CATEGORY_PERSONAL = "PERSONAL";
    public static final String CATEGORY_TEST = "TEST";
    public static final String PARAM_VERSION_FROM = "VERSION_FROM";
    public static final String PARAM_VERSION_TO = "VERSION_TO";
    public static final String PARAM_LISTENER_BOOLEAN = "LISTENER_BOOLEAN";

    //Pages
    public static final String PAGE_INSTANCE = "/instance.zul";
    public static final String PAGE_INSTANCE_NOT_FOUND = "/instance_not_found.zul";
    public static final String PAGE_NOT_AUTHORIZED = "/not_authorized.zul";
    public static final String PAGE_ERROR = "/error.zul";
    public static final String PAGE_INDEX = "/index.zul";
    public static final String PAGE_ADMIN = "/admin/admin.zul";
    public static final String PAGE_MONITORING_OVERVIEW = "/monitoring_overview.zul";

    //Labels
    public static final String LABEL_WELCOME = "welcome";
    public static final String LABEL_STATE = "state";
    public static final String LABEL_JOB = "jobAction";
    public static final String LABEL_JOB_STATE = "jobState";
    public static final String LABEL_DB_TYPE = "dbType";
    public static final String LABEL_INSTANCE_TITLE = "instanceTitle";
    public static final String LABEL_CREATED_ON = "createdOn";
    public static final String LABEL_SELECT_ONE = "selectOne";
    public static final String LABEL_RESTORE_TITLE = "restoreTitle";
    public static final String LABEL_AVAILABLE_SNAPSHOTS = "availableSnapshots";
    public static final String LABEL_SELECT_SNAPSHOT = "selectSnapshot";
    public static final String LABEL_CANCEL = "cancel";
    public static final String LABEL_ACCEPT = "accept";
    public static final String LABEL_SELECT_METRIC = "selectMetric";
    public static final String LABEL_METRICS_TITLE = "metricsTitle";
    public static final String LABEL_AUTOMATIC_BACKUP = "automaticBackup";
    public static final String LABEL_HOURS = "hours";
    public static final String LABEL_BACKUP_TITLE = "backupTitle";
    public static final String LABEL_BACKUP_POPUP = "backupPopup";
    public static final String LABEL_BACKUP_CONFIG = "backupConfiguration";
    public static final String LABEL_BACKUP_TO_TAPE = "backupToTape";
    public static final String LABEL_BACKUP_TO_TAPE_WARNING = "backupToTapeWarning";
    public static final String LABEL_BACKUP_NOW_TITLE = "backupNowTitle";
    public static final String LABEL_BACKUP_NOW = "backupNow";
    public static final String LABEL_INTRODUCTION = "introduction";
    public static final String LABEL_NO_CONFIG_FILES = "noConfigFiles";
    public static final String LABEL_NO_JOBS = "noJobs";
    public static final String LABEL_NO_SNAPSHOTS = "noSnapshots";
    public static final String LABEL_CATEGORY = "category";
    public static final String LABEL_CONFIG_TITLE = "configTitle";
    public static final String LABEL_CONFIG_MESSAGE = "configMessage";
    public static final String LABEL_CONFIG_UPLOAD = "configUpload";
    public static final String LABEL_CONFIG_DOWNLOAD = "configDownload";
    public static final String LABEL_CONFIG = "config";
    public static final String LABEL_LEMON_MESSAGE= "lemonMessage";
    public static final String LABEL_LEMON_LINK = "lemonLink";
    public static final String LABEL_CLOSE = "close";
    public static final String LABEL_DESTROY_TITLE = "destroyTitle";
    public static final String LABEL_DESTROY_MESSAGE = "destroyMessage";
    public static final String LABEL_SNAPSHOTS_FOR_DAY = "snapshotsForDay";
    public static final String LABEL_SNAPSHOTS_FOR_DAY_EMPTY = "snapshotsForDayEmpty";
    public static final String LABEL_RESTORE_CONFIRM_TITLE = "restoreConfirmTitle";
    public static final String LABEL_RESTORE_CONFIRM_MESSAGE = "restoreConfirmMessage";
    public static final String LABEL_UPGRADE_TITLE = "upgradeTitle";
    public static final String LABEL_UPGRADE_MESSAGE_FROM = "upgradeMessageFrom";
    public static final String LABEL_UPGRADE_MESSAGE_TO = "upgradeMessageTo";
    public static final String LABEL_APPLY_CHANGES = "applyChanges";
    public static final String LABEL_ADD_UPGRADE_TITLE = "addUpgradeTitle";
    public static final String LABEL_ADD_UPGRADE_MESSAGE = "addUpgradeMessage";
    public static final String LABEL_VERSION_FROM = "versionFrom";
    public static final String LABEL_VERSION_TO = "versionTo";
    public static final String LABEL_DELETE_UPGRADE = "deleteUpgrade";
    public static final String LABEL_FILES_TITLE = "filesTitle";
    public static final String LABEL_FILES_POPUP = "filesPopup";
    public static final String LABEL_LOGS_TITLE = "logsTitle";
    public static final String LABEL_LOGS_MESSAGE = "logsMessage";
    public static final String LABEL_LOGS_DOWNLOAD = "logsDownload";
    public static final String LABEL_NO_LOGS = "noLogs";
    public static final String LABEL_SHARED_INSTANCE_WARNING = "sharedInstanceWarning";
    public static final String LABEL_UPGRADE_SLAVE_FIRST = "upgradeSlaveFirst";
    public static final String LABEL_MONITORING_OVERVIEW_TITLE = "monitoringOverviewTitle";
    public static final String LABEL_MONITORING_OVERVIEW_MESSAGE = "monitoringOerviewMessage";
    public static final String LABEL_MONITORING_OVERVIEW = "monitoringOverview";
    public static final String LABEL_MONITORING_OVERVIEW_WARNING = "monitoringOverviewWarning";
    public static final String LABEL_SHUTDOWN_TITLE = "shutdownTitle";
    public static final String LABEL_SHUTDOWN_WARNING = "shutdownWarning";
    public static final String LABEL_MESSAGE_NO_CERN = "messageNoCern";
    public static final String LABEL_METRICS = "metrics";
    public static final String LABEL_RESCUE_TITLE = "rescueTitle";
    public static final String LABEL_RESCUE_MESSAGE = "rescueMessage";

    //Errors
    public static final String ERROR_DB_NAME_EMPTY = "errorDbNameEmpty";
    public static final String ERROR_DB_NAME_LENGTH = "errorDbNameLength";
    public static final String ERROR_DB_NAME_CHARS = "errorDbNameChars";
    public static final String ERROR_E_GROUP_LENGTH = "errorEGroupLength";
    public static final String ERROR_E_GROUP_CREATION = "errorEGroupCreation";
    public static final String ERROR_E_GROUP_CHARS = "errorEGroupChars";
    public static final String ERROR_E_GROUP_DASH = "errorEGroupDash";
    public static final String ERROR_E_GROUP_SEARCH = "errorEGroupSearch";
    public static final String ERROR_EXPIRY_DATE_FORMAT = "errorExpiryDateFormat";
    public static final String ERROR_EXPIRY_DATE_FUTURE = "errorExpiryDateFuture";
    public static final String ERROR_DB_TYPE_EMPTY = "errorDbTypeEmpty";
    public static final String ERROR_DB_TYPE_LIST = "errorDbTypeList";
    public static final String ERROR_DB_SIZE_EMPTY = "errorDbSizeEmpty";
    public static final String ERROR_DB_SIZE_RANGE = "errorDbSizeRange";
    public static final String ERROR_INSTANCE_CREATION = "errorInstanceCreation";
    public static final String ERROR_INTEGER_FORMAT = "errorIntegerFormat";
    public static final String ERROR_NO_CONNECTIONS_RANGE = "errorNoConnectionsRange";
    public static final String ERROR_DESCRIPTION_LENGTH = "errorDescriptionLength";
    public static final String ERROR_UPLOADING_CONFIG_FILE = "errorUploadingConfigFile";
    public static final String ERROR_UPLOADING_CONFIG_FILE_SIZE = "errorUploadingConfigFileSize";
    public static final String ERROR_DOWNLOADING_CONFIG_FILE = "errorDownloadingConfigFile";
    public static final String ERROR_DISPATCHING_JOB = "errorDispatchingJob";
    public static final String ERROR_DISPATCHING_UPGRADE_JOB = "errorDispatchingUpgradeJob";
    public static final String ERROR_SELECT_SNAPSHOT = "errorSelectSnapshot";
    public static final String ERROR_OBTAINING_METRICS = "errorObtainingMetrics";
    public static final String ERROR_DISPLAYING_HELP = "errorDisplayingHelp";
    public static final String ERROR_INSTANCE_UNIQUE = "errorInstanceUnique";
    public static final String ERROR_CATEGORY_EMPTY = "errorCategoryEmpty";
    public static final String ERROR_CATEGORY_LIST = "errorCategoryList";
    public static final String ERROR_PROJECT_LENGTH = "errorProjectLength";
    public static final String ERROR_CONFIG_TYPE = "errorConfigType";
    public static final String ERROR_UPDATING_INSTANCE = "errorUpdatingInstance";
    public static final String ERROR_COLLECTIVE_ACTION = "errorCollectiveAction";
    public static final String ERROR_DISABLING_AUTO_BACKUPS = "errorDisablingAutoBackups";
    public static final String ERROR_NO_SNAPSHOT = "errorNoSnapshot";
    public static final String ERROR_SNAPSHOT_PAST = "errorSnapshotPast";
    public static final String ERROR_USERNAME_EMPTY = "errorUsernameEmpty";
    public static final String ERROR_USERNAME_LENGTH = "errorUsernameLength";
    public static final String ERROR_USERNAME_CHARS = "errorUsernameChars";
    public static final String ERROR_USERNAME_WS = "errorUsernameWS";
    public static final String ERROR_USERNAME_NOT_FOUND = "errorUsernameNotFound";
    public static final String ERROR_DISPLAYING_CONFIRM_WINDOW = "errorDisplayingConfirmWindow";
    public static final String ERROR_BACKUP_TO_TAPE_DATE = "errorBackupToTapeDate";
    public static final String ERROR_BACKUP_TO_TAPE_DAY_EMPTY = "errorBackupToTapeDayEmpty";
    public static final String ERROR_BACKUP_TO_TAPE_TIME_EMPTY = "errorBackupToTapeTimeEmpty";
    public static final String ERROR_ADDING_UPGRADE = "errorAddingUpgrade";
    public static final String ERROR_DELETING_UPGRADE = "errorDeletingUpgrade";
    public static final String ERROR_VERSION_EMPTY = "errorVersionEmpty";
    public static final String ERROR_VERSION_LENGTH = "errorVersionLength";
    public static final String ERROR_OPENING_ADD_UPGRADE = "errorOpeningAddUpgrade";
    public static final String ERROR_SLOW_LOG_FILE = "errorSlowLogFile";
    public static final String ERROR_DOWNLOADING_SLOW_LOG_FILE = "errorDownloadingSlowLogFile";
    public static final String ERROR_HOST_LENGTH = "errorHostLength";
    public static final String ERROR_HOST_EMPTY = "errorHostEmpty";
    public static final String ERROR_HOST_CHARS = "errorHostChars";
    public static final String ERROR_MASTER_DOES_NOT_EXIST = "errorMasterDoesNotExist";
    public static final String ERROR_PIT_ONE_MINUTE = "errorPITOneMinute";
    public static final String ERROR_DESTROYING_INSTANCE = "errorDestroyingInstance";
    public static final String ERROR_RESCUING_INSTANCE = "errorRescuingInstance";
    public static final String ERROR_INSTANCE_ON_FIM = "errorInstanceOnFIM";

    //Images
    public static final String IMG_MAINTENANCE = "/img/maintenance.png";
    public static final String IMG_AWAITING_APPROVAL = "/img/awaiting.png";
    public static final String IMG_PENDING = "/img/pending.png";
    public static final String IMG_RUNNING = "/img/running.png";
    public static final String IMG_STOPPED = "/img/stopped.png";
    public static final String IMG_BUSY = "/img/busy.png";
    public static final String IMG_UNKNOWN = "/img/awaiting.png";
    public static final String IMG_STARTUP = "/img/startup.png";
    public static final String IMG_SHUTDOWN = "/img/shutdown.png";
    public static final String IMG_CONFIG = "/img/config.png";
    public static final String IMG_UPLOAD = "/img/upload.png";
    public static final String IMG_DOWNLOAD = "/img/download.png";
    public static final String IMG_BACKUP = "/img/backup.png";
    public static final String IMG_RESTORE = "/img/restore.png";
    public static final String IMG_UPGRADE = "/img/upgrade.png";
    public static final String IMG_MONITOR = "/img/monitor.png";
    public static final String IMG_DESTROY = "/img/destroy.png";
    public static final String IMG_CANCEL = "/img/cancel.png";
    public static final String IMG_ACCEPT = "/img/accept.png";
    public static final String IMG_WARNING = "/img/warning.png";
    public static final String IMG_FILES = "/img/files.png";
    public static final String IMG_LOGS = "/img/logs.png";
    public static final String IMG_RESCUE = "/img/rescue.png";

    //Style classes
    public static final String STYLE_BUTTON = "button";
    public static final String STYLE_BUTTON_DISABLED = "buttonDisabled";
    public static final String STYLE_BIG_BUTTON = "bigButton";
    public static final String STYLE_BIG_BUTTON_DISABLED = "bigButtonDisabled";
    public static final String STYLE_JOB_STATE = "jobState";
    public static final String STYLE_TITLE = "title";
}