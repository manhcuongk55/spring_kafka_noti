package vn.viettel.browser.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.FireBaseUtils;
import vn.viettel.browser.ultils.JedisUtils;

@Service
public class MessageBoxService {
	ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	JedisUtils jedisUtils = new JedisUtils();

	public String sendMessBoxToListDeviceIdsByVersion(String message) throws Exception {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		JSONObject mess = new JSONObject(message);
		JSONObject data = new JSONObject();
		JSONObject notification = new JSONObject();
		String version_android = "";
		String version_ios = "";
		try {
			version_android = mess.getString("version_android");
			version_ios = mess.getString("version_ios");
			data.put("content", mess.getString("content"));
			data.put("title", mess.getString("title"));
			data.put("type", mess.getString("type"));
			jedisUtils.set("done_box"+ mess.getString("content"), 0 + "");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JSONArray listIos = elasticsearchUtils.getListDeviceByVersion("ios", version_ios);
		JSONArray listAndroid = elasticsearchUtils.getListDeviceByVersion("android", version_android);
		int all = listIos.length() + listAndroid.length();
		jedisUtils.set("sent_total_box" + mess.getString("content"), total + "_" + all);
		for (int i = 0; i < listIos.length(); i++) {
			try {
				JSONObject results = new JSONObject();
				results.put("data", data);
				notification.put("body", mess.getString("title"));
				// notification.put("badge",0);
				notification.put("sound", "default");
				results.put("notification", notification);
				results.put("mutable_content", true);
				JSONObject keyObj = (JSONObject) listIos.get(i);
				String key = keyObj.getString("key");
				results.put("to", key);
				String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
				ids.put("ios_" + key, rp);
				System.out.println("@results_ios : " + results);
				total++;
				jedisUtils.set("sent_total_box"+ mess.getString("content"), total + "_" + all);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		for (int i = 0; i < listAndroid.length(); i++) {
			JSONObject results = new JSONObject();
			results.put("data", data);
			JSONObject keyObj = (JSONObject) listAndroid.get(i);
			String key = keyObj.getString("key");
			results.put("to", key);
			System.out.println("key:android " + key);
			String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
			ids.put("android_" + key, rp);
			System.out.println("@results_android : " + results);
			total++;
			jedisUtils.set("sent_total_box"+ mess.getString("content"), total + "_" + all);
		}
		jedisUtils.set("done_box"+ mess.getString("content"), 1 + "");
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();

	}

	public String sendMessBoxToAll(String message) throws JSONException {
		JSONObject mess = new JSONObject(message);
		jedisUtils.set("done_box" +  mess.getString("content"), 0 + "");
		int total = 0;
		jedisUtils.set("sent_total_box" +  mess.getString("content"), total + "_" + elasticsearchUtils.getTotalDevice());
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		try {
			JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories("*").get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject notification = new JSONObject();
				try {
					data.put("type", mess.getString("type"));
					data.put("content", mess.getString("content"));
					data.put("title", mess.getString("title"));
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
				jedisUtils.set("sent_total_box" +  mess.getString("content"), total + "_" + elasticsearchUtils.getTotalDevice());

			}
			jedisUtils.set("done_box" +  mess.getString("content"), 1 + "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

}
