package vn.viettel.browser.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import vn.viettel.browser.ultils.DateTimeUtils;
import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.FireBaseUtils;
import vn.viettel.browser.ultils.JedisUtils;

@Service
public class NotificationService {

	public static final String NOTIFICATION_CLICK_FUNCTION = "getArticleByNotification";
	public static final String LOGGING_INDEX = "browser_logging_v3";
	ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	public String sendNotoToListDeviceByCategoryId(String message) throws JSONException {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		try {
			JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories("*").get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				JedisUtils.set("done", 0 + "");
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
							String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
							ids.put("ios_" + key, rp);
							System.out.println("@results_ios : " + results);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						results.put("to", key);
						System.out.println("key:android " + key);
						String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
						ids.put("android_" + key, rp);
						System.out.println("@results_android : " + results);
					}
					total++;
					JedisUtils.set("sent_total", total + "_" + elasticsearchUtils.getTotalDeviceByCategoryId(categoryId,"*"));
				}
			}
			JedisUtils.set("done", 1 + "");
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
			JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories("*").get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				JedisUtils.set("done", 0 + "");
				String key = (String) keys.next();
				JSONObject mess = new JSONObject(message);
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject notification = new JSONObject();
				try {
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
						String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
						ids.put("ios_" + key, rp);
						System.out.println("@results_ios : " + results);
					} catch (JSONException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				} else {
					results.put("to", key);
					System.out.println("key:android " + key);
					String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
					ids.put("android_" + key, rp);
					System.out.println("@results_android : " + results);
				}
				total++;
				JedisUtils.set("sent_total", total + "_" + elasticsearchUtils.getTotalDevice());
			}
			JedisUtils.set("done", 1 + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
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
		SearchRequestBuilder query = elasticsearchUtils.esClient.prepareSearch(LOGGING_INDEX).setTypes("logs")
				.setQuery(boolQuery)
				.addAggregation(AggregationBuilders.terms("top_devices").field("notificationId.keyword"));
		SearchResponse response = query.setSize(10).execute().actionGet();
		return response.toString();
	}

	// Function lấy bài hot trong vòng 1h gần đây
	public SearchResponse getHotArticleRecently() {
		SearchResponse response = new SearchResponse();
		DateTime dateFrom = DateTimeUtils.getPreviousTime("hour", 1);
		if (dateFrom != null) {
			long from = DateTimeUtils.convertDateTimeToUnixTimestamp(dateFrom);

			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("display", "1"))
					.must(QueryBuilders.rangeQuery("time_post").from(from / 1000));
			SearchRequestBuilder query = elasticsearchUtils.esClient.prepareSearch("br_article_v4").setTypes("article")
					.setQuery(boolQuery).addAggregation(AggregationBuilders.terms("hot_tags").field("tags").size(1)
							.subAggregation(AggregationBuilders.topHits("top_article_of_tags").size(1)));
			response = query.setSize(0).execute().actionGet();
		}
		return response;
	}

	public JSONObject getListDeviceIdsByCategoryId(String id, String from, String size, String device) {
		JSONObject results = new JSONObject();
		JSONObject metadata = new JSONObject();
		ArrayList<String> data = new ArrayList<>();
		String categoryId = "categoryId:" + id;
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		try {
			JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories(device).get("data");
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

		SearchRequestBuilder query = elasticsearchUtils.esClient.prepareSearch(LOGGING_INDEX).setTypes("logs")
				.setQuery(boolQuery).addAggregation(AggregationBuilders.dateHistogram("notifications_clicked_by_day")
						.field("@timestamp").dateHistogramInterval(DateHistogramInterval.DAY));

		SearchResponse response = query.setSize(Integer.parseInt(size)).execute().actionGet();
		return response.toString();
	}
}
