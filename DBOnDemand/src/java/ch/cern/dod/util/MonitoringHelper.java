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
import org.zkoss.json.JSONArray;

/**
 * Helper to obtain monitoring data.
 * @author Daniel Gomez Blanco
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
     * @deprecated Metrics are obtained as raw data and rendered with Google Visualization API
     */
    public RenderedImage getMetric (DODInstance instance, String metric) throws IOException {
        URL plotUrl = new URL(DODConstants.MONITORING_URL + "&" + DODConstants.MONITORING_INSTANCE + "=" + DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName() + "&"
                            + DODConstants.MONITORING_METRIC + "=" + metric);
        RenderedImage image = ImageIO.read(plotUrl);
        return image;
    }
    
    /**
     * Gets the values for the selected metric in a JSON array.
     * @param instance instance to obtain the metric for.
     * @param metric metric to obtain.
     * @return JSON array.
     * @throws IOException if there is an error processing the response.
     */
    public String getJSONMetric (String instance, String metric, int days) throws IOException {
        //HTTP call to RACMon
        URL plotUrl = new URL(DODConstants.MONITORING_URL + "&" + DODConstants.MONITORING_DAYS + "=" + days
                            + "&" + DODConstants.MONITORING_INSTANCE + "=" + DODConstants.PREFIX_INSTANCE_NAME + instance + "&"
                            + DODConstants.MONITORING_METRIC + "=" + metric);
        URLConnection plotConnection = plotUrl.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                plotConnection.getInputStream()));
        String inputLine;
        StringBuilder toret = new StringBuilder();
        //If there is data to read
        if ((inputLine = in.readLine()) != null) {
            //Create the columns and get the first row
            toret.append("{cols: [{id: 'date', label: 'Date', type: 'datetime'}, {id: 'value', label: 'Cumulative', type: 'number'}, {id: 'value', label: 'Delta', type: 'number'}], rows: [");
            String[] values = inputLine.split(",");
            long lastDate = Long.parseLong(values[0]) * 1000;
            float lastValue = Float.parseFloat(values[1]);
            toret.append("{c: [{v: new Date(");
            toret.append(Long.parseLong(values[0]) * 1000);
            toret.append(")}, {v: ");
            toret.append(lastValue);
            toret.append("}, {v: 0}]}");

            //For every row
            while ((inputLine = in.readLine()) != null) {
                values = inputLine.split(",");
                long currentDate = Long.parseLong(values[0]) * 1000;
                float currentValue = Float.parseFloat(values[1]);
                //If more than 7 minutes passed without changes, add a delta 0 6 minutes before for plotting
                if (lastDate + 420000 < currentDate) {
                    toret.append(", {c: [{v: new Date(");
                    toret.append(lastDate + 360000);
                    toret.append(")}, {}, {v:0}]}");
                }
                if (currentDate - 420000 > lastDate) {
                    toret.append(", {c: [{v: new Date(");
                    toret.append(currentDate - 360000);
                    toret.append(")}, {}, {v:0}]}");
                }
                //Append the accumulative value
                toret.append(", {c: [{v: new Date(");
                toret.append(currentDate);
                toret.append(")}, {v: ");
                toret.append(currentValue);
                //If last time and current time are the same then append delta
                if (currentDate == lastDate) {
                    toret.append("}, {v: ");
                    toret.append(currentValue - lastValue);
                }
                toret.append("}]}");
                //Update last value and last date
                lastValue = currentValue;
                lastDate = currentDate;
            }
            //Append a delta 0 at the end of the graph to close the line
            toret.append(", {c: [{v: new Date(");
            toret.append(lastDate);
            toret.append(")}, {}, {v:0}]}");
            toret.append("]}");
        }
        in.close();

        //If there is data return the JSON object, if not the return null
        if (toret.length() > 0)
            return toret.toString();
        else
            return "null";
    }
}
