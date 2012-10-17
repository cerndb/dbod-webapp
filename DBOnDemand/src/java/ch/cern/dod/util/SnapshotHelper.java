package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODSnapshot;
import ch.cern.dod.ws.DODWebService;
import ch.cern.dod.ws.DODWebServicePortType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Helper to manage snapshots using web services.
 * @author Daniel Gomez Blanco
 * @version 26/09/2011
 */
public class SnapshotHelper {

    /**
     * Username to connect to web services.
     */
    private String wsUser;
    /**
     * Password to connect to web services.
     */
    private String wsPassword;

    /**
     * Constructor for this class.
     * @param user username to connect to web services
     * @param password password to connect to web services
     */
    public SnapshotHelper(String user, String password) {
        this.wsUser = user;
        this.wsPassword = password;
    }

    /**
     * Gets the snapshots for a specific instance.
     * @param instance instance to get the snapshots of.
     * @return list of snapshots.
     */
    public List<DODSnapshot> getSnapshots(DODInstance instance) {
        ArrayList<DODSnapshot> snapshots = new ArrayList<DODSnapshot>();
        try {
            DODWebService service = new DODWebService();
            DODWebServicePortType port = service.getDODWebServicePort();
            String snapshotsString = port.getSnapshots(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            String[] snapshotArray = snapshotsString.split(":");
            Pattern pattern = Pattern.compile("_");
            for (int i=0; i<snapshotArray.length;i++) {
                if (snapshotArray[i] != null && !snapshotArray[i].isEmpty()) {
                    snapshots.add(getSnapshotFromString(snapshotArray[i], pattern));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, "ERROR OBTAINING SNAPSHOTS FOR INSTANCE " + instance.getDbName(), ex.getMessage());
        }
        return snapshots;
    }

    /**
     * Creates a snapshot object from a string with the file locator.
     * @param s String to be parsed.
     * @param pattern Pattern to be applied to separate fields.
     * @return Snapshot object
     */
    private DODSnapshot getSnapshotFromString (String s, Pattern pattern) {
        DODSnapshot snapshot = new DODSnapshot();
        //File locator, is the string itself
        snapshot.setFileLocator(s);

        //Split string and get the second and third items (date and time)
        String[] items = pattern.split(s);
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Integer.parseInt(items[1].substring(4, 8)), Integer.parseInt(items[1].substring(2, 4)) - 1, Integer.parseInt(items[1].substring(0, 2)),
                        Integer.parseInt(items[2].substring(0, 2)), Integer.parseInt(items[2].substring(2, 4)), Integer.parseInt(items[2].substring(4, 6)));
        snapshot.setCreationDate(calendar.getTime());

        return snapshot;
    }
}
