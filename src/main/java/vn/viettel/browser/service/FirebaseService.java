package vn.viettel.browser.service;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import vn.viettel.browser.ultils.DateTimeUtils;
import vn.viettel.browser.ultils.ElasticsearchUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FirebaseService {

	private static final String FIREBASE_URL = "https://fcm.googleapis.com/fcm/send";
	private static final String FIREBASE_API_KEY = "key=AAAAYdEO07Y:APA91bHVj_2t-wSrNj6E372nSYj4YstqraLfXKrSP0Tt_AJbe2A_zY975VZ9X85c6-7pdjfZcO6XvHMrGJS4ONLV_eO00baSjNgh5uJjC2SBoBZPGhEC7LvufC3jAWvxIXIRFrSjCIsG";
	public static final String NOTIFICATION_CLICK_FUNCTION = "getArticleByNotification";

	private static final String DEVICE_NOTIFICATION_CAT_KEY = "categories";
	private static final String DEVICE_NOTIFICATION_KEY = "device_id";
	private static final String FILTER_TERM = "parameters:\"size:20,from:0\"";
	private static final String START_DATE = "2017-05-17T00:00:00";
	Settings settings = Settings.builder().put("cluster.name", "vbrowser").put("client.transport.sniff", true).build();
	TransportClient esClient = new PreBuiltTransportClient(settings);
	Jedis jedis = new Jedis("localhost");

	public FirebaseService() {
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

	public JSONObject getListDeviceIdsFromAllCategories() {
		org.json.JSONObject data = new org.json.JSONObject();
		JSONObject results = new JSONObject();
		JSONObject rows = new JSONObject();
		JSONObject metadata = new JSONObject();
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("function.keyword", "postListArticlesByCategor"))
				.must(QueryBuilders.queryStringQuery(FILTER_TERM))
				.must(QueryBuilders.rangeQuery("@timestamp").from(START_DATE));

		SearchRequestBuilder query = this.esClient.prepareSearch("browser_logging_v3").setTypes("logs")
				.setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms(DEVICE_NOTIFICATION_CAT_KEY).field("parameters.keyword")
						.size(20).subAggregation(AggregationBuilders.terms(DEVICE_NOTIFICATION_KEY)
								.field("notificationId.keyword").size(1000)));

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
					for (Iterator it = val.keys(); it.hasNext();) {
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

	public String sendNotoToListDeviceIdsByCategoryId(String message) throws JSONException {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		try {
			JSONObject input = (JSONObject) this.getListDeviceIdsFromAllCategories().get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				jedis.set("done", 0 + "");
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				JSONObject mess = new JSONObject(message);
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject notification = new JSONObject();
				String categoryId = "";
				try {
					categoryId = "categoryId:" + mess.getString("category");
					data.put("articleId", mess.getString("articleId"));
					data.put("title", mess.getString("title"));
					data.put("image", mess.getString("image"));
					results.put("data", data);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (obj.has(categoryId)) {
					if (key.contains("ios")) {
						try {
							notification.put("body", mess.getString("title"));
							// notification.put("badge",0);
							notification.put("sound", "default");
							results.put("notification", notification);
							results.put("mutable_content", true);
							key.replace("ios", "");
							results.put("to", key);
							System.out.println("key:ios " + key);
							String rp = pushNotificationToSingleDevice(results);
							ids.put("ios_" + key, rp);
							System.out.println("@results_ios : " + results);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						results.put("to", key);
						System.out.println("key:android " + key);
						String rp = pushNotificationToSingleDevice(results);
						ids.put("android_" + key, rp);
						System.out.println("@results_android : " + results);
					}
					total++;
					jedis.set("sent_total", total + "_" + getTotalDeviceByCategoryId(categoryId));
				}
			}
			jedis.set("done", 1 + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

	public int getTotalDeviceByCategoryId(String categoryId) {
		int totalDevice = 0;
		try {
			JSONObject input = (JSONObject) this.getListDeviceIdsFromAllCategories().get("data");
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
		int totalDevice = 0;
		try {
			JSONObject input = (JSONObject) this.getListDeviceIdsFromAllCategories().get("data");
			Iterator<?> keys = input.keys();
			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				totalDevice++;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return totalDevice;
	}

	public String checkStatusSending() throws JSONException {
		JSONObject reponses = new JSONObject();
		reponses.put("done", jedis.get("done"));
		reponses.put("sent_total", jedis.get("sent_total"));
		return reponses.toString();
	}

	public String sendMessBoxToListDeviceIdsByVersion(String message) throws JSONException {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		try {
			JSONObject input = (JSONObject) this.getListDeviceIdsFromAllCategories().get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				JSONObject mess = new JSONObject(message);
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject notification = new JSONObject();
				String type = "";
				String version_android = "";
				String version_ios = "";
				try {
					type = mess.getString("type");
					version_android = mess.getString("version_android");
					version_ios = mess.getString("version_ios");
					data.put("content", mess.getString("content"));
					data.put("title", mess.getString("title"));
					results.put("data", data);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (key.contains("ios")) {
					if (obj.has(version_ios)) {
						try {
							notification.put("body", mess.getString("title"));
							// notification.put("badge",0);
							notification.put("sound", "default");
							results.put("notification", notification);
							results.put("mutable_content", true);
							key.replace("ios", "");
							results.put("to", key);
							System.out.println("key:ios " + key);
							String rp = pushNotificationToSingleDevice(results);
							ids.put("ios_" + key, rp);
							System.out.println("@results_ios : " + results);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				} else {
					if (obj.has(version_android)) {
						results.put("to", key);
						System.out.println("key:android " + key);
						String rp = pushNotificationToSingleDevice(results);
						ids.put("android_" + key, rp);
						System.out.println("@results_android : " + results);
					}
				}
				total++;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

	public String sendNotiToAll(String message) throws JSONException {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		try {
			JSONObject input = (JSONObject) this.getListDeviceIdsFromAllCategories().get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				jedis.set("done", 0 + "");
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				JSONObject mess = new JSONObject(message);
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject notification = new JSONObject();
				String categoryId = "";
				try {
					categoryId = "categoryId:" + mess.getString("category");
					data.put("articleId", mess.getString("articleId"));
					data.put("title", mess.getString("title"));
					data.put("image", mess.getString("image"));
					results.put("data", data);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (key.contains("ios")) {
					try {
						notification.put("body", mess.getString("title"));
						// notification.put("badge",0);
						notification.put("sound", "default");
						results.put("notification", notification);
						results.put("mutable_content", true);
						key.replace("ios", "");
						results.put("to", key);
						System.out.println("key:ios " + key);
						String rp = pushNotificationToSingleDevice(results);
						ids.put("ios_" + key, rp);
						System.out.println("@results_ios : " + results);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					results.put("to", key);
					System.out.println("key:android " + key);
					String rp = pushNotificationToSingleDevice(results);
					ids.put("android_" + key, rp);
					System.out.println("@results_android : " + results);
				}
				total++;
				jedis.set("sent_total", total + "_" + getTotalDevice());
			}
			jedis.set("done", 1 + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

	// Hàm lấy danh sách device theo version App
	public SearchResponse getListDeviceByVersion(String device, String version) throws JSONException {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (device.equals("android") || device.equals("ios") ) {
			boolQuery.must(QueryBuilders.termsQuery("deviceType", device))
					.must(QueryBuilders.termsQuery("appVersion", version));
		} else {
			boolQuery.must(QueryBuilders.termsQuery("appVersion", version));
		}

		SearchRequestBuilder query = this.esClient.prepareSearch("browser_logging_v3").setTypes("logs")
				.setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms("devices").field("notificationId.keyword").size(10000));

		SearchResponse response = query.setSize(0).execute().actionGet();
		return response;
	}

	// Hàm tính tổng thiết bị theo version
	public SearchResponse countNumberOfDeviceByVersion(String device, String version) throws JSONException {
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		if (device.equals("android") || device.equals("ios") ) {
			boolQuery.must(QueryBuilders.termsQuery("deviceType", device))
					.must(QueryBuilders.termsQuery("appVersion", version));
		} else {
			boolQuery.must(QueryBuilders.termsQuery("appVersion", version));
		}

		SearchRequestBuilder query = this.esClient.prepareSearch("browser_logging_v3").setTypes("logs")
				.setQuery(boolQuery)
				.addAggregation(AggregationBuilders.cardinality("devices").field("notificationId.keyword"));

		SearchResponse response = query.setSize(0).execute().actionGet();
		return response;
	}

	public String sendMessBoxToAll(String message) throws JSONException {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		try {
			JSONObject input = (JSONObject) this.getListDeviceIdsFromAllCategories().get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				JSONObject mess = new JSONObject(message);
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject notification = new JSONObject();
				String type = "";
				try {
					type = mess.getString("type");
					data.put("type", mess.getString("type"));
					data.put("content", mess.getString("content"));
					data.put("title", mess.getString("title"));
					results.put("data", data);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (key.contains("ios")) {
					try {
						notification.put("body", mess.getString("title"));
						// notification.put("badge",0);
						notification.put("sound", "default");
						results.put("notification", notification);
						results.put("mutable_content", true);
						key.replace("ios", "");
						results.put("to", key);
						System.out.println("key:ios " + key);
						String rp = pushNotificationToSingleDevice(results);
						ids.put("ios_" + key, rp);
						System.out.println("@results_ios : " + results);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					results.put("to", key);
					System.out.println("key:android " + key);
					String rp = pushNotificationToSingleDevice(results);
					ids.put("android_" + key, rp);
					System.out.println("@results_android : " + results);
				}
				total++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

	public String pushNotificationToTopic(JSONObject mess) throws Exception {
		URL obj = new URL(FIREBASE_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", FIREBASE_API_KEY);
		con.setRequestProperty("Content-Type", "application/json");
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		System.out.println("@mess.toString() : " + mess.toString());
		byte[] ptext = mess.toString().getBytes("UTF8");
		wr.write(ptext);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + FIREBASE_URL);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
		return response.toString();
	}

	public String pushNotificationToSingleDevice(JSONObject mess) throws Exception {
		URL obj = new URL(FIREBASE_URL);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		// add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", FIREBASE_API_KEY);
		con.setRequestProperty("Content-Type", "application/json");
		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		System.out.println("@mess.toString() : " + mess.toString());
		byte[] ptext = mess.toString().getBytes("UTF8");
		wr.write(ptext);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + FIREBASE_URL);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
		return response.toString();
	}

	public String getTotalNotificationClicks(String from, String to, String device) {
		if (from.equals("")) {
			from = DateTimeUtils.getPreviousDate(7);
		}
		if (to.equals("")) {
			to = DateTimeUtils.getTimeNow();
		}

		JSONObject result = new JSONObject();
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
				.must(QueryBuilders.termsQuery("function.keyword", NOTIFICATION_CLICK_FUNCTION))
				.must(QueryBuilders.rangeQuery("@timestamp").from(from).to(to));

		if (device.equals("ios")) {
			boolQuery.must(QueryBuilders.wildcardQuery("notificationId.keyword", "ios*"));
		} else if (device.equals("android")) {
			boolQuery.mustNot(QueryBuilders.wildcardQuery("notificationId.keyword", "ios*"));
		}
		SearchRequestBuilder query = this.esClient.prepareSearch("browser_logging_v3").setTypes("logs")
				.setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms("top_devices").field("notificationId.keyword"));
		SearchResponse response = query.setSize(10).execute().actionGet();
		return response.toString();
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
			SearchRequestBuilder query = this.esClient.prepareSearch("br_article_v4").setTypes("article")
					.setQuery(boolQuery).addAggregation(AggregationBuilders.terms("hot_tags").field("tags").size(1)
							.subAggregation(AggregationBuilders.topHits("top_article_of_tags").size(1)));
			response = query.setSize(0).execute().actionGet();
		}
		return response;
	}

	// Function lấy bài hot trong vòng 1h gần đây
	public SearchResponse getHotArticleRecently() {
		SearchResponse response = new SearchResponse();
		DateTime dateFrom = DateTimeUtils.getPreviousTime("hour", 1);
		if (dateFrom != null) {
			long from = DateTimeUtils.convertDateTimeToUnixTimestamp(dateFrom);

			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("display", "1"))
					.must(QueryBuilders.rangeQuery("time_post").from(from / 1000));
			SearchRequestBuilder query = this.esClient.prepareSearch("br_article_v4").setTypes("article")
					.setQuery(boolQuery).addAggregation(AggregationBuilders.terms("hot_tags").field("tags").size(1)
							.subAggregation(AggregationBuilders.topHits("top_article_of_tags").size(1)));
			response = query.setSize(0).execute().actionGet();
		}
		return response;
	}

	public JSONObject getListDeviceIdsByCategoryId(String id, String from, String size) {
		JSONObject results = new JSONObject();
		JSONObject metadata = new JSONObject();
		ArrayList<String> data = new ArrayList<>();
		String categoryId = "categoryId:" + id;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		try {
			JSONObject input = (JSONObject) this.getListDeviceIdsFromAllCategories().get("data");
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

		JSONObject results = new JSONObject();
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
				.filter(QueryBuilders.termsQuery("function.keyword", NOTIFICATION_CLICK_FUNCTION))
				.filter(QueryBuilders.rangeQuery("@timestamp").from(from).to(to));

		SearchRequestBuilder query = this.esClient.prepareSearch("browser_logging_v3").setTypes("logs")
				.setQuery(boolQuery).addAggregation(AggregationBuilders.dateHistogram("notifications_clicked_by_day")
						.field("@timestamp").dateHistogramInterval(DateHistogramInterval.DAY));

		SearchResponse response = query.setSize(Integer.parseInt(size)).execute().actionGet();
		return response.toString();
	}

	public boolean checkDuplicateMessVsKey(String key) {
		if (jedis.exists(key)) {
			return true;
		} else {
			jedis.set(key, "1");
			jedis.expire(key, 24 * 3600);
			return false;
		}
	}
}
