package ch.cern.dod.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Provides network utilities
 * @author Daniel Gomez Blanco
 */
public class NetworkHelper {
    
    /**
     * Checks if an IP address belongs to a subnet.
     * @param ip IP to check
     * @param subnet subnet to check
     * @param mask mask to apply
     * @return true if it belongs to the given subnet, false otherwise
     */
    public static boolean isIpInSubnet (String ip, String subnet, String mask) {
        InetAddress ipAddress = null;
        InetAddress subnetAddress = null;
        InetAddress maskAddress = null;
        //Instantiate addresses
        try {
            ipAddress = InetAddress.getByName(ip);
            subnetAddress = InetAddress.getByName(subnet);
            maskAddress = InetAddress.getByName(mask);
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetworkHelper.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        //Check if the IP belongs to the subnet
        if (ipAddress != null && subnetAddress != null && maskAddress != null) {
            for (int i=0; i < 4; i++) {
                if ((ipAddress.getAddress()[i] & maskAddress.getAddress()[i])
                        != (subnetAddress.getAddress()[i] & maskAddress.getAddress()[i]))
                    return false;
            }
        }
        
        return true;
    }
    
    /**
     * Checks if an IP address belongs to a subnet at CERN.
     * @param ip IP to check
     * @return true if it belongs to the given subnet, false otherwise
     */
    public static boolean isCernIp (String ip) {
        //Get file
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/afs/cern.ch/project/jps/reps/DBOnDemand/cern_subnets.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NetworkHelper.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        //Check ip in subnets
        if (reader != null) {
            try {
                String line = null;
                while ((line = reader.readLine()) != null) {
                  String[] subnet = line.split(" ");
                  if (subnet.length == 2) {
                      if (isIpInSubnet(ip, subnet[0], subnet[1])) {
                          return true;
                      }
                  }
                }
            } catch (IOException ex) {
                Logger.getLogger(NetworkHelper.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        
        return false;
    }
}
