package vn.viettel.browser.kafka;

import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.Appplication;
import vn.viettel.browser.ultils.FireBaseUtils;

public class NotificationConsumerThread implements Runnable {

	private final KafkaConsumer<String, String> consumer;
	public NotificationConsumerThread()
			throws JSONException {
		this.consumer = new KafkaConsumer<>(Appplication.propConsum);
		this.consumer.subscribe(Arrays.asList(Appplication.topic));
	}

	@Override
	public void run() {
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				String key = record.value();
				try {
					FireBaseUtils.pushNotificationToSingleDevice(new JSONObject(key));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
