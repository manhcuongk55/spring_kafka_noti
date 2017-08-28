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
		JSONObject inputSearch = new JSONObject();
		JSONObject mess = new JSONObject(message);
		/*String arrayString = mess.getString("versionAndroid");
		if (arrayString.contains("[") && arrayString.contains("]")) {
			arrayString = arrayString.replace("[", "");
			arrayString = arrayString.replace("]", "");
			String[] arrayParamaterId = arrayString.split(",");
		}*/
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		String idJob = "";
		try {
			JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearch)
					.get("data");
			Iterator<?> keys = input.keys();

			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				JSONObject resultsAndroid = createJSonForAndroid(message);
				JSONObject resultsIos = createJSonForIos(message);
				String categoryId = "";
				try {
					idJob = mess.getString("articleId");
					jedisUtils.set("done" + idJob, 0 + "");
					categoryId = "categoryId:" + mess.getString("category");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (obj.has(categoryId)) {
					if (key.contains("ios")) {
						try {
							key.replace("ios", "");
							resultsIos.put("to", key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsIos);
							ids.put("ios_" + key, rp);
							System.out.println("@results_ios : " + resultsIos);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						resultsAndroid.put("to", key);
						String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsAndroid);
						ids.put("android_" + key, rp);
						System.out.println("@results_android : " + resultsAndroid);
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
		JSONObject inputSearch = new JSONObject();
		if (mess.has("test")) {
			return sendNotoToDeviceTest(message, ProductionConfig.TestFireBase);
		} else {
			int total = 0;
			JSONObject reponses = new JSONObject();
			JSONArray devices = new JSONArray();
			reponses.put("message", message);
			Map<String, String> ids = new HashMap<>();
			String idJob = "";
			try {
				JSONObject input = (JSONObject) elasticsearchUtils.getListAllDevices(inputSearch).get("data");
				Iterator<?> keys = input.keys();
				while (keys.hasNext()) {
					String key = (String) keys.next();
					JSONObject resultsAndroid = createJSonForAndroid(message);
					JSONObject resultsIos = createJSonForIos(message);
					idJob = mess.getString("articleId");
					jedisUtils.set("done" + idJob, 0 + "");
					if (key.contains("ios")) {
						try {
							key.replace("ios", "");
							resultsIos.put("to", key);
							System.out.println("key:ios " + key);
							String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsIos);
							ids.put("ios_" + key, rp);
							System.out.println("@results_ios : " + resultsIos);
						} catch (JSONException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					} else {
						resultsAndroid.put("to", key);
						System.out.println("key:android " + key);
						String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsAndroid);
						ids.put("android_" + key, rp);
						System.out.println("@results_android : " + resultsAndroid);
					}
					total++;
					jedisUtils.set("sent_total" + idJob, total + "_" + elasticsearchUtils.getTotalDevice(inputSearch));

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
			JSONObject resultsAndroid = createJSonForAndroid(message);
			JSONObject resultsIos = createJSonForIos(message);
			try {
				idJob = mess.getString("articleId");
				jedisUtils.set("done" + idJob, 0 + "");

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (listDevices[i].contains("ios")) {
				try {
					resultsIos.put("to", listDevices[i]);
					System.out.println("key:ios " + listDevices[i]);
					String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsIos);
					ids.put("ios_" + listDevices[i], rp);
					System.out.println("@results_ios : " + resultsIos);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} else {
				resultsAndroid.put("to", listDevices[i]);
				System.out.println("key:android " + listDevices[i]);
				String rp = FireBaseUtils.pushNotificationToSingleDevice(resultsAndroid);
				ids.put("android_" + listDevices[i], rp);
				System.out.println("@results_android : " + resultsAndroid);
			}
			total++;
			jedisUtils.set("sent_total" + idJob, total + "_" + listDevices.length);
		}
		jedisUtils.set("done" + idJob, 1 + "");
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

	public static JSONObject createJSonForAndroid(String message) throws JSONException {
		JSONObject mess = new JSONObject(message);
		String idJob = mess.getString("articleId");
		JSONObject results = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("articleId", idJob);
		data.put("title", mess.getString("title"));
		data.put("image", mess.getString("image"));
		results.put("data", data);
		return results;
	}

	public static JSONObject createJSonForIos(String message) throws JSONException {
		JSONObject mess = new JSONObject(message);
		JSONObject notification = new JSONObject();
		notification.put("body", mess.getString("title"));
		notification.put("sound", "default");
		String idJob = mess.getString("articleId");
		JSONObject results = new JSONObject();
		results.put("notification", notification);
		results.put("mutable_content", true);
		JSONObject data = new JSONObject();
		data.put("articleId", idJob);
		data.put("title", mess.getString("title"));
		data.put("image", mess.getString("image"));
		results.put("data", data);
		return results;
	}
}
