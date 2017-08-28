package vn.viettel.browser.kafka;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.FireBaseUtils;
import vn.viettel.browser.ultils.JSONUtils;
import vn.viettel.browser.ultils.JedisUtils;

public class NotificationConsumerThread implements Runnable {

	private final KafkaConsumer<String, String> consumer;
	private final String topic;
	private final String mess;
	private final int typeMess;
	private final ElasticsearchUtils elasticsearchUtils;
	private final JedisUtils jedisUtils;
	private final JSONObject inputSearchListDevices;

	public NotificationConsumerThread(String brokers, String groupId, String topic, String mess, int typeSend,
			JSONObject inputSearchListDevices, ElasticsearchUtils elasticsearchUtils, Properties prop, JedisUtils jedisUtils) {
		this.consumer = new KafkaConsumer<>(prop);
		this.topic = topic;
		this.consumer.subscribe(Arrays.asList(this.topic));
		this.mess = mess;
		this.typeMess = typeSend;
		this.inputSearchListDevices = inputSearchListDevices;
		this.elasticsearchUtils = elasticsearchUtils;
		this.jedisUtils = jedisUtils;
	}
	@Override
	public void run() {
		if (this.typeMess == 0) {
			int total0 = 0;
			JSONObject resultsAndroid0 = null;
			JSONObject resultsIos0 = null;
			String categoryId0 = "";
			String idJob0 = "";
			JSONObject mess0 = null;
			try {
				resultsAndroid0 = JSONUtils.createJSonForAndroidNotification(this.mess);
				resultsIos0 = JSONUtils.createJSonForIosNotification(this.mess);
				mess0 = new JSONObject(this.mess);
				idJob0 = mess0.getString("articleId");
				jedisUtils.set("done" + idJob0, 0 + "");
				categoryId0 = "categoryId:" + mess0.getString("category");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					System.out.println("Receive message: " + record.value() + ", Partition: " + record.partition()
							+ ", Offset: " + record.offset() + ", by ThreadID: " + Thread.currentThread().getId());
					String key = record.value();
					if (key.contains("ios")) {
						try {
							key.replace("ios", "");
							resultsIos0.put("to", key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsIos0);
							System.out.println("@results_ios : " + resultsIos0);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							resultsAndroid0.put("to", key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsAndroid0);
							total0++;
							jedisUtils.set("sent_total" + idJob0,
									total0 + "_" + elasticsearchUtils.getTotalDeviceByCategoryId(categoryId0, "*"));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("@results_android : " + resultsAndroid0);
					}

				}
				jedisUtils.set("done" + idJob0, 1 + "");
			}

		}

		else if (this.typeMess == 1) {
			int total1 = 0;
			JSONObject resultsAndroid1 = null;
			JSONObject resultsIos1 = null;
			String idJob1 = "";
			JSONObject mess1 = null;
			try {
				resultsAndroid1 = JSONUtils.createJSonForAndroidNotification(this.mess);
				resultsIos1 = JSONUtils.createJSonForIosNotification(this.mess);
				mess1 = new JSONObject(this.mess);
				idJob1 = mess1.getString("articleId");
				jedisUtils.set("done" + idJob1, 0 + "");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					System.out.println("Receive message to send notification to all devices: " + record.value()
							+ ", Partition: " + record.partition() + ", Offset: " + record.offset() + ", by ThreadID: "
							+ Thread.currentThread().getId());
					String key = record.value();
					if (key.contains("ios")) {
						try {
							key.replace("ios", "");
							resultsIos1.put("to", key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsIos1);
							System.out.println("@results_ios : " + resultsIos1);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							resultsAndroid1.put("to", key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsAndroid1);
							total1++;
							jedisUtils.set("sent_total" + idJob1,
									total1 + "_" + elasticsearchUtils.getTotalDevice(this.inputSearchListDevices));

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("@results_android : " + resultsAndroid1);
					}
				}
				jedisUtils.set("done" + idJob1, 1 + "");
			}
		} else if (this.typeMess == 2) {
			int total2 = 0;
			JSONObject mess2 = null;
			JSONObject results2 = new JSONObject();
			JSONObject data2 = new JSONObject();
			JSONObject notification2 = new JSONObject();
			try {
				mess2 = new JSONObject(this.mess);
				data2.put("content", mess2.getString("content"));
				data2.put("title", mess2.getString("title"));
				data2.put("type", mess2.getString("type"));
				data2.put("jobId", mess2.getString("jobId"));
				jedisUtils.set("done_box" + mess2.getString("jobId"), 0 + "");
				jedisUtils.set("sent_total_box" + mess2.getString("jobId"),
						total2 + "_" + elasticsearchUtils.getTotalDevice(this.inputSearchListDevices));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					System.out.println("Receive message: " + record.value() + ", Partition: " + record.partition()
							+ ", Offset: " + record.offset() + ", by ThreadID: " + Thread.currentThread().getId());
					String key = record.value();
					if (key.contains("ios")) {
						try {
							results2.put("data", data2);
							notification2.put("body", mess2.getString("title"));
							// notification.put("badge",0);
							notification2.put("sound", "default");
							results2.put("notification", notification2);
							results2.put("mutable_content", true);
							key.replace("ios", "");
							results2.put("to", key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(results2);
							System.out.println("@results_ios : " + results2);
							total2++;
							jedisUtils.set("sent_total_box" + mess2.getString("jobId"),
									total2 + "_" + elasticsearchUtils.getTotalDevice(this.inputSearchListDevices));
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							results2.put("data", data2);
							results2.put("to", key);
							FireBaseUtils.pushNotificationToSingleDevice(results2);
							System.out.println("@results_android : " + results2);
							total2++;
							jedisUtils.set("sent_total_box" + mess2.getString("jobId"),
									total2 + "_" + elasticsearchUtils.getTotalDevice(this.inputSearchListDevices));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				try {
					jedisUtils.set("done_box" + mess2.getString("jobId"), 1 + "");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else if (this.typeMess == 3) {
			int total3 = 0;
			ConsumerRecords<String, String> records = consumer.poll(100);
			JSONObject data3 = new JSONObject();
			JSONObject results3 = new JSONObject();
			JSONObject notification3 = new JSONObject();
			JSONObject mess3 = null;
			try {
				mess3 = new JSONObject(this.mess);
				data3.put("type", mess3.getString("type"));
				data3.put("content", mess3.getString("content"));
				data3.put("title", mess3.getString("title"));
				data3.put("jobId", mess3.getString("jobId"));
				results3.put("data", data3);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {
				for (ConsumerRecord<String, String> record : records) {
					String key = record.value();
					if (key.contains("ios")) {
						try {
							notification3.put("body", mess3.getString("title"));
							// notification.put("badge",0);
							notification3.put("sound", "default");
							results3.put("notification", notification3);
							results3.put("mutable_content", true);
							key.replace("ios", "");
							results3.put("to", key);
							System.out.println("key:ios " + key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(results3);
							System.out.println("@results_ios : " + results3);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							results3.put("to", key);
							System.out.println("key:android " + key);
							FireBaseUtils.pushNotificationToSingleDevice(results3);
							System.out.println("@results_android : " + results3);
							total3++;
							jedisUtils.set("sent_total_box" + mess3.getString("jobId"),
									total3 + "_" + elasticsearchUtils.getTotalDevice(this.inputSearchListDevices));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					try {
						jedisUtils.set("done_box" + mess3.getString("jobId"), 1 + "");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		} else if (this.typeMess == 4) {
			int total4 = 0;
			JSONObject resultsAndroid4 = null;
			JSONObject resultsIos4 = null;
			String idJob4 = "";
			JSONObject mess4 = null;
			try {
				resultsAndroid4 = JSONUtils.createJSonForAndroidNotification(this.mess);
				resultsIos4 = JSONUtils.createJSonForIosNotification(this.mess);
				mess4 = new JSONObject(this.mess);
				idJob4 = mess4.getString("articleId");
				jedisUtils.set("done" + idJob4, 0 + "");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					System.out.println("Receive message: " + record.value() + ", Partition: " + record.partition()
							+ ", Offset: " + record.offset() + ", by ThreadID: " + Thread.currentThread().getId());
					String key = record.value();
					if (key.contains("ios")) {
						try {
							key.replace("ios", "");
							resultsIos4.put("to", key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsIos4);
							System.out.println("@results_ios : " + resultsIos4);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							resultsAndroid4.put("to", key);
							FireBaseUtils.pushNotificationToSingleDevice(resultsAndroid4);
							System.out.println("@results_android : " + resultsAndroid4);
							total4++;
							jedisUtils.set("sent_total" + idJob4,
									total4 + "_" + elasticsearchUtils.getTotalDevice(this.inputSearchListDevices));
							System.out.println("@results_android : " + resultsAndroid4);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				}
				jedisUtils.set("done" + idJob4, 1 + "");
			}
		}

	}
}
