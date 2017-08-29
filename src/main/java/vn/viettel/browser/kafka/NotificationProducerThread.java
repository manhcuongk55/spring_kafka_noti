package vn.viettel.browser.kafka;

import java.util.Iterator;
import java.util.Properties;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.config.ProductionConfig;
import vn.viettel.browser.ultils.ElasticsearchUtils;

public class NotificationProducerThread implements Runnable {

	private final KafkaProducer<String, String> producer;
	private final String topic;
	JSONObject inputSearch = new JSONObject();

	private ElasticsearchUtils elasticsearchUtils;

	String categoryId;
	int typeSendMess;
	JSONObject input;

	public NotificationProducerThread(String topic, String categoryId, int typeSendMess, JSONObject input,
			ElasticsearchUtils elasticsearchUtils, Properties prop) {
		this.producer = new KafkaProducer<String, String>(prop);
		this.topic = topic;
		this.categoryId = categoryId;
		this.typeSendMess = typeSendMess;
		this.input = input;
		this.elasticsearchUtils = elasticsearchUtils;
	}

	public NotificationProducerThread(String topic, int typeSendMess, JSONObject input,
			ElasticsearchUtils elasticsearchUtils, Properties prop) {
		this.producer = new KafkaProducer<String, String>(prop);
		this.topic = topic;
		this.typeSendMess = typeSendMess;
		this.input = input;
		this.elasticsearchUtils = elasticsearchUtils;
	}

	@Override
	public void run() {
		switch (typeSendMess) {
		case 0:
			try {
				getNotiToListDeviceByCategoryId(input, categoryId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 1:
			try {
				getAllDevicesToSendNotification(input);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 2:
			try {
				getListDeviceToSendMessByConfig(input);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 3:
			try {
				getAllDeviceToSendMessByConfig(input);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case 4:
			try {
				sendNotiToDeviceTest(ProductionConfig.TestFireBase);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		default:
			break;
		}

	}

	public void getNotiToListDeviceByCategoryId(JSONObject input, String categoryId) throws JSONException {
		try {
			input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearch).get("data");
			System.out.println("input ............. " + input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Iterator<?> keys = input.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			System.out.println("key ............. " + key);
			JSONObject obj = null;
			try {
				obj = (JSONObject) input.get(key);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String categoryId0 = "categoryId:" + categoryId;

			if (obj.has(categoryId0)) {
				producer.send(new ProducerRecord<String, String>(topic, key), new Callback() {
					public void onCompletion(RecordMetadata metadata, Exception e) {
						if (e != null) {
							e.printStackTrace();
						}
						System.out.println("Sent:" + key + ", Partition: " + metadata.partition() + ", Offset: "
								+ metadata.offset());
					}
				});
			}

		}
		// closes producer
		producer.close();
	}

	public void getAllDevicesToSendNotification(JSONObject inputSearch) throws Exception {
		JSONObject input = null;
		try {
			input = (JSONObject) elasticsearchUtils.getListAllDevices(inputSearch).get("data");
			System.out.println("input ............. " + input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Iterator<?> keys = input.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			System.out.println("key ............. " + key);
			producer.send(new ProducerRecord<String, String>(topic, key), new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null) {
						e.printStackTrace();
					}
					System.out.println(
							"Sent:" + key + ", Partition: " + metadata.partition() + ", Offset: " + metadata.offset());
				}
			});

		}
		// closes producer
		producer.close();

	}

	public void sendNotiToDeviceTest(String[] listDevices) throws Exception {
		for (int i = 0; i < listDevices.length; i++) {
			String key = listDevices[i];
			producer.send(new ProducerRecord<String, String>(topic, listDevices[i]), new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null) {
						e.printStackTrace();
					}
					System.out.println(
							"Sent:" + key + ", Partition: " + metadata.partition() + ", Offset: " + metadata.offset());
				}
			});

		}
		producer.close();

	}

	public void getListDeviceToSendMessByConfig(JSONObject inputSearch) {
		System.out.println("NotificationProducerThread...getListDeviceToSendMessByConfig");
		JSONObject input = null;
		try {
			input = (JSONObject) elasticsearchUtils.getListAllDevices(inputSearch).get("data");
			System.out.println("input ............. " + input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Iterator<?> keys = input.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			System.out.println("key ............. " + key);
			producer.send(new ProducerRecord<String, String>(topic, key), new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null) {
						e.printStackTrace();
					}
					System.out.println(
							"Sent:" + key + ", Partition: " + metadata.partition() + ", Offset: " + metadata.offset());
				}
			});

		}
		// closes producer
		producer.close();
	}

	public void getAllDeviceToSendMessByConfig(JSONObject inputSearch) {
		System.out.println("NotificationProducerThread...getAllDeviceToSendMessByConfig");
		JSONObject input = null;
		try {
			input = (JSONObject) elasticsearchUtils.getListAllDevices(inputSearch).get("data");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Iterator<?> keys = input.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			System.out.println("key ............. " + key);
			producer.send(new ProducerRecord<String, String>(topic, key), new Callback() {
				public void onCompletion(RecordMetadata metadata, Exception e) {
					if (e != null) {
						e.printStackTrace();
					}
					System.out.println(
							"Sent:" + key + ", Partition: " + metadata.partition() + ", Offset: " + metadata.offset());
				}
			});

		}
		// closes producer
		producer.close();
	}

}
