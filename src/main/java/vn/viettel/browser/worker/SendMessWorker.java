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

	String message;
    float time1;
    float time2;
	public void setName(String message) {
		time1 = System.currentTimeMillis();
		this.message = message;
	}
	@Override
	public void run() {
		try {
			while(SetListFirsebaseIdToQueue.namesQueue.size() > 0) {
				JSONObject results = new JSONObject();
				JSONObject data = new JSONObject();
				data.put("articleId", "sssssssssssssssss");
				data.put("title", "sssssssssssssssss");
				data.put("image", "sssssssssssssssss");
				results.put("data", data);
				results.put("to", SetListFirsebaseIdToQueue.namesQueue.poll());
				
				System.out.println("Size Queue SendMessWorker....... " +  message + "......... "+ SetListFirsebaseIdToQueue.namesQueue.size());
				
				FireBaseUtils.pushNotificationToSingleDevice(results);
				System.out.println("@results_android : " + results);
			}
			time2 = System.currentTimeMillis();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("time of " + message + ".... " + (time2 -time1)/1000);
		

	}

}