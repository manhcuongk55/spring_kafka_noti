package vn.viettel.browser.ultils;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONUtils {
	public static JSONObject createJSonForAndroidNotification(String message) throws JSONException {
		JSONObject mess = new JSONObject(message);
		String idJob = "";
		if (mess.has("articleId")) {
			idJob = mess.getString("articleId");
		}
		JSONObject results = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("articleId", mess.has("articleId") ? mess.getString("articleId") : "");
		data.put("title", mess.has("title") ? mess.getString("title") : "");
		data.put("image", mess.has("image") ? mess.getString("image") : "");
		results.put("data", data);
		return results;
	}

	public static JSONObject createJSonForIosNotification(String message) throws JSONException {
		JSONObject mess = new JSONObject(message);
		JSONObject notification = new JSONObject();
		notification.put("body", mess.has("title") ? mess.getString("title") : "");
		notification.put("sound", "default");
		String idJob = mess.has("articleId") ? mess.getString("articleId") : "";
		JSONObject results = new JSONObject();
		results.put("notification", notification);
		results.put("mutable_content", true);
		JSONObject data = new JSONObject();
		data.put("articleId", idJob);
		data.put("title", mess.has("title") ? mess.getString("title") : "");
		data.put("image", mess.has("image") ? mess.getString("image") : "");
		results.put("data", data);
		return results;
	}
}
