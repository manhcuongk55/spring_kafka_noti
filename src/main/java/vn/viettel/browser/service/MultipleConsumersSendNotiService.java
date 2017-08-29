package vn.viettel.browser.service;

import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.mysql.fabric.xmlrpc.base.Array;

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
		JSONObject input = new JSONObject();
		String categoryId = "";
		if (messJson.has("deviceType")) {
			input.put("deviceType", messJson.get("deviceType"));
		}
		if (messJson.has("appVersion")) {
			if (!messJson.getString("appVersion").trim().equals("[]")) {
				String appVersion = HibernateUtils.getVersionAppFromKeyInDB(messJson.getString("appVersion"));
				input.put("appVersion", appVersion);
			}

		}
		if (messJson.has("deviceVersion")) {
			if (!messJson.getString("deviceVersion").trim().equals("[]")) {
				String deviceVersion = HibernateUtils.getVersionDeviceFromKeyInDB(messJson.getString("deviceVersion"));
				input.put("deviceVersion", deviceVersion);
			}
		}
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

}
