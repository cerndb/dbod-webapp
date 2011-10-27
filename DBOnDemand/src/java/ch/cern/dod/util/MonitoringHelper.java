package ch.cern.dod.util;

import ch.cern.dod.db.entity.DODInstance;
import ch.cern.dod.db.entity.DODMetric;
import java.awt.image.RenderedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

/**
 * Helper to obtain monitoring data.
 * @author Daniel Gomez Blanco
 * @version 23/09/2011
 */
public class MonitoringHelper {

    /**
     * Gets the available metrics.
     * @return List of metrics (value, description) available to obtain.
     * @throws MalformedURLException if the URL is malformed.
     * @throws IOException if there is a problem reading the response.
     */
    public List<DODMetric> getAvailableMetrics() throws MalformedURLException, IOException {
        List<DODMetric> metrics = new ArrayList<DODMetric>();
        URL plotUrl = new URL(DODConstants.MONITORING_URL);
        URLConnection plotConnection = plotUrl.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                plotConnection.getInputStream()));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            DODMetric metric = new DODMetric();
            StringTokenizer tokens = new StringTokenizer(inputLine, ",");
            metric.setId(tokens.nextToken());
            metric.setDescription(tokens.nextToken());
            metrics.add(metric);
        }
        in.close();
        return metrics;
    }

    /**
     * Creates an image with the contents of the response.
     * @param instance instance to obtain the metric for.
     * @param metric metric to obtain.
     * @return image with the specified metric.
     * @throws IOException if there is an error processing the response.
     */
    public RenderedImage getMetric (DODInstance instance, String metric) throws IOException {
//        URL plotUrl = new URL(DODConstants.MONITORING_URL + "&" + DODConstants.MONITORING_INSTANCE + "=" + DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName() + "&"
//                            + DODConstants.MONITORING_METRIC + "=" + metric);
        URL plotUrl = new URL(DODConstants.MONITORING_URL + "&" + DODConstants.MONITORING_INSTANCE + "=" + "provasql" + "&"
                            + DODConstants.MONITORING_METRIC + "=" + metric);
        RenderedImage image = ImageIO.read(plotUrl);
        return image;
    }
}
