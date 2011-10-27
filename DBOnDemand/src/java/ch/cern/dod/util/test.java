/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.ws.DODWebServiceLocator;
import ch.cern.dod.ws.DODWebServiceSoapBindingStub;
import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;
import org.apache.axis.wsdl.WSDL2Java;
import org.zkoss.util.media.AMedia;

/**
 *
 * @author dgomezbl
 */
public class test {

    public static void main(String[] args) throws ServiceException, RemoteException {
//        String[] wsdl = new String[]{"/home/dgomezbl/Documents/DBOnDemand/dodwebservices.wsdl"};
//        WSDL2Java.main(wsdl);
//        testGetFile();
        testGetParam();
    }

    private static void testGetFile() {
            DODInstance instance = new DODInstance();
            instance.setDbName("testinstance");
           String filePath = "/etc/shadow";
            ConfigFileHelper fileHelper = new ConfigFileHelper("dodws", "Sup3rN0va11");
            AMedia file = fileHelper.getFile(instance, filePath);
    }

    private static void testGetParam() {
            DODInstance instance = new DODInstance();
            instance.setDbName("ignacio");
            String param = "host";
            ParamsHelper paramsHelper = new ParamsHelper("dodws", "Sup3rN0va11");
            String value = paramsHelper.getParam(instance, param);
            System.out.println(value);
    }
}
