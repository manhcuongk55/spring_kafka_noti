package vn.viettel.browser.ultils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	public static JSONObject createJSonForAndroidNotification(String message) throws JSONException {
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

	public static JSONObject createJSonForIosNotification(String message) throws JSONException {
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
	public static JSONObject createJSonForAndroidForSendMessBox(String message) throws JSONException {
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

	public static JSONObject createJSonForIosForSendMessBox(String message) throws JSONException {
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
