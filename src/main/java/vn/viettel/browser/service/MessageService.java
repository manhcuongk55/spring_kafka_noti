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

@Service
public class MessageService {
	ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	public String sendMessBoxToListDeviceIdsByVersion(String message) throws JSONException {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		try {
			JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories().get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject obj = (JSONObject) input.get(key);
				JSONObject mess = new JSONObject(message);
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				JSONObject notification = new JSONObject();
				String version_android = "";
				String version_ios = "";
				try {
					version_android = mess.getString("version_android");
					version_ios = mess.getString("version_ios");
					data.put("content", mess.getString("content"));
					data.put("title", mess.getString("title"));
					results.put("data", data);
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (key.contains("ios")) {
					if (obj.has(version_ios)) {
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
					}
				} else {
					if (obj.has(version_android)) {
						results.put("to", key);
						System.out.println("key:android " + key);
						String rp = FireBaseUtils.pushNotificationToSingleDevice(results);
						ids.put("android_" + key, rp);
						System.out.println("@results_android : " + results);
					}
				}
				total++;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

	public String sendMessBoxToAll(String message) throws JSONException {
		int total = 0;
		JSONObject reponses = new JSONObject();
		JSONArray devices = new JSONArray();
		reponses.put("message", message);
		Map<String, String> ids = new HashMap<>();
		try {
			JSONObject input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories().get("data");

			Iterator<?> keys = input.keys();

			/* Process to group firebase Id to category */
			while (keys.hasNext()) {
				String key = (String) keys.next();
				JSONObject mess = new JSONObject(message);
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		devices.put(ids);
		reponses.put("devices", devices);
		reponses.put("total", total);
		return reponses.toString();
	}

}
