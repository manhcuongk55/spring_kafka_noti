package vn.viettel.browser.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.json.JSONObject;

import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.JedisUtils;

public final class NotificationConsumerGroup {
	private List<NotificationConsumerThread> consumers;

	public NotificationConsumerGroup(String brokers, String groupId, String topic, int numberOfConsumers,
			String messInput, int typeMess, JSONObject input, ElasticsearchUtils elasticsearchUtils, Properties prop,
			JedisUtils jedisUtils) {
		consumers = new ArrayList<>();
		for (int i = 0; i < numberOfConsumers; i++) {
			NotificationConsumerThread ncThread = new NotificationConsumerThread(brokers, groupId, topic, messInput,
					typeMess, input, elasticsearchUtils, prop, jedisUtils);
			consumers.add(ncThread);
		}
	}

	public void execute() {
		for (NotificationConsumerThread ncThread : consumers) {
			Thread t = new Thread(ncThread);
			t.start();
		}
	}
}
