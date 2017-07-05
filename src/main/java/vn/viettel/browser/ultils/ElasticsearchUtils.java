package vn.viettel.browser.ultils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import antlr.collections.List;

/**
 * Created by giang on 03/04/2017.
 */
public class ElasticsearchUtils {
	public static final String NOTIFICATION_CLICK_FUNCTION = "getArticleByNotification";
	private static final String DEVICE_NOTIFICATION_CAT_KEY = "categories";
	private static final String LOGGING_INDEX = "browser_logging_v3";
	private static final String DEVICE_NOTIFICATION_KEY = "device_id";
	private static final String FILTER_TERM = "parameters:\"size:20,from:0\"";
	private static final String START_DATE = "2017-05-17T00:00:00";
	static Settings settings = Settings.builder().put("cluster.name", "vbrowser").put("client.transport.sniff", true)
			.build();
	public TransportClient esClient = new PreBuiltTransportClient(settings);

	public ElasticsearchUtils() {
		try {
			this.esClient
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.107.233"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.107.232"), 9300))
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("192.168.107.234"), 9300))
					.addTransportAddress(
							new InetSocketTransportAddress(InetAddress.getByName("192.168.107.235"), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public JSONObject getListDeviceIdsFromAllCategories(String device) {
		org.json.JSONObject data = new org.json.JSONObject();
		JSONObject results = new JSONObject();
		JSONObject rows = new JSONObject();
		JSONObject metadata = new JSONObject();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (device.equals("*")) {
			boolQuery.must(QueryBuilders.termsQuery("function.keyword", "postListArticlesByCategor"))
					.must(QueryBuilders.queryStringQuery(FILTER_TERM))
					.must(QueryBuilders.rangeQuery("@timestamp").from(START_DATE));
		} else {
			boolQuery.must(QueryBuilders.termsQuery("function.keyword", "postListArticlesByCategor"))
					.must(QueryBuilders.queryStringQuery(FILTER_TERM))
					.must(QueryBuilders.termQuery("deviceType", device))
					.must(QueryBuilders.rangeQuery("@timestamp").from(START_DATE));
		}

		SearchRequestBuilder query = esClient.prepareSearch(LOGGING_INDEX).setTypes("logs").setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms(DEVICE_NOTIFICATION_CAT_KEY).field("parameters.keyword")
						.size(20).subAggregation(AggregationBuilders.terms(DEVICE_NOTIFICATION_KEY)
								.field("notificationId.keyword").size(1000000)));

		SearchResponse response = query.setSize(0).execute().actionGet();

		try {
			data = ElasticsearchUtils.convertEsResultAggrsToArray(response, DEVICE_NOTIFICATION_CAT_KEY,
					DEVICE_NOTIFICATION_KEY);
			Iterator<?> keys = data.keys();

			/* Process to get max categoryId of each firebase Id */
			while (keys.hasNext()) {
				String k = (String) keys.next();
				JSONArray arr = (JSONArray) data.get(k);
				long maxValue = 0;
				for (int i = 0; i < arr.length(); i++) {
					JSONObject val = (JSONObject) arr.get(i);
					for (Iterator<?> it = val.keys(); it.hasNext();) {
						String keyName = (String) it.next();
						if (val.getLong(keyName) > maxValue) {
							JSONObject obj = new JSONObject();
							obj.put(keyName, val.getLong(keyName));
							rows.put(k, obj);
							maxValue = val.getLong(keyName);
						}
					}
				}
			}

			metadata.put("date", dateFormat.format(date));
			metadata.put("total", rows.length());
			results.put("metadata", metadata);
			results.put("data", rows);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return results;
	}

	public static String convertEsResultToString(SearchResponse response) throws JSONException {
		Gson gson = new Gson();
		ArrayList<Object> results = new ArrayList<Object>();
		SearchHit[] hits = response.getHits().getHits();
		for (int i = 0; i < hits.length; i++) {
			JSONObject obj = new JSONObject();
			obj.put("_source", hits[i].getSource());
			obj.put("_id", hits[i].getId());
			results.add(obj);
		}

		return gson.toJson(results);
	}

	public static String convertEsResultAggrsToString(SearchResponse response, String key) throws JSONException {
		Gson gson = new Gson();
		ArrayList<Object> results = new ArrayList<Object>();
		Terms terms = response.getAggregations().get(key);
		Collection<Terms.Bucket> buckets = terms.getBuckets();
		for (Terms.Bucket bucket : buckets) {
			JSONObject obj = new JSONObject();
			obj.put(bucket.getKeyAsString(), bucket.getDocCount());
			results.add(obj);
		}
		return gson.toJson(results);
	}

	public static org.json.JSONObject convertEsResultAggrsToArray(SearchResponse response, String key, String subKey)
			throws JSONException {
		Terms agg = response.getAggregations().get(key);
		org.json.JSONObject result = new org.json.JSONObject();
		Collection<Terms.Bucket> buckets = agg.getBuckets();

		for (Terms.Bucket bucket : buckets) {
			if (bucket.getDocCount() != 0) {
				Terms terms = bucket.getAggregations().get(subKey);
				Collection<Terms.Bucket> bkts = terms.getBuckets();
				for (Terms.Bucket b : bkts) {
					if (b.getDocCount() != 0 && !b.getKeyAsString().equals("undefined")) {
						org.json.JSONObject obj = new org.json.JSONObject();
						String categoryName = bucket.getKeyAsString().split(",")[2].replace("}", "").replace("\"", "");
						if (!categoryName.equals("categoryId:1")) {
							obj.put(categoryName, b.getDocCount());
						}
						result.append(b.getKeyAsString(), obj);
					}
				}
			}
		}
		return result;
	}

	// Function lấy bài hot trong vòng 1h gần đây
	public SearchResponse getHotArticleRecentlyByCategory(int categoryID) {
		SearchResponse response = new SearchResponse();
		DateTime dateFrom = DateTimeUtils.getPreviousTime("hour", 1);
		if (dateFrom != null) {
			long from = DateTimeUtils.convertDateTimeToUnixTimestamp(dateFrom);

			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("display", "1"))
					.must(QueryBuilders.termQuery("category.id", categoryID))
					.must(QueryBuilders.rangeQuery("time_post").from(from / 1000));
			SearchRequestBuilder query = esClient.prepareSearch("br_article_v4").setTypes("article").setQuery(boolQuery)
					.addAggregation(AggregationBuilders.terms("hot_tags").field("tags").size(1)
							.subAggregation(AggregationBuilders.topHits("top_article_of_tags").size(1)));
			response = query.setSize(0).execute().actionGet();
		}
		return response;
	}

	// Function lấy bài hot trong vòng 1h gần đây
	public JSONObject getHotArticleRecently() throws JSONException {
		SearchResponse response = new SearchResponse();
		JSONObject results1 = null;
		JSONObject mess = null;
		DateTime dateFrom = DateTimeUtils.getPreviousTime("hour", 1);
		if (dateFrom != null) {
			long from = DateTimeUtils.convertDateTimeToUnixTimestamp(dateFrom);

			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("display", "1"))
					.must(QueryBuilders.rangeQuery("time_post").from(from / 1000));
			SearchRequestBuilder query = esClient.prepareSearch("br_article_v4").setTypes("article").setQuery(boolQuery)
					.addAggregation(AggregationBuilders.terms("hot_tags").field("tags").size(1)
							.subAggregation(AggregationBuilders.topHits("top_article_of_tags").size(1)));
			response = query.setSize(0).execute().actionGet();
			JSONObject response1 = new JSONObject(response.toString());
			JSONObject aggregations = (JSONObject) response1.get("aggregations");
			JSONObject results = (JSONObject) aggregations.get("hot_tags");
			JSONArray list = results.getJSONArray("buckets");
			results1 = (JSONObject) list.get(0);
			results1 = (JSONObject) results1.get("top_article_of_tags");
			results1 = (JSONObject) results1.get("hits");
			results1 = (JSONObject) ((JSONArray) results1.get("hits")).get(0);
			results1 = (JSONObject) results1.get("_source");
			mess = new JSONObject();
			mess.put("category", ((JSONObject) results1.get("category")).get("id"));
			mess.put("title", results1.getJSONArray("title").get(0));
			mess.put("articleId", results1.get("id"));
			mess.put("image", results1.getJSONArray("images").get(0));
		}
		return mess;
	}

	// Hàm lấy danh sách device theo version App
	public JSONArray getListDeviceByVersion(String device, String version) throws JSONException {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (device.equals("android") || device.equals("ios")) {
			boolQuery.must(QueryBuilders.termsQuery("deviceType", device))
					.must(QueryBuilders.termsQuery("appVersion", version));
		} else {
			boolQuery.must(QueryBuilders.termsQuery("appVersion", version));
		}

		SearchRequestBuilder query = esClient.prepareSearch(LOGGING_INDEX).setTypes("logs").setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms("devices").field("notificationId.keyword").size(10000));

		SearchResponse response = query.setSize(0).execute().actionGet();
		JSONObject response1 = new JSONObject(response.toString());
		JSONObject aggregations = (JSONObject) response1.get("aggregations");
		JSONObject results = (JSONObject) aggregations.get("devices");
		JSONArray listDevices = results.getJSONArray("buckets");
		return listDevices;
	}

	// Hàm tính tổng thiết bị theo version
	public SearchResponse countNumberOfDeviceByVersion(String device, String version) throws JSONException {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (device.equals("android") || device.equals("ios")) {
			boolQuery.must(QueryBuilders.termsQuery("deviceType", device))
					.must(QueryBuilders.termsQuery("appVersion", version));
		} else {
			boolQuery.must(QueryBuilders.termsQuery("appVersion", version));
		}

		SearchRequestBuilder query = esClient.prepareSearch(LOGGING_INDEX).setTypes("logs").setQuery(boolQuery)
				.addAggregation(AggregationBuilders.cardinality("devices").field("notificationId.keyword"));

		SearchResponse response = query.setSize(0).execute().actionGet();
		return response;
	}

	public int getTotalDeviceByCategoryId(String categoryId, String device) {
		int totalDevice = 0;
		try {
			JSONObject input = (JSONObject) getListDeviceIdsFromAllCategories(device).get("data");
			Iterator<?> keys = input.keys();
			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				if (obj.has(categoryId)) {
					totalDevice++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalDevice;
	}

	public int getTotalDevice() {
		int count = 0;
		try {
			JSONObject input = (JSONObject) getListDeviceIdsFromAllCategories("*");
			JSONObject metadata = input.getJSONObject("metadata");
			if (metadata != null && metadata.get("total") != null) {
				count = metadata.getInt("total");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public JSONObject getListDeviceIdsByCategoryId(String id, String from, String size, String device) {
		JSONObject results = new JSONObject();
		JSONObject metadata = new JSONObject();
		ArrayList<String> data = new ArrayList<>();
		String categoryId = "categoryId:" + id;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		try {
			JSONObject input = (JSONObject) getListDeviceIdsFromAllCategories(device).get("data");
			int total = 0;
			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				if (obj.has(categoryId)) {
					data.add(key);
					total++;
				}
			}
			ArrayList<String> res = data.stream().skip(Integer.parseInt(from)).limit(Integer.parseInt(size))
					.collect(Collectors.toCollection(ArrayList::new));

			metadata.put("date", dateFormat.format(date));
			metadata.put("name", categoryId);
			metadata.put("total", total);
			metadata.put("size", Integer.parseInt(size));
			metadata.put("from", Integer.parseInt(from));
			results.put("data", res);
			results.put("metadata", metadata);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	public String getNumberOfNotificationsClickedByTime(String from, String to, String size) {
		if (from.equals("")) {
			from = DateTimeUtils.getPreviousDate(30);
		}
		if (to.equals("")) {
			to = DateTimeUtils.getTimeNow();
		}
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
				.filter(QueryBuilders.termsQuery("function.keyword", NOTIFICATION_CLICK_FUNCTION))
				.filter(QueryBuilders.rangeQuery("@timestamp").from(from).to(to));

		SearchRequestBuilder query = esClient.prepareSearch("browser_logging_v3").setTypes("logs").setQuery(boolQuery)
				.addAggregation(AggregationBuilders.dateHistogram("notifications_clicked_by_day").field("@timestamp")
						.dateHistogramInterval(DateHistogramInterval.DAY));

		SearchResponse response = query.setSize(Integer.parseInt(size)).execute().actionGet();
		return response.toString();
	}

	public String getTotalNotificationClicks(String from, String to, String device) {
		if (from.equals("")) {
			from = DateTimeUtils.getPreviousDate(7);
		}
		if (to.equals("")) {
			to = DateTimeUtils.getTimeNow();
		}

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("function.keyword", NOTIFICATION_CLICK_FUNCTION))
				.must(QueryBuilders.rangeQuery("@timestamp").from(from).to(to));

		if (device.equals("ios")) {
			boolQuery.must(QueryBuilders.wildcardQuery("notificationId.keyword", "ios*"));
		} else if (device.equals("android")) {
			boolQuery.mustNot(QueryBuilders.wildcardQuery("notificationId.keyword", "ios*"));
		}
		SearchRequestBuilder query = esClient.prepareSearch("browser_logging_v3").setTypes("logs").setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms("top_devices").field("notificationId.keyword"));
		SearchResponse response = query.setSize(10).execute().actionGet();
		return response.toString();
	}
}
