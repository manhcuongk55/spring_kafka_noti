package vn.viettel.browser.service;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import vn.viettel.browser.ultils.JedisUtils;

@Service
public class CheckStatusService {
	JedisUtils jedisUtils = new JedisUtils();
	public String checkStatusSending() throws JSONException {
		JSONObject reponses = new JSONObject();
		reponses.put("done", jedisUtils.get("done"));
		reponses.put("sent_total", jedisUtils.get("sent_total"));
		return reponses.toString();
	}
}
