package vn.viettel.browser.test;

import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.ultils.FireBaseUtils;
import vn.viettel.browser.worker.SetListFirsebaseIdToQueue;

public class TestFirebaseSend {
	public static void main(String[] args) throws Exception {
		JSONObject results = new JSONObject();
		JSONObject data = new JSONObject();
		data.put("articleId", "sssssssssssssssss");
		data.put("title", "sssssssssssssssss");
		data.put("image", "sssssssssssssssss");
		results.put("data", data);
		results.put("to", SetListFirsebaseIdToQueue.namesQueue.peek());
		FireBaseUtils.pushNotificationToSingleDevice(results);
	}

}
