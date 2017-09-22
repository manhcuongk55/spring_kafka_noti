package vn.viettel.browser.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import vn.viettel.browser.Application;
import vn.viettel.browser.ultils.JedisUtils;

@Service
public class CheckStatusService {
	public String checkStatusSending(String id) throws JSONException {
		JSONObject JsonId = new JSONObject(id);
		String jobId = JsonId.getString("jobId");
		JSONObject reponses = new JSONObject();
		String sent_total = Application.jedisUtils.get("sent_total"+ jobId);
		String received = Application.jedisUtils.get("received"+ jobId);
		reponses.put("sent_total",sent_total + "");
		reponses.put("jobId", jobId + "");
		reponses.put("received", received + "");
		return reponses.toString();
	}
}
