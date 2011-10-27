package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODSnapshot;
import ch.cern.dod.ws.DODWebServiceLocator;
import ch.cern.dod.ws.DODWebServiceSoapBindingStub;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.xml.rpc.ServiceException;

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
     * @throws ServiceException if there is an error executing the web service.
     * @throws RemoteException if there is an error connection to the server.
     */
    public List<DODSnapshot> getSnapshots(DODInstance instance) {
        ArrayList<DODSnapshot> snapshots = new ArrayList<DODSnapshot>();
        try {
            DODWebServiceLocator locator = new DODWebServiceLocator();
            DODWebServiceSoapBindingStub stub = (DODWebServiceSoapBindingStub) locator.getDODWebServicePort();
            stub.setUsername(wsUser);
            stub.setPassword(wsPassword);
            String snapshotsString = stub.getSnapshots(DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName());
            String[] snapshotArray = snapshotsString.split(":");
            Pattern pattern = Pattern.compile("_");
            for (int i=0; i<snapshotArray.length;i++) {
                if (snapshotArray[i] != null && !snapshotArray[i].isEmpty())
                    snapshots.add(getSnapshotFromString(snapshotArray[i], pattern));
            }
        } catch (RemoteException ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
        } catch (ServiceException ex) {
            Logger.getLogger(SnapshotHelper.class.getName()).log(Level.SEVERE, ex.getMessage());
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
