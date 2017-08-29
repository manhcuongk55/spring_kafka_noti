package vn.viettel.browser.test;

import org.json.JSONException;
import org.json.JSONObject;

import vn.viettel.browser.ultils.ElasticsearchUtils;

public class TestElasticSearch {
	
	static ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	public static void main(String[] args) {
		JSONObject inputSearch = new JSONObject();
		JSONObject input = new JSONObject();
		try {
			inputSearch.put("deviceType", "*");
			inputSearch.put("deviceVersion", "6.0");
			input = (JSONObject) elasticsearchUtils.getListAllDevices(inputSearch).get("data");
			System.out.println(input);
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	}

}
