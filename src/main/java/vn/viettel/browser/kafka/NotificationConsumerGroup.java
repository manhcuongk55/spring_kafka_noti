package vn.viettel.browser.kafka;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;

import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.Application;
import vn.viettel.browser.service.MultipleConsumersSendNotiService;
import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.JedisUtils;

public final class NotificationConsumerGroup {
	private List<NotificationConsumerThread> consumers;

	public NotificationConsumerGroup() throws JSONException {
		consumers = new ArrayList<>();
		for (int i = 0; i < Application.numberOfConsumer; i++) {
			NotificationConsumerThread ncThread = new NotificationConsumerThread();
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
