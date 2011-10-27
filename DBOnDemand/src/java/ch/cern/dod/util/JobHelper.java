package ch.cern.dod.util;

import ch.cern.dod.db.dao.DODInstanceDAO;
import ch.cern.dod.db.dao.DODJobDAO;
import ch.cern.dod.db.entity.DODCommandParam;
import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODJob;
import ch.cern.dod.db.entity.DODSnapshot;
import ch.cern.dod.exception.ConfigFileSizeException;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.UploadEvent;

/**
 * Helper to create jobs.
 * @author Daniel Gomez Blanco
 * @version 27/09/2011
 */
public class JobHelper {

    /**
     * Indicates if the user performing the action is in admin mode
     */
    private boolean adminMode;

    /**
     * DAO to manage jobs.
     */
    DODJobDAO jobDAO;

    /**
     * DAO to manage instances
     */
    DODInstanceDAO instanceDAO;

    /**
     * Constructor for this class.
     * @param adminMode indicates if the user performing the action is in admin mode
     */
    public JobHelper(boolean adminMode) {
        this.adminMode = adminMode;
        jobDAO = new DODJobDAO();
        instanceDAO = new DODInstanceDAO();
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
    public boolean doStartup(DODInstance instance, String username) {
        Date now = new Date();
        //Create job
        DODJob job = new DODJob();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(DODConstants.JOB_STARTUP);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(DODConstants.JOB_STATE_PENDING);

        //Create param
        List<DODCommandParam> params = new ArrayList<DODCommandParam>();
        DODCommandParam instanceName = new DODCommandParam();
        instanceName.setUsername(instance.getUsername());
        instanceName.setDbName(instance.getDbName());
        instanceName.setCommandName(DODConstants.JOB_STARTUP);
        instanceName.setType(instance.getDbType());
        instanceName.setCreationDate(now);
        instanceName.setName(DODConstants.PARAM_INSTANCE_NAME);
        instanceName.setValue(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
        params.add(instanceName);

        //Execute
        int result = jobDAO.insert(job, params);
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(DODConstants.INSTANCE_STATE_JOB_PENDING);
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
    public boolean doShutdown(DODInstance instance, String username) {
        Date now = new Date();
        //Create job
        DODJob job = new DODJob();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(DODConstants.JOB_SHUTDOWN);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(DODConstants.JOB_STATE_PENDING);

        //Create param
        List<DODCommandParam> params = new ArrayList<DODCommandParam>();
        DODCommandParam instanceName = new DODCommandParam();
        instanceName.setUsername(instance.getUsername());
        instanceName.setDbName(instance.getDbName());
        instanceName.setCommandName(DODConstants.JOB_SHUTDOWN);
        instanceName.setType(instance.getDbType());
        instanceName.setCreationDate(now);
        instanceName.setName(DODConstants.PARAM_INSTANCE_NAME);
        instanceName.setValue(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
        params.add(instanceName);

        //Execute
        int result = jobDAO.insert(job, params);
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(DODConstants.INSTANCE_STATE_JOB_PENDING);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Uploads a config file and creates the corresponding job to copy the file to the intance.
     * @param instance instance to copy the config file to.
     * @param username requester of this job.
     * @param fileType type of config file to upload.
     * @param filePath path of config file to upload.
     * @param event upload event to get the data from.
     * @return true if the creation of this job was successful, false otherwise.
     * @throws ConfigFileSizeException if the file to be uploaded is too big.
     * @throws UnsupportedEncodingException if the enconding is not supported.
     * @throws IOException if there is an error reading the file.
     */
    public boolean doUpload(DODInstance instance, String username, String fileType, String filePath, UploadEvent event) throws ConfigFileSizeException, UnsupportedEncodingException, IOException {
        //Get config file
        Media media = event.getMedia();
        if (media != null) {
            String data = mediaToString(media);
            if (data.getBytes("UTF-8").length / 1024 <= DODConstants.MAX_SIZE_CONFIG_FILE) {
                Date now = new Date();
                //Insert job
                DODJob job = new DODJob();
                job.setUsername(instance.getUsername());
                job.setDbName(instance.getDbName());
                job.setCommandName(DODConstants.JOB_UPLOAD);
                job.setType(instance.getDbType());
                job.setCreationDate(now);
                job.setRequester(username);
                if (adminMode)
                    job.setAdminAction(1);
                else
                    job.setAdminAction(0);
                job.setState(DODConstants.JOB_STATE_PENDING);

                //Create params
                List<DODCommandParam> params = new ArrayList<DODCommandParam>();
                DODCommandParam instanceName = new DODCommandParam();
                instanceName.setUsername(instance.getUsername());
                instanceName.setDbName(instance.getDbName());
                instanceName.setCommandName(DODConstants.JOB_UPLOAD);
                instanceName.setType(instance.getDbType());
                instanceName.setCreationDate(now);
                instanceName.setName(DODConstants.PARAM_INSTANCE_NAME);
                instanceName.setValue(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
                params.add(instanceName);
                DODCommandParam configFile = new DODCommandParam();
                configFile.setUsername(instance.getUsername());
                configFile.setDbName(instance.getDbName());
                configFile.setCommandName(DODConstants.JOB_UPLOAD);
                configFile.setType(instance.getDbType());
                configFile.setCreationDate(now);
                configFile.setName(DODConstants.PARAM_CONFIG_FILE + "=" + fileType);
                configFile.setValue(data);
                params.add(configFile);
                DODCommandParam path = new DODCommandParam();
                path.setUsername(instance.getUsername());
                path.setDbName(instance.getDbName());
                path.setCommandName(DODConstants.JOB_UPLOAD);
                path.setType(instance.getDbType());
                path.setCreationDate(now);
                path.setName(DODConstants.PARAM_CONFIG_PATH);
                path.setValue(filePath);
                params.add(path);


                int result = jobDAO.insert(job, params);
                //If everything went OK update instance object
                if (result > 0) {
                    instance.setState(DODConstants.INSTANCE_STATE_JOB_PENDING);
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
     * @param hours interval (in hours) between automatic backups. 0 if no automatic backups should be done.
     * @return true if the creation of this job was successful, false otherwise.
     */
    public boolean doBackup(DODInstance instance, String username, int hours) {
        Date now = new Date();
        //Create job
        DODJob job = new DODJob();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(DODConstants.JOB_BACKUP);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(DODConstants.JOB_STATE_PENDING);
        //Create param
        List<DODCommandParam> params = new ArrayList<DODCommandParam>();
        DODCommandParam instanceName = new DODCommandParam();
        instanceName.setUsername(instance.getUsername());
        instanceName.setDbName(instance.getDbName());
        instanceName.setCommandName(DODConstants.JOB_BACKUP);
        instanceName.setType(instance.getDbType());
        instanceName.setCreationDate(now);
        instanceName.setName(DODConstants.PARAM_INSTANCE_NAME);
        instanceName.setValue(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
        params.add(instanceName);
        int result = jobDAO.insertAndCreateScheduledBackup(job, hours, params);
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(DODConstants.INSTANCE_STATE_JOB_PENDING);
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
     * @return true if the creation of this job was successful, false otherwise.
     */
    public boolean doRestore(DODInstance instance, String username, DODSnapshot snapshot) {
        Date now = new Date();
        //Create job
        DODJob job = new DODJob();
        job.setUsername(instance.getUsername());
        job.setDbName(instance.getDbName());
        job.setCommandName(DODConstants.JOB_RESTORE);
        job.setType(instance.getDbType());
        job.setCreationDate(now);
        job.setRequester(username);
        if (adminMode)
            job.setAdminAction(1);
        else
            job.setAdminAction(0);
        job.setState(DODConstants.JOB_STATE_PENDING);
        //Create param
        List<DODCommandParam> params = new ArrayList<DODCommandParam>();
        DODCommandParam instanceName = new DODCommandParam();
        instanceName.setUsername(instance.getUsername());
        instanceName.setDbName(instance.getDbName());
        instanceName.setCommandName(DODConstants.JOB_RESTORE);
        instanceName.setType(instance.getDbType());
        instanceName.setCreationDate(now);
        instanceName.setName(DODConstants.PARAM_INSTANCE_NAME);
        instanceName.setValue(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
        params.add(instanceName);
        DODCommandParam snapshotFile = new DODCommandParam();
        snapshotFile.setUsername(instance.getUsername());
        snapshotFile.setDbName(instance.getDbName());
        snapshotFile.setCommandName(DODConstants.JOB_RESTORE);
        snapshotFile.setType(instance.getDbType());
        snapshotFile.setCreationDate(now);
        snapshotFile.setName(DODConstants.PARAM_SNAPSHOT_FILE);
        snapshotFile.setValue(snapshot.getFileLocator());
        params.add(snapshotFile);
        //Insert job
        int result = jobDAO.insert(job, params);
        //If everything went OK update instance object
        if (result > 0) {
            instance.setState(DODConstants.INSTANCE_STATE_JOB_PENDING);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Destroy an instance.
     * @param instance instance to restore the backup from.
     * @param username requester of this job.
     * @return true if the creation of this job was successful, false otherwise.
     */
    public boolean doDestroy(DODInstance instance, String username) {
        Date now = new Date();
        //Insert job
        int result = instanceDAO.delete(instance);
        //If everything went OK update instance object
        if (result > 0) {
            instance.setStatus(false);
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
        String text = "";
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
