package vn.viettel.browser.kafka;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import vn.viettel.browser.Application;

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
