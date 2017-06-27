package vn.viettel.browser.service;

import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.ultils.JedisUtils;

public class CheckStatusService {
	
	public String checkStatusSending() throws JSONException {
		JSONObject reponses = new JSONObject();
		reponses.put("done", JedisUtils.get("done"));
		reponses.put("sent_total", JedisUtils.get("sent_total"));
		return reponses.toString();
	}
}
