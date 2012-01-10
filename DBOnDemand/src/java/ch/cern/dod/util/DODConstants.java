package ch.cern.dod.util;

/**
 * Class containing constants for DBOnDemand.
 * @author Daniel Gomez Blanco
 * @version 22/11/2011
 */
public class DODConstants {
    
    //Variables
    public static final String ENVIRONMENT_CONTEXT = "java:/comp/env";
    public static final String DATA_SOURCE = "jdbc/dbondemand";
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
    public static final String MONITORING_URL = "http://phydb.web.cern.ch/phydb/racmon/conf/upl/plot_metric.php?tgtt=M";
    public static final String MONITORING_INSTANCE = "tgt";
    public static final String MONITORING_METRIC = "metric";
    public static final String HELP_DIR = "/afs/cern.ch/project/jps/reps/DBOnDemand/user_help_html/";
    public static final String ADMIN_HELP_DIR = "/afs/cern.ch/project/jps/reps/DBOnDemand/admin_help_html/";
    public static final String LEMON_URL = "http://lemonweb.cern.ch/lemon-web/info.php?entity=";
    public static final String PARAM_HOST = "host";
    public static final int MAX_SIZE_CONFIG_FILE = 512;
    public static final int MAX_DB_SIZE = 1000;
    public static final int MAX_NO_CONNECTIONS = 1000;
    public static final int MAX_USERNAME_LENGTH = 32;
    public static final int MAX_DB_NAME_LENGTH = 128;
    public static final int MAX_E_GROUP_LENGTH = 256;
    public static final int MAX_PROJECT_LENGTH = 128;
    public static final int MAX_DESCRIPTION_LENGTH = 1024;
    public static final int MIN_INTERVAL_HOURS = 6;
    public static final int DEFAULT_INTERVAL_HOURS = 24;

    //DB values
    public static final String INSTANCE_STATE_AWAITING_APPROVAL = "AWAITING_APPROVAL";
    public static final String INSTANCE_STATE_JOB_PENDING = "JOB_PENDING";
    public static final String INSTANCE_STATE_RUNNING = "RUNNING";
    public static final String INSTANCE_STATE_STOPPED = "STOPPED";
    public static final String JOB_STATE_PENDING = "PENDING";
    public static final String JOB_STATE_RUNNING = "RUNNING";
    public static final String JOB_STATE_FINISHED_OK = "FINISHED_OK";
    public static final String JOB_STATE_FINISHED_FAIL = "FINISHED_FAIL";
    public static final String JOB_STARTUP = "STARTUP";
    public static final String JOB_SHUTDOWN = "SHUTDOWN";
    public static final String JOB_UPLOAD = "UPLOAD_CONFIG";
    public static final String JOB_BACKUP = "BACKUP";
    public static final String JOB_DISABLE_BACKUPS = "DISABLE_AUTOMATIC_BACKUPS";
    public static final String JOB_ENABLE_BACKUPS = "ENABLE_AUTOMATIC_BACKUPS";
    public static final String JOB_RESTORE = "RESTORE";
    public static final String JOB_MONITOR = "MONITOR";
    public static final String JOB_DESTROY = "DESTROY";
    public static final String DB_TYPE_ORACLE = "ORACLE";
    public static final String DB_TYPE_MYSQL = "MYSQL";
    public static final String PARAM_INSTANCE_NAME = "INSTANCE_NAME";
    public static final String PREFIX_INSTANCE_NAME = "dod_";
    public static final String PARAM_SNAPSHOT = "SNAPSHOT";
    public static final String PARAM_CONFIG_FILE = "CONFIG_FILE";
    public static final String CONFIG_FILE_MY_CNF = "MY_CNF";
    public static final String PARAM_CONFIG_PATH = "CONFIG_PATH";
    public static final String CONFIG_PATH_MY_CNF = "/etc/my.cnf";
    public static final String PARAM_BACKUP_INTERVAL = "BACKUP_INTERVAL";
    public static final String PARAM_PITR_TIME = "TIMESTAMP";
    public static final String CATEGORY_OFFICIAL = "OFFICIAL";
    public static final String CATEGORY_PERSONAL = "PERSONAL";
    public static final String CATEGORY_TEST = "TEST";

    //Pages
    public static final String PAGE_INSTANCE = "/instance.zul";
    public static final String PAGE_INSTANCE_NOT_FOUND = "/instance_not_found.zul";
    public static final String PAGE_NOT_AUTHORIZED = "/not_authorized.zul";
    public static final String PAGE_ERROR = "/error.zul";
    public static final String PAGE_INDEX = "/index.zul";
    public static final String PAGE_ADMIN = "/admin/admin.zul";

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
    public static final String LABEL_BACKUP_MESSAGE = "backupMessage";
    public static final String LABEL_BACKUP_TO_TAPE = "backupToTape";
    public static final String LABEL_BACKUP_NOW = "backupNow";
    public static final String LABEL_INTRODUCTION = "introduction";
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
    public static final String ERROR_DB_SIZE_EMPTY = "errorDbTypeEmpty";
    public static final String ERROR_DB_SIZE_RANGE = "errorDbTypeList";
    public static final String ERROR_INSTANCE_CREATION = "errorInstanceCreation";
    public static final String ERROR_INTEGER_FORMAT = "errorIntegerFormat";
    public static final String ERROR_NO_CONNECTIONS_RANGE = "errorNoConnectionsRange";
    public static final String ERROR_DESCRIPTION_LENGTH = "errorDescriptionLength";
    public static final String ERROR_UPLOADING_CONFIG_FILE = "errorUploadingConfigFile";
    public static final String ERROR_UPLOADING_CONFIG_FILE_SIZE = "errorUploadingConfigFileSize";
    public static final String ERROR_DOWNLOADING_CONFIG_FILE = "errorDownloadingConfigFile";
    public static final String ERROR_DISPATCHING_JOB = "errorDispatchingJob";
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

    //Images
    public static final String IMG_AWAITING_APPROVAL = "/img/awaiting.png";
    public static final String IMG_PENDING = "/img/pending.png";
    public static final String IMG_RUNNING = "/img/running.png";
    public static final String IMG_STOPPED = "/img/stopped.png";
    public static final String IMG_STARTUP = "/img/startup.png";
    public static final String IMG_SHUTDOWN = "/img/shutdown.png";
    public static final String IMG_CONFIG = "/img/config.png";
    public static final String IMG_UPLOAD = "/img/upload.png";
    public static final String IMG_DOWNLOAD = "/img/download.png";
    public static final String IMG_BACKUP = "/img/backup.png";
    public static final String IMG_RESTORE = "/img/restore.png";
    public static final String IMG_MONITOR = "/img/monitor.png";
    public static final String IMG_DESTROY = "/img/destroy.png";
    public static final String IMG_CANCEL = "/img/cancel.png";
    public static final String IMG_ACCEPT = "/img/accept.png";
    public static final String IMG_WARNING = "/img/warning.png";

    //Style classes
    public static final String STYLE_BUTTON = "button";
    public static final String STYLE_BUTTON_DISABLED = "buttonDisabled";
    public static final String STYLE_BIG_BUTTON = "bigButton";
    public static final String STYLE_BIG_BUTTON_DISABLED = "bigButtonDisabled";
    public static final String STYLE_JOB_STATE = "jobState";
    public static final String STYLE_TITLE = "title";
}
