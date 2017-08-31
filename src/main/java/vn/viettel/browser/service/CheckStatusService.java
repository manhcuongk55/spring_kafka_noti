package vn.viettel.browser.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import vn.viettel.browser.ultils.JedisUtils;

@Service
public class CheckStatusService {
	JedisUtils jedisUtils = new JedisUtils();
	public String checkStatusSending(String id) throws JSONException {
		JSONObject JsonId = new JSONObject(id);
		id = JsonId.getString("jobId");
		JSONObject reponses = new JSONObject();
		String sent_total = jedisUtils.get("sent_total"+ id);
		reponses.put("sent_total",sent_total + "");
		reponses.put("jobId", id + "");
		return reponses.toString();
	}
	public String checkStatusSendingBox(String id) throws JSONException {
		JSONObject JsonId = new JSONObject(id);
		id = JsonId.getString("jobId");
		JSONObject reponses = new JSONObject();
		String sent_total_box = jedisUtils.get("sent_total_box" + id);
		reponses.put("sent_total_box",sent_total_box + "" );
		reponses.put("jobId_box", id + "");
		return reponses.toString();
	}
}
