package ch.cern.dbod.util;

import ch.cern.dbod.db.dao.InstanceDAO;
import ch.cern.dbod.db.dao.JobDAO;
import ch.cern.dbod.db.entity.CommandParam;
import ch.cern.dbod.db.entity.Instance;
import ch.cern.dbod.db.entity.Job;
import ch.cern.dbod.db.entity.Snapshot;
import ch.cern.dbod.exception.ConfigFileSizeException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.UploadEvent;

/**
 * Helper to create jobs.
 * @author Daniel Gomez Blanco
 */
public class JobHelper {

    /**
     * Indicates if the user performing the action is in admin mode
     */
    private boolean adminMode;

    /**
     * DAO to manage jobs.
     */
    JobDAO jobDAO;

    /**
     * DAO to manage instances
     */
    InstanceDAO instanceDAO;

    /**
     * Constructor for this class.
     * @param adminMode indicates if the user performing the action is in admin mode
     */
    public JobHelper(boolean adminMode) {
        this.adminMode = adminMode;
        jobDAO = new JobDAO();
        instanceDAO = new InstanceDAO();
    }
    
    public boolean isAdminMode() {
        return adminMode;
    }

    /**
     * Starts up an instance.
     * @param instance instance to start up.
     * @param username requester of this job.
     * @return true if the creation of this job was successful, false otherwise.
     */
    public boolean doStartup(Instance instance, String username) {
        Date now = new Date();
        //Create job
        Job job = new Job();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(CommonConstants.JOB_STARTUP);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(CommonConstants.JOB_STATE_PENDING);

        //Execute
        int result = jobDAO.insert(job, new ArrayList<CommandParam>());
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(CommonConstants.INSTANCE_STATE_JOB_PENDING);
            Logger.getLogger(JobHelper.class.getName()).log(Level.INFO, "STARTUP JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
            return true;
        } else {
            return false;
        }
    }

    /**
     * Shuts down an instance.
     * @param instance instance to shut down.
     * @param username requester of this job.
     * @return true if the creation of this job was successful, false otherwise.
     */
    public boolean doShutdown(Instance instance, String username) {
        Date now = new Date();
        //Create job
        Job job = new Job();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(CommonConstants.JOB_SHUTDOWN);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(CommonConstants.JOB_STATE_PENDING);
        
        //Param
        List<CommandParam> params = new ArrayList<>();
        
        //Do not stop listener if instance is Oracle in a shared machine
        if (instance.getDbType().equals(CommonConstants.DB_TYPE_ORACLE)) {
            List <Instance> instancesPerHost = instanceDAO.selectInstancesPerHost(instance.getHost(), null);
            
            CommandParam listener = new CommandParam();
            listener.setUsername(instance.getUsername());
            listener.setDbName(instance.getDbName());
            listener.setCommandName(CommonConstants.JOB_SHUTDOWN);
            listener.setType(instance.getDbType());
            listener.setCreationDate(now);
            listener.setName(CommonConstants.PARAM_LISTENER_BOOLEAN);
            
            if (instancesPerHost.size() > 1) {
                listener.setValue("false");
            }
            else {
                listener.setValue("true");
            }
            params.add(listener);
        }

        //Execute
        int result = jobDAO.insert(job, params);
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(CommonConstants.INSTANCE_STATE_JOB_PENDING);
            Logger.getLogger(JobHelper.class.getName()).log(Level.INFO, "SHUTDOWN JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
            return true;
        } else {
            return false;
        }
    }

    /**
     * Uploads a config file and creates the corresponding job to copy the file to the instance.
     * @param instance instance to copy the config file to.
     * @param username requester of this job.
     * @param fileType type of config file to upload.
     * @param reloadConfig make server processes to reload the uploaded config file
     * @param event upload event to get the data from.
     * @return true if the creation of this job was successful, false otherwise.
     * @throws ConfigFileSizeException if the file to be uploaded is too big.
     * @throws UnsupportedEncodingException if the enconding is not supported.
     * @throws IOException if there is an error reading the file.
     */
    public boolean doUpload(Instance instance, String username, String fileType, boolean reloadConfig, UploadEvent event) throws ConfigFileSizeException, UnsupportedEncodingException, IOException {
        //Get config file
        Media media = event.getMedia();
        if (media != null) {
            String data = mediaToString(media);
            if (data.getBytes("UTF-8").length / 1024 <= CommonConstants.MAX_SIZE_CONFIG_FILE) {
                Date now = new Date();
                //Insert job
                Job job = new Job();
                job.setUsername(instance.getUsername());
                job.setDbName(instance.getDbName());
                job.setCommandName(CommonConstants.JOB_UPLOAD);
                job.setType(instance.getDbType());
                job.setCreationDate(now);
                job.setRequester(username);
                if (adminMode)
                    job.setAdminAction(1);
                else
                    job.setAdminAction(0);
                job.setState(CommonConstants.JOB_STATE_PENDING);

                //Create params
                List<CommandParam> params = new ArrayList<>();
                CommandParam configFile = new CommandParam();
                configFile.setUsername(instance.getUsername());
                configFile.setDbName(instance.getDbName());
                configFile.setCommandName(CommonConstants.JOB_UPLOAD);
                configFile.setType(instance.getDbType());
                configFile.setCreationDate(now);
                configFile.setName(CommonConstants.PARAM_CONFIG_FILE);
                configFile.setValue(data);
                params.add(configFile);
                CommandParam path = new CommandParam();
                path.setUsername(instance.getUsername());
                path.setDbName(instance.getDbName());
                path.setCommandName(CommonConstants.JOB_UPLOAD);
                path.setType(instance.getDbType());
                path.setCreationDate(now);
                path.setName(CommonConstants.PARAM_CONFIG_TYPE);
                path.setValue(fileType);
                params.add(path);

                // If DB type is PG, pass config reload param
                if (instance.getDbType().equals(CommonConstants.DB_TYPE_PG)) {
                    CommandParam reloadCfg = new CommandParam();
                    reloadCfg.setUsername(instance.getUsername());
                    reloadCfg.setDbName(instance.getDbName());
                    reloadCfg.setCommandName(CommonConstants.JOB_UPLOAD);
                    reloadCfg.setType(instance.getDbType());
                    reloadCfg.setCreationDate(now);
                    reloadCfg.setName(CommonConstants.PARAM_RELOAD_CONFIG);
                    if (reloadConfig == true) { 
                        reloadCfg.setValue(CommonConstants.CONFIG_RELOAD_TRUE);
                    } else {
                        reloadCfg.setValue(CommonConstants.CONFIG_RELOAD_FALSE);
                    }
                    params.add(reloadCfg);
                }

                int result = jobDAO.insert(job, params);
                //If everything went OK update instance object
                if (result > 0) {
                    Logger.getLogger(JobHelper.class.getName()).log(Level.INFO, "UPLOAD CONFIG FILE JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
                    instance.setState(CommonConstants.INSTANCE_STATE_JOB_PENDING);
                    return true;
                } else {
                    return false;
                }
            } else {
                throw new ConfigFileSizeException("CONFIG FILE IS TOO BIG: " + data.getBytes("UTF-8").length / 1024);
            }
        }
        else
            return true;
    }

    /**
     * Creates a backup for an instance.
     * @param instance instance to create the backup from.
     * @param username requester of this job.
     * @return true if the creation of this job was successful, false otherwise.
     */
    public boolean doBackup(Instance instance, String username) {
        Date now = new Date();
        //Create job
        Job job = new Job();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(CommonConstants.JOB_BACKUP);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(CommonConstants.JOB_STATE_PENDING);
        
        int result = jobDAO.insert(job, new ArrayList<CommandParam>());
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(CommonConstants.INSTANCE_STATE_JOB_PENDING);
            Logger.getLogger(JobHelper.class.getName()).log(Level.INFO, "BACKUP JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
            return true;
        } else {
            return false;
        }
    }   

    /**
     * Restore a backup for an instance.
     * @param instance instance to restore the backup from.
     * @param username requester of this job.
     * @param snapshot snapshot to restore.
     * @param pitrTime offset to restore.
     * @return true if the creation of this job was successful, false otherwise.
     */
    public boolean doRestore(Instance instance, String username, Snapshot snapshot, Date pitrTime) {
        Date now = new Date();
        DateFormat formatter = new SimpleDateFormat(CommonConstants.DATE_TIME_FORMAT_PITR);
        //Create job
        Job job = new Job();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(CommonConstants.JOB_RESTORE);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(CommonConstants.JOB_STATE_PENDING);
        //Create param
        List<CommandParam> params = new ArrayList<>();
        CommandParam snapshotFile = new CommandParam();
        snapshotFile.setUsername(instance.getUsername());
        snapshotFile.setDbName(instance.getDbName());
        snapshotFile.setCommandName(CommonConstants.JOB_RESTORE);
        snapshotFile.setType(instance.getDbType());
        snapshotFile.setCreationDate(now);
        snapshotFile.setName(CommonConstants.PARAM_SNAPSHOT);
        snapshotFile.setValue(snapshot.getFileLocator());
        params.add(snapshotFile);
        //Only add PIT if the date is different form the snapshot date
        if (!formatter.format(snapshot.getCreationDate()).equals(formatter.format(pitrTime))) {
            CommandParam pitrParam = new CommandParam();
            pitrParam.setUsername(instance.getUsername());
            pitrParam.setDbName(instance.getDbName());
            pitrParam.setCommandName(CommonConstants.JOB_RESTORE);
            pitrParam.setType(instance.getDbType());
            pitrParam.setCreationDate(now);
            pitrParam.setName(CommonConstants.PARAM_PITR_TIME);
            pitrParam.setValue(formatter.format(pitrTime));
            params.add(pitrParam);
        }
        //Insert job
        int result = jobDAO.insert(job, params);
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(CommonConstants.INSTANCE_STATE_JOB_PENDING);
            Logger.getLogger(JobHelper.class.getName()).log(Level.INFO, "RESTORE JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Upgrades a database to a newer version.
     * @param instance instance to upgrade.
     * @param username requester of this job.
     * @return true if the creation of this job was successful, false otherwise.
     */
    public boolean doUpgrade (Instance instance, String username) {
        Date now = new Date();
        //Create job
        Job job = new Job();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(CommonConstants.JOB_UPGRADE);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(CommonConstants.JOB_STATE_PENDING);

        //Create params
        List<CommandParam> params = new ArrayList<>();
        
        //If the database is Oracle add version from
        if (instance.getDbType().equals(CommonConstants.DB_TYPE_ORACLE)) {
            CommandParam versionFrom = new CommandParam();
            versionFrom.setUsername(instance.getUsername());
            versionFrom.setDbName(instance.getDbName());
            versionFrom.setCommandName(CommonConstants.JOB_UPGRADE);
            versionFrom.setType(instance.getDbType());
            versionFrom.setCreationDate(now);
            versionFrom.setName(CommonConstants.PARAM_VERSION_FROM);
            versionFrom.setValue(instance.getVersion());
            params.add(versionFrom);
        }
        CommandParam versionTo = new CommandParam();
        versionTo.setUsername(instance.getUsername());
        versionTo.setDbName(instance.getDbName());
        versionTo.setCommandName(CommonConstants.JOB_UPGRADE);
        versionTo.setType(instance.getDbType());
        versionTo.setCreationDate(now);
        versionTo.setName(CommonConstants.PARAM_VERSION_TO);
        versionTo.setValue(instance.getUpgradeTo());
        params.add(versionTo);

        //Execute
        int result = jobDAO.insert(job, params);
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(CommonConstants.INSTANCE_STATE_JOB_PENDING);
            Logger.getLogger(JobHelper.class.getName()).log(Level.INFO, "UPGRADE JOB FOR REQUESTER {0} ON INSTANCE {1} SUCCESSFULLY CREATED", new Object[]{username, instance.getDbName()});
            return true;
        } else {
            return false;
        }
    }

    /**
     * Reads data from a file and parses it into a String.
     * @param media file to read from.
     * @return String with the content of the file.
     * @throws IOException
     */
    private String mediaToString(Media media) throws IOException
    {
        String text;
        if (media.isBinary()) {
            if (media.inMemory())
            {
                StringBuilder buffer = new StringBuilder();
                byte[] bytes = media.getByteData();
                for (int i = 0; i < bytes.length; i++)
                    buffer.append((char) bytes[i]);
                text = buffer.toString();
            }
            else
            {
                StringWriter writer = new StringWriter();
                IOUtils.copy(media.getStreamData(), writer);
                text = writer.toString();
            }
        }
        else {
            if (media.inMemory())
                text = media.getStringData();
            else
            {
                StringWriter writer = new StringWriter();
                IOUtils.copy(media.getReaderData(), writer);
                text = writer.toString();
            }
        }
        return text;
    }
}
