package ch.cern.dbod.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.visualization.datasource.datatable.ColumnDescription;
import com.google.visualization.datasource.datatable.DataTable;
import com.google.visualization.datasource.datatable.TableRow;
import com.google.visualization.datasource.datatable.value.DateTimeValue;
import com.google.visualization.datasource.datatable.value.NumberValue;
import com.google.visualization.datasource.datatable.value.Value;
import com.google.visualization.datasource.datatable.value.ValueType;
import com.google.visualization.datasource.render.JsonRenderer;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;
import io.searchbox.params.SearchType;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.cookie.Cookie;

/**
 * Monitoring helper to access metrics on ElasticSearch
 *
 * @author Daniel Gomez Blanco
 */
public class ESHelper {    
    /**
     * Factory to create clients
     */
    private JestClientFactory factory;
    
    /**
     * Constructor for this class.
     * @param restAddress address to access the REST API
     */
    public ESHelper(String restAddress) {        
        // Configuration (timeout is increased to allow big queries)
        HttpClientConfig clientConfig = new HttpClientConfig.Builder(restAddress).connTimeout(30000).multiThreaded(true).build();

        // Construct a new Jest client according to configuration via factory
        this.factory = new JestClientFactory();
        factory.setHttpClientConfig(clientConfig);
    }
    
    /**
     * Gets a JSON string with the object that represents all the metrics during
     * the given number of days for the top and bottom 5 entities.
     * This is calculated after the average of that metric during those days,
     * in all DBOD instances
     * @param cookie SSO cookie used to authenticate
     * @param metricGroup lemon metric group
     * @param metric specific metric inside that group
     * @param days number of days to obtain queries from
     * @return String with the JSON object representing the metrics
     */
    public String getTopBottomJSONMetrics (Cookie cookie, String metricGroup, String metric, int days) {
        //Get a client
        JestClient client = factory.getObject();
        
        //Try to make sure we shut down the client in the finall block
        try {
            //Get starting time
            long start = (new Date()).getTime() - (days * 86400000);

            //Get top and bottom 5        
            //We use facets to retun the average of the metric on that period,
            //sorted by average.
            String query = "{\n" +
                "    \"size\": 0,\n" +
                "    \"query\": {\n" +
                "        \"filtered\": {\n" +
                "            \"query\": {\n" +
                "                \"query_string\": {\n" +
                "                    \"query\": \"@fields.submitter_hostgroup: database\\\\/db_ondemand AND @fields.metric_name: " + metricGroup + "\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"filter\": {\n" +
                "                \"range\": {\"@timestamp\": {\"from\": " + start + ", \"to\": \"now\"}}\n" +
                "            }\n" +
                "        }\n" +
                "    },\n" +
                "    \"facets\": {\n" +
                "        \"metric_stats\": {\n" +
                "            \"terms_stats\": {\n" +
                "                \"key_field\": \"@fields.entity\",\n" +
                "                \"value_field\": \"@message."+ metric +"\",\n" +
                "                \"order\": \"mean\",\n" +
                "                \"size\": 0" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

            //The query is a search of count type, to avoid all the output for the documents
            //and improve performance
            Search search = new Search.Builder(query).addIndex("flume-lemon-*").setSearchType(SearchType.COUNT).setHeader("Cookie", cookie.getName() + "=" + cookie.getValue()).build();

            //Execute and get the result as a JSON object
            JsonObject result = client.execute(search).getJsonObject();
            JsonArray stats = result.getAsJsonObject("facets").getAsJsonObject("metric_stats").getAsJsonArray("terms");

            //Get first 5
            StringBuilder entities = new StringBuilder();
            for (int i = 0; i < stats.size() && i < 5; i++) {
                if (i != 0) {
                    entities.append(" OR ");
                }
                entities.append("@fields.entity: ");
                entities.append(stats.get(i).getAsJsonObject().getAsJsonPrimitive("term").getAsString());
            }
            //Get last 5
            for (int i = stats.size() - 1; i >= stats.size() - 5 && i >= 0; i--) {
                entities.append(" OR @fields.entity: ");
                entities.append(stats.get(i).getAsJsonObject().getAsJsonPrimitive("term").getAsString());
            }

            //Query to get metrics of those 10 entities
            query = "{\n" +
                "    \"size\": 10000,\n" +
                "    \"query\": {\n" +
                "        \"filtered\": {\n" +
                "            \"query\": {\n" +
                "                \"query_string\": {\n" +
                "                    \"query\": \"(" + entities + ") AND @fields.metric_name: " + metricGroup + "\"\n" +
                "                }\n" +
                "            },\n" +
                "            \"filter\": {\n" +
                "                \"range\": {\"@timestamp\": {\"from\": " + start + ", \"to\": \"now\"}}\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}";

            //We form the query adding the indexes to search for and the cookie
            search = new Search.Builder(query).addIndex("flume-lemon-*").setHeader("Cookie", cookie.getName() + "=" + cookie.getValue()).build();

            //Get the result as a JSON object, and the array of hits
            result = client.execute(search).getJsonObject();
            JsonArray hits = result.getAsJsonObject("hits").getAsJsonArray("hits");

            //Initialise the table with the date column
            DataTable table = new DataTable();
            table.addColumn(new ColumnDescription("date", ValueType.DATETIME, "Date"));

            //Add results to table (one row per value)
            //It does not matter if times are the same as they get rendered properly
            for (int i=0; i < hits.size(); i++) {
                //Get the entity
                JsonObject hit = hits.get(i).getAsJsonObject();
                String entity = hit.getAsJsonObject("_source").getAsJsonObject("@fields").get("entity").getAsString();

                //Create column if it does not exist yet
                if (!table.containsColumn(entity)) {
                    table.addColumn(new ColumnDescription(entity, ValueType.NUMBER, entity));

                    //Add one empty cell to all rows (all the past rows did not have this column)
                    Iterator<TableRow> rows = table.getRows().iterator();
                    while (rows.hasNext()) {
                        rows.next().addCell(NumberValue.getNullValue());
                    }
                }

                //Get the timestamp
                String time = hit.getAsJsonObject("_source").getAsJsonObject("@fields").get("timestamp").getAsString();
                Calendar date = new GregorianCalendar();
                date.setTimeInMillis(Long.valueOf(time));
                Value timestamp = new DateTimeValue(date.get(Calendar.YEAR),
                                                    date.get(Calendar.MONTH),
                                                    date.get(Calendar.DAY_OF_MONTH),
                                                    date.get(Calendar.HOUR_OF_DAY),
                                                    date.get(Calendar.MINUTE),
                                                    date.get(Calendar.SECOND),
                                                    date.get(Calendar.MILLISECOND));

                //Get metric
                double value = hit.getAsJsonObject("_source").getAsJsonObject("@message").get(metric).getAsDouble();

                //Create row and add timestamp
                TableRow row = new TableRow();
                row.addCell(timestamp);

                //Add value to row surrounded by nulls (remmber, only one value per row)
                for (int j=1; j < table.getNumberOfColumns(); j++) {
                    if (j == table.getColumnIndex(entity)) {
                        row.addCell(value);
                    }
                    else {
                        row.addCell(NumberValue.getNullValue());
                    }
                }

                //Add row to table
                table.addRow(row);            
            }

            return JsonRenderer.renderDataTable(table, true, false, false).toString();
        }
        catch (Exception ex) {
            Logger.getLogger(ESHelper.class.getName()).log(Level.SEVERE, "COULD NOT OBTAIN ES METRIC", ex);
        }
        finally {
            client.shutdownClient();
        }
        
        return null;
    }    
}
