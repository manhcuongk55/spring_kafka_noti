package vn.viettel.browser.test;

import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.ultils.ElasticsearchUtils;

public class QueryObjectDemo {
	private static final ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();

	public static void main(String[] args) throws JSONException {

		JSONObject inputSearcJson = new JSONObject();
		inputSearcJson.put("appVersion", "inputSearcJson");
		inputSearcJson.put("appVersion1", "inputSearcJson");
		inputSearcJson.put("appVersion2", "inputSearcJson");
		inputSearcJson.remove("appVersion2");
		System.out.println("input ............. " + inputSearcJson.toString());
	}

}