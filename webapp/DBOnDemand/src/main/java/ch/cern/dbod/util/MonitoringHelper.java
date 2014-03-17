package ch.cern.dbod.util;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Get;

/**
 * Monitoring helper to access metrics on ElasticSearch
 *
 * @author Daniel Gomez Blanco
 */
public class MonitoringHelper {

    public static void main(String[] args) throws Exception {
        System.setProperty("jsse.enableSNIExtension", "false");
        
        XTrustProvider.install();

        // Configuration
        HttpClientConfig clientConfig = new HttpClientConfig.Builder("https://dashboards.cern.ch/public").multiThreaded(true).build();

        // Construct a new Jest client according to configuration via factory
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(clientConfig);
        JestClient client = factory.getObject();

        Get get = new Get.Builder("flume-lemon-2014-03-11", "QLk2VSYxRUunKaDADR6sbA").type("log").build();

        JestResult result = client.execute(get);
        
        System.out.println(result.getJsonString());
    }
}
