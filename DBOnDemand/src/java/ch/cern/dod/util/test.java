/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODSnapshot;
import java.rmi.RemoteException;
import java.util.List;
//import javax.xml.rpc.ServiceException;

/**
 *
 * @author dgomezbl
 */
public class test {

    public static void main(String[] args) throws RemoteException {
        testGetParam();
//        testEgroups();
//        testAuthentication();
    }

    private static void testGetParam() {
            DODInstance instance = new DODInstance();
            instance.setDbName("testinstance");
            String param = "host";
            ParamsHelper paramsHelper = new ParamsHelper("", "");
            String value = paramsHelper.getParam(instance, param);
            System.out.println(value.toString());
    }
    
    private static void testGetFile() {
            DODInstance instance = new DODInstance();
            instance.setDbName("testinstance");
            FileHelper fileHelper = new FileHelper("dgomezbl", "AK3422dgb@cern");
            String[] value = fileHelper.getSlowLogs(instance);
            System.out.println(value.toString());
    }
    
    private static void testEgroups() {
        EGroupHelper helper = new EGroupHelper("", "");
        System.out.println(helper.eGroupExists("-"));
    }
    
    private static void testAuthentication() {
        AuthenticationHelper helper = new AuthenticationHelper("", "");
        System.out.println(helper.getUserInfo("dgomezbl").getDepartment());
    }
}
