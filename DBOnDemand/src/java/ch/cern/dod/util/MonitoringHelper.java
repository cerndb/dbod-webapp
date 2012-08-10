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
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

/**
 * Helper to obtain monitoring data.
 * @author Daniel Gomez Blanco
 */
public class MonitoringHelper {

    /**
     * Gets the available metrics.
     * @param  dbType type of DB to obtain metrics for
     * @return List of metrics (value, description) available to obtain.
     * @throws MalformedURLException if the URL is malformed.
     * @throws IOException if there is a problem reading the response.
     */
    public List<DODMetric> getAvailableMetrics(String dbType) throws MalformedURLException, IOException {
        List<DODMetric> metrics = new ArrayList<DODMetric>();
        String url = "";
        //Get metrics for DB
        if (dbType.equals(DODConstants.DB_TYPE_MYSQL)) {
            url = DODConstants.MONITORING_URL + "&" + DODConstants.MONITORING_TYPE + "=" + DODConstants.MONITORING_TYPE_MYSQL;
        }
        else if (dbType.equals(DODConstants.DB_TYPE_ORACLE)) {
            url = DODConstants.MONITORING_URL + "&" + DODConstants.MONITORING_TYPE + "=" + DODConstants.MONITORING_TYPE_MYSQL;
        }
        URL plotUrl = new URL(url);
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
            if (dbType.equals(DODConstants.DB_TYPE_MYSQL))
                metric.setType(DODConstants.MONITORING_TYPE_MYSQL);
            metrics.add(metric);
        }
        in.close();
        
        //Get metrics fro machine
        plotUrl = new URL(DODConstants.MONITORING_URL + "&" + DODConstants.MONITORING_TYPE + "=" + DODConstants.MONITORING_TYPE_MACHINE);
        plotConnection = plotUrl.openConnection();
        in = new BufferedReader(
                                new InputStreamReader(
                                plotConnection.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            DODMetric metric = new DODMetric();
            StringTokenizer tokens = new StringTokenizer(inputLine, ",");
            metric.setId(tokens.nextToken());
            metric.setDescription(tokens.nextToken());
            metric.setType(DODConstants.MONITORING_TYPE_MACHINE);
            metrics.add(metric);
        }
        in.close();
        
        //Sort array
        Collections.sort(metrics);
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
     * @param host host of the instance.
     * @param metric metric to obtain.
     * @return JSON array.
     * @throws IOException if there is an error processing the response.
     */
    public String getJSONMetric (DODInstance instance, String host, DODMetric metric, int days) throws IOException {
        //HTTP call to RACMon
        String url = DODConstants.MONITORING_URL + "&" + DODConstants.MONITORING_TYPE + "=" + metric.getType() + "&" + DODConstants.MONITORING_DAYS + "=" + days
                            + "&" + DODConstants.MONITORING_METRIC + "=" + metric.getId();
        if (metric.getType().equals(DODConstants.MONITORING_TYPE_MACHINE)) {
            url += "&" + DODConstants.MONITORING_INSTANCE + "=" + host;
        }
        else {
            url += "&" + DODConstants.MONITORING_INSTANCE + "=" + DODConstants.PREFIX_INSTANCE_NAME + instance.getDbName();
        }
        URL plotUrl = new URL(url);
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
