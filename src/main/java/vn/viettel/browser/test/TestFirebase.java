package vn.viettel.browser.test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.ultils.ElasticsearchUtils;

/**
 * Created by giang on 29/05/2017.
 */
public class TestFirebase {
    private static final String DEVICE_NOTIFICATION_CAT_KEY = "categories";
    private static final String DEVICE_NOTIFICATION_KEY = "device_id";
    private static final String FILTER_TERM = "parameters:\"size:20,from:0\"";
    private static final String START_DATE = "2017-05-17T00:00:00";
    static Settings settings = Settings.builder().put("cluster.name", "vbrowser")
            .put("client.transport.sniff", true).build();
    static TransportClient esClient = new PreBuiltTransportClient(settings);


    public static void main(String[] args) {
        System.out.println("Hello Cuong");
        try {
            sendNotoToListDeviceIdsByCategoryId("4");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public TestFirebase() {
        System.out.println("FUC");
        try {
            this.esClient.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.107.233"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject getListDeviceIdsFromAllCategories() {
        org.json.JSONObject data    = new org.json.JSONObject();
        JSONObject results          = new JSONObject();
        JSONObject rows             = new JSONObject();
        JSONObject metadata         = new JSONObject();
        DateFormat dateFormat       = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.termsQuery("function.keyword","postListArticlesByCategor"))
                .must(QueryBuilders.queryStringQuery(FILTER_TERM))
                .must(QueryBuilders.rangeQuery("@timestamp").from(START_DATE));

        SearchRequestBuilder query = esClient.prepareSearch("browser_logging_v3")
                .setTypes("logs").setQuery(boolQuery)
                .addAggregation(AggregationBuilders.terms(DEVICE_NOTIFICATION_CAT_KEY).field("parameters.keyword").size(20)
                        .subAggregation(AggregationBuilders.terms(DEVICE_NOTIFICATION_KEY).field("notificationId.keyword").size(1000)));

        SearchResponse response = query.setSize(0).execute().actionGet();

        try {
            data = ElasticsearchUtils.convertEsResultAggrsToArray
                    (response,DEVICE_NOTIFICATION_CAT_KEY,DEVICE_NOTIFICATION_KEY);
            Iterator<?> keys = data.keys();

            /* Process to get max categoryId of each firebase Id */
            while( keys.hasNext() ) {
                String k = (String)keys.next();
                JSONArray arr = (JSONArray) data.get(k);
                long maxValue = 0;
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject val = (JSONObject) arr.get(i);
                    for (Iterator it = val.keys(); it.hasNext(); ) {
                        String keyName = (String) it.next();
                        if (val.getLong(keyName) > maxValue) {
                            JSONObject obj = new JSONObject();
                            obj.put(keyName,val.getLong(keyName));
                            rows.put(k,obj);
                            maxValue = val.getLong(keyName);
                        }
                    }
                }
            }

            metadata.put("date", dateFormat.format(date));
            metadata.put("total", rows.length());
            results.put("metadata",metadata);
            results.put("data",rows);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static String sendNotoToListDeviceIdsByCategoryId(String category) throws JSONException {
        String categoryId = category;
        try {
            JSONObject input = (JSONObject) getListDeviceIdsFromAllCategories().get("data");
            int total = 0;
            Iterator<?> keys = input.keys();

            /* Process to group firebase Id to category */
            while (keys.hasNext()){
                String key = (String) keys.next();
                JSONObject obj = (JSONObject) input.get(key);
                if (obj.has(categoryId)) {
                    System.out.println(key);
                    total++;
                }
            }
            System.out.println("@total : "+ total);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }


}
