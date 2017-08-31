package vn.viettel.browser.service;

import java.util.Iterator;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.google.common.collect.Iterators;

import vn.viettel.browser.Appplication;
import vn.viettel.browser.config.ProductionConfig;
import vn.viettel.browser.ultils.HibernateUtils;
import vn.viettel.browser.ultils.JSONUtils;

@Service
public final class MultipleConsumersSendNotiService {
	int totalCount = 0;
	private JSONObject resultsAndroid = new JSONObject();
	private JSONObject resultsIos = new JSONObject();
	private String idJob = "";
	private JSONObject messJson = new JSONObject();

	JSONObject results = new JSONObject();
	JSONObject data = new JSONObject();
	JSONObject notification = new JSONObject();

	public String SendMess(String mess, int typeMess) throws Exception {
		// Start Notification Producer Thread
		JSONObject messJson = new JSONObject(mess);
		JSONObject inputSearchListDevices = createInputSearch(mess);
		String categoryId = "";
		if (messJson.has("category")) {
			categoryId = messJson.getString("category");
		}
		if (typeMess == 0) {
			resultsAndroid = JSONUtils.createJSonForAndroidNotification(mess);
			resultsIos = JSONUtils.createJSonForIosNotification(mess);
			messJson = new JSONObject(mess);
			idJob = messJson.getString("articleId");
			Appplication.jedisUtils.set("sent" + idJob, 0 + "");
			categoryId = "categoryId:" + messJson.getString("category");
			totalCount = Appplication.elasticsearchUtils.getTotalDeviceByCategoryId(inputSearchListDevices, categoryId);
			Appplication.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
			getNotiToListDeviceByCategoryId(inputSearchListDevices, categoryId,idJob,totalCount,typeMess);
		} else if (typeMess == 1) {
			resultsAndroid = JSONUtils.createJSonForAndroidNotification(mess);
			resultsIos = JSONUtils.createJSonForIosNotification(mess);
			messJson = new JSONObject(mess);
			idJob = messJson.getString("articleId");
			Appplication.jedisUtils.set("sent" + idJob, 0 + "");
			totalCount = Appplication.elasticsearchUtils.getTotalDevice(inputSearchListDevices);
			Appplication.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
			getAllDevicesToSendNotification(inputSearchListDevices,idJob,totalCount,typeMess);
		} else if (typeMess == 2) {
			totalCount = Appplication.elasticsearchUtils.getTotalDevice(inputSearchListDevices);
			messJson = new JSONObject(mess);
			data.put("content", messJson.getString("content"));
			data.put("title", messJson.getString("title"));
			data.put("type", messJson.getString("type"));
			idJob = messJson.getString("jobId");
			data.put("jobId", idJob);
			Appplication.jedisUtils.set("sent_total_box" + idJob, 0 + "_" + totalCount);
			Appplication.jedisUtils.set("sent_box" + idJob, 0 + "");
			getListDeviceToSendMessByConfig(inputSearchListDevices,idJob,totalCount,typeMess);
		} else if (typeMess == 3) {
			totalCount = Appplication.elasticsearchUtils.getTotalDevice(inputSearchListDevices);
			messJson = new JSONObject(mess);
			data.put("content", messJson.getString("content"));
			data.put("title", messJson.getString("title"));
			data.put("type", messJson.getString("type"));
			idJob = messJson.getString("jobId");
			data.put("jobId", idJob);
			Appplication.jedisUtils.set("sent_total_box" + idJob, 0 + "_" + totalCount);
			Appplication.jedisUtils.set("sent_box" + idJob, 0 + "");
			getAllDeviceToSendMessByConfig(inputSearchListDevices,idJob,totalCount,typeMess);
		} else if (typeMess == 4) {
			resultsAndroid = JSONUtils.createJSonForAndroidNotification(mess);
			resultsIos = JSONUtils.createJSonForIosNotification(mess);
			messJson = new JSONObject(mess);
			idJob = messJson.getString("articleId");
			Appplication.jedisUtils.set("sent" + idJob, 0 + "");
			totalCount = ProductionConfig.TestFireBase.length;
			Appplication.jedisUtils.set("sent_total" + idJob, 0 + "_" + totalCount);
			sendNotiToDeviceTest(ProductionConfig.TestFireBase,idJob,totalCount,typeMess);
		}
		return mess;
	}

	public void getNotiToListDeviceByCategoryId(JSONObject inputSearch, String categoryId, String idJob, int totalCount,
			int typeMess) throws JSONException {
		JSONObject input = null;
		try {
			input = (JSONObject) Appplication.elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearch)
					.get("data");
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
				if (key.contains("ios")) {
					key.replace("ios", "");
					resultsIos.put("to", key);
					resultsIos.put("idJob", idJob);
					resultsIos.put("totalCount", totalCount);
					resultsIos.put("type", typeMess);
					Appplication.producer.send(
							new ProducerRecord<String, String>(Appplication.topic, resultsIos.toString()),
							new Callback() {
								public void onCompletion(RecordMetadata metadata, Exception e) {
									if (e != null) {
										e.printStackTrace();
									}
									System.out.println("Sent:" + resultsIos + ", Partition: " + metadata.partition()
											+ ", Offset: " + metadata.offset());
								}
							});
				} else {
					resultsAndroid.put("to", key);
					resultsAndroid.put("idJob", idJob);
					resultsAndroid.put("totalCount", totalCount);
					resultsAndroid.put("type", typeMess);
					Appplication.producer.send(
							new ProducerRecord<String, String>(Appplication.topic, resultsAndroid.toString()),
							new Callback() {
								public void onCompletion(RecordMetadata metadata, Exception e) {
									if (e != null) {
										e.printStackTrace();
									}
									System.out.println("Sent:" + resultsAndroid + ", Partition: " + metadata.partition()
											+ ", Offset: " + metadata.offset());
								}
							});
				}

			}

		}
		// closes producer
		Appplication.producer.close();
	}

	public void getAllDevicesToSendNotification(JSONObject inputSearch, String idJob, int totalCount, int typeMess)
			throws Exception {
		JSONObject input = null;
		try {
			input = (JSONObject) Appplication.elasticsearchUtils.getListAllDevices(inputSearch).get("data");
			System.out.println("input ............. " + input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Iterator<?> keys = input.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.contains("ios")) {
				key.replace("ios", "");
				resultsIos.put("to", key);
				resultsIos.put("idJob", idJob);
				resultsIos.put("totalCount", totalCount);
				resultsIos.put("type", typeMess);
				Appplication.producer.send(
						new ProducerRecord<String, String>(Appplication.topic, resultsIos.toString()), new Callback() {
							public void onCompletion(RecordMetadata metadata, Exception e) {
								if (e != null) {
									e.printStackTrace();
								}
								System.out.println("Sent:" + resultsIos + ", Partition: " + metadata.partition()
										+ ", Offset: " + metadata.offset());
							}
						});
			} else {
				resultsAndroid.put("to", key);
				resultsAndroid.put("idJob", idJob);
				resultsAndroid.put("totalCount", totalCount);
				resultsAndroid.put("type", typeMess);
				Appplication.producer.send(
						new ProducerRecord<String, String>(Appplication.topic, resultsAndroid.toString()),
						new Callback() {
							public void onCompletion(RecordMetadata metadata, Exception e) {
								if (e != null) {
									e.printStackTrace();
								}
								System.out.println("Sent:" + resultsAndroid + ", Partition: " + metadata.partition()
										+ ", Offset: " + metadata.offset());
							}
						});
			}

		}
		// closes producer
		Appplication.producer.close();

	}

	public void sendNotiToDeviceTest(String[] listDevices, String idJob, int totalCount, int typeMess)
			throws Exception {
		for (int i = 0; i < listDevices.length; i++) {
			String key = listDevices[i];
			if (key.contains("ios")) {
				key.replace("ios", "");
				resultsIos.put("to", key);
				resultsIos.put("idJob", idJob);
				resultsIos.put("totalCount", totalCount);
				resultsIos.put("type", typeMess);
				Appplication.producer.send(
						new ProducerRecord<String, String>(Appplication.topic, resultsIos.toString()), new Callback() {
							public void onCompletion(RecordMetadata metadata, Exception e) {
								if (e != null) {
									e.printStackTrace();
								}
								System.out.println("Sent:" + resultsIos + ", Partition: " + metadata.partition()
										+ ", Offset: " + metadata.offset());
							}
						});
			} else {
				resultsAndroid.put("to", key);
				resultsAndroid.put("idJob", idJob);
				resultsAndroid.put("totalCount", totalCount);
				resultsAndroid.put("type", typeMess);
				Appplication.producer.send(
						new ProducerRecord<String, String>(Appplication.topic, resultsAndroid.toString()),
						new Callback() {
							public void onCompletion(RecordMetadata metadata, Exception e) {
								if (e != null) {
									e.printStackTrace();
								}
								System.out.println("Sent:" + resultsAndroid + ", Partition: " + metadata.partition()
										+ ", Offset: " + metadata.offset());
							}
						});
			}

		}
		Appplication.producer.close();

	}

	public void getListDeviceToSendMessByConfig(JSONObject inputSearch, String idJob, int totalCount, int typeMess)
			throws JSONException {
		JSONObject input = null;
		try {
			input = (JSONObject) Appplication.elasticsearchUtils.getListAllDevices(inputSearch).get("data");
			System.out.println("input ............. " + input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Iterator<?> keys = input.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.contains("ios")) {
				key.replace("ios", "");
				results.put("data", data);
				notification.put("body", messJson.getString("title"));
				// notification.put("badge",0);
				notification.put("sound", "default");
				results.put("notification", notification);
				results.put("mutable_content", true);
				key.replace("ios", "");
				results.put("to", key);
			} else {
				results.put("data", data);
				results.put("to", key);
			}
			results.put("idJob", idJob);
			results.put("totalCount", totalCount);
			results.put("type", typeMess);
			Appplication.producer.send(new ProducerRecord<String, String>(Appplication.topic, results.toString()),
					new Callback() {
						public void onCompletion(RecordMetadata metadata, Exception e) {
							if (e != null) {
								e.printStackTrace();
							}
							System.out.println("Sent:" + results + ", Partition: " + metadata.partition() + ", Offset: "
									+ metadata.offset());
						}
					});
		}
		// closes producer
		Appplication.producer.close();
	}

	public void getAllDeviceToSendMessByConfig(JSONObject inputSearch, String idJob, int totalCount, int typeMess)
			throws JSONException {
		JSONObject input = null;
		try {
			input = (JSONObject) Appplication.elasticsearchUtils.getListAllDevices(inputSearch).get("data");
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Iterator<?> keys = input.keys();

		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (key.contains("ios")) {
				key.replace("ios", "");
				results.put("data", data);
				notification.put("body", messJson.getString("title"));
				// notification.put("badge",0);
				notification.put("sound", "default");
				results.put("notification", notification);
				results.put("mutable_content", true);
				key.replace("ios", "");
				results.put("to", key);
			} else {
				results.put("data", data);
				results.put("to", key);
			}
			results.put("idJob", idJob);
			results.put("totalCount", totalCount);
			results.put("type", typeMess);
			Appplication.producer.send(new ProducerRecord<String, String>(Appplication.topic, results.toString()),
					new Callback() {
						public void onCompletion(RecordMetadata metadata, Exception e) {
							if (e != null) {
								e.printStackTrace();
							}
							System.out.println("Sent:" + results + ", Partition: " + metadata.partition() + ", Offset: "
									+ metadata.offset());
						}
					});

		}
		// closes producer
		Appplication.producer.close();
	}

	public static int getTotalDevicesByConfigCate(JSONObject inputSearch) throws JSONException {
		int total = 0;
		JSONObject input = new JSONObject();

		try {
			input = (JSONObject) Appplication.elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearch)
					.get("data");
			System.out.println("input ............. " + input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		Iterator<?> keys = input.keys();
		total = Iterators.size(keys);
		return total;

	}

	public static int getTotalDevicesByConfig(JSONObject inputSearch) throws JSONException {
		int total = 0;
		JSONObject input = new JSONObject();

		try {
			input = (JSONObject) Appplication.elasticsearchUtils.getListAllDevices(inputSearch).get("data");
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
				appAndroidVersion = HibernateUtils
						.getVersionAndroidAppFromKeyInDB(messJson.getString("appAndroidVersion"));
			}

		}
		if (!appAndroidVersion.isEmpty() && !appIosVersion.isEmpty()) {
			appVersion = appAndroidVersion + "," + appIosVersion;
		} else if (!appAndroidVersion.isEmpty()) {
			appVersion = appAndroidVersion;
		} else {
			appVersion = appIosVersion;
		}
		if (!appVersion.isEmpty()) {
			input.put("appVersion", appVersion);
		}
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
						.getVersionIosDeviceFromKeyInDB(messJson.getString("deviceIosVersion"));
			}
		}
		if (!deviceAndroidVersion.isEmpty() && !deviceIosVersion.isEmpty()) {
			deviceVersion = deviceAndroidVersion + "," + deviceIosVersion;
		} else if (!deviceAndroidVersion.isEmpty()) {
			deviceVersion = deviceAndroidVersion;
		} else {
			deviceVersion = deviceIosVersion;
		}
		if (!deviceVersion.isEmpty()) {
			input.put("deviceVersion", deviceVersion);
		}
		return input;
	}
}
