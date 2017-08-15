package vn.viettel.browser.worker;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import vn.viettel.browser.service.NotificationService;
import vn.viettel.browser.ultils.FireBaseUtils;

@Component
@Scope("prototype")
public class SendMessWorker implements Runnable {

	/*String message;

	public void setMessage(String message) {
		this.message = message;
	}*/
	@Override
	public void run() {
		try {
			JSONObject results = new JSONObject();
			JSONObject data = new JSONObject();
			data.put("articleId", "sssssssssssssssss");
			data.put("title", "sssssssssssssssss");
			data.put("image", "sssssssssssssssss");
			results.put("data", data);
			results.put("to", SetListFirsebaseIdToQueue.namesQueue.peek());
			FireBaseUtils.pushNotificationToSingleDevice(results);
			System.out.println("@results_android : " + results);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}