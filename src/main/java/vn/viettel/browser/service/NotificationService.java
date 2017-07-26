package vn.viettel.browser.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import vn.viettel.browser.config.ProductionConfig;
import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.FireBaseUtils;
import vn.viettel.browser.ultils.JedisUtils;

@Service
public class NotificationService {

	ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	JedisUtils jedisUtils = new JedisUtils();

	public String sendNotoToListDeviceByCategoryId(String message) throws JSONException {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		String idJob = "";
		try {
			JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories("*").get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				JSONObject mess = new JSONObject(message);
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject notification = new JSONObject();
				String categoryId = "";
				try {
					idJob = mess.getString("articleId");
					jedisUtils.set("done" + idJob, 0 + "");
					categoryId = "categoryId:" + mess.getString("category");
					data.put("articleId", idJob);
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
					jedisUtils.set("sent_total" + idJob,
							total + "_" + elasticsearchUtils.getTotalDeviceByCategoryId(categoryId, "*"));
				}
			}
			jedisUtils.set("done" + idJob, 1 + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

	public String sendNotiToAll(String message) throws Exception {
		JSONObject mess = new JSONObject(message);
		if(mess.has("test")){
			return sendNotoToDeviceTest(message, ProductionConfig.TestFireBase);
		}else{
			int total = 0;
			JSONObject reponses = new JSONObject();
			JSONArray devices = new JSONArray();
			reponses.put("message", message);
			Map<String, String> ids = new HashMap<>();
			String idJob = "";
			try {
				JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories("*").get("data");

				Iterator<?> keys = input.keys();

				/* Process to group firebase Id to category */
				while (keys.hasNext()) {
					String key = (String) keys.next();
					JSONObject obj = (JSONObject) input.get(key);
					JSONObject results = new JSONObject();
					JSONObject data = new JSONObject();
					JSONObject notification = new JSONObject();
					try {
						idJob = mess.getString("articleId");
						jedisUtils.set("done" + idJob, 0 + "");
						data.put("articleId", idJob);
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
					jedisUtils.set("sent_total" + idJob, total + "_" + elasticsearchUtils.getTotalDevice());

				}
				jedisUtils.set("done" + idJob, 1 + "");
			} catch (Exception e) {
				e.printStackTrace();
			}
			devices.put(ids);
			reponses.put("devices", devices);
			reponses.put("total", total);
			return reponses.toString();
		}
		
	}

	public String sendNotoToDeviceTest(String message, String[] listDevices) throws Exception {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		String idJob = "";
		for (int i = 0; i < listDevices.length; i++) {
			JSONObject mess = new JSONObject(message);
			JSONObject results = new JSONObject();
			JSONObject data = new JSONObject();
			JSONObject notification = new JSONObject();
			String categoryId = "";
			try {
				idJob = mess.getString("articleId");
				jedisUtils.set("done" + idJob, 0 + "");
				categoryId = "categoryId:" + mess.getString("category");
				data.put("articleId", idJob);
				data.put("title", mess.getString("title"));
				data.put("image", mess.getString("image"));
				results.put("data", data);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (listDevices[i].contains("ios")) {
				try {
					notification.put("body", mess.getString("title"));
					// notification.put("badge",0);
					notification.put("sound", "default");
					results.put("notification", notification);
					results.put("mutable_content", true);
					listDevices[i].replace("ios", "");
					results.put("to", listDevices[i]);
					System.out.println("key:ios " + listDevices[i]);
					String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
					ids.put("ios_" + listDevices[i], rp);
					System.out.println("@results_ios : " + results);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				results.put("to", listDevices[i]);
				System.out.println("key:android " + listDevices[i]);
				String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
				ids.put("android_" + listDevices[i], rp);
				System.out.println("@results_android : " + results);
			}
			total++;
			jedisUtils.set("sent_total" + idJob,
					total + "_" + elasticsearchUtils.getTotalDeviceByCategoryId(categoryId, "*"));
		}
		jedisUtils.set("done" + idJob, 1 + "");
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

}
