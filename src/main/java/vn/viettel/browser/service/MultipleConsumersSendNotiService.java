package vn.viettel.browser.service;

import java.util.Iterator;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterators;

import vn.viettel.browser.kafka.NotificationConsumerGroup;
import vn.viettel.browser.kafka.NotificationProducerThread;
import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.HibernateUtils;
import vn.viettel.browser.ultils.JedisUtils;

@Service
public final class MultipleConsumersSendNotiService {
	private static final String brokers = "localhost:9092";
	private static final String groupId = "group05";
	private static final String topic = "cuong1";
	private static final int numberOfConsumer = 1000;
	private static final ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	private static final Properties propConsum = createConsumerConfig(brokers, groupId);
	private static final Properties propPro = createProducerConfig(brokers);
	private static final JedisUtils jeUtils = new JedisUtils();

	public String SendMess(String mess, int typeMess) throws JSONException {
		// Start Notification Producer Thread
		JSONObject messJson = new JSONObject(mess);
		JSONObject input = createInputSearch(mess);
		String categoryId = "";
		if (messJson.has("category")) {
			categoryId = messJson.getString("category");
		}
		if (typeMess == 0) {
			NotificationProducerThread producerThread = new NotificationProducerThread(topic, categoryId, typeMess,
					input, elasticsearchUtils, propPro);
			Thread t1 = new Thread(producerThread);
			t1.start();
		} else {
			NotificationProducerThread producerThread = new NotificationProducerThread(topic, typeMess, input,
					elasticsearchUtils, propPro);
			Thread t1 = new Thread(producerThread);
			t1.start();
		}

		// Start group of Notification Consumers
		NotificationConsumerGroup consumerGroup = new NotificationConsumerGroup(brokers, groupId, topic,
				numberOfConsumer, mess, typeMess, input, elasticsearchUtils, propConsum, jeUtils);

		consumerGroup.execute();

		try {
			Thread.sleep(100000);
		} catch (InterruptedException ie) {

		}
		return mess;
	}

	private static Properties createConsumerConfig(String brokers, String groupId) {
		Properties props = new Properties();
		props.put("bootstrap.servers", brokers);
		props.put("group.id", groupId);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "300000");
		props.put("request.timeout.ms", "3000000");
		props.put("auto.offset.reset", "earliest");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	private static Properties createProducerConfig(String brokers) {
		Properties props = new Properties();
		props.put("bootstrap.servers", brokers);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return props;
	}

	public static int getTotalDevicesByConfigCate(String inputSearch) throws JSONException {
		int total = 0;
		JSONObject inputSearcJson = new JSONObject(inputSearch);
		JSONObject input = new JSONObject();

		try {
			input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearcJson).get("data");
			System.out.println("input ............. " + input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		Iterator<?> keys = input.keys();
		total = Iterators.size(keys);
		return total;

	}

	public static int getTotalDevicesByConfig(String inputSearch) throws JSONException {
		int total = 0;
		JSONObject inputSearcJson = new JSONObject(inputSearch);
		JSONObject input = new JSONObject();

		try {
			input = (JSONObject) elasticsearchUtils.getListAllDevices(inputSearcJson).get("data");
			System.out.println("input ............. " + input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		Iterator<?> keys = input.keys();
		total = Iterators.size(keys);
		return total;

	}

	private static JSONObject createInputSearch(String mess) throws JSONException {
		JSONObject messJson = new JSONObject(mess);
		JSONObject input = new JSONObject();
		if (messJson.has("deviceType")) {
			if (messJson.getString("deviceType").trim().contains("android,ios")) {
				input.put("deviceType", "*");
			} else if (messJson.getString("deviceType").trim().contains("android")) {
				input.put("deviceType", "android");
			} else {
				input.put("deviceType", "ios");
			}
		}
		String appIosVersion = "";
		String appAndroidVersion = "";
		String appVersion = "";
		if (messJson.has("appIosVersion")) {
			if (!messJson.getString("appIosVersion").trim().equals("[]")) {
				appIosVersion = HibernateUtils.getVersionIosAppFromKeyInDB(messJson.getString("appIosVersion"));
			}

		}
		if (messJson.has("appAndroidVersion")) {
			if (!messJson.getString("appAndroidVersion").trim().equals("[]")) {
				appAndroidVersion = HibernateUtils.getVersionIosAppFromKeyInDB(messJson.getString("appAndroidVersion"));
			}

		}
		appVersion = appAndroidVersion + "," + appIosVersion;
		input.put("appVersion", appVersion);

		String deviceAndroidVersion = "";
		String deviceIosVersion = "";
		String deviceVersion = "";
		if (messJson.has("deviceAndroidVersion")) {
			if (!messJson.getString("deviceAndroidVersion").trim().equals("[]")) {
				deviceAndroidVersion = HibernateUtils
						.getVersionAndroidDeviceFromKeyInDB(messJson.getString("deviceAndroidVersion"));
			}
		}
		if (messJson.has("deviceIosVersion")) {
			if (!messJson.getString("deviceIosVersion").trim().equals("[]")) {
				deviceIosVersion = HibernateUtils
						.getVersionAndroidDeviceFromKeyInDB(messJson.getString("deviceIosVersion"));
			}
		}
		deviceVersion = deviceAndroidVersion + "," + deviceIosVersion;
		input.put("deviceVersion", deviceVersion);
		return input;
	}

}
