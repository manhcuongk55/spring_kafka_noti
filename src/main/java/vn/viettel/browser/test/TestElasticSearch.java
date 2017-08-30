package vn.viettel.browser.test;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.Iterators;

import vn.viettel.browser.ultils.ElasticsearchUtils;

public class TestElasticSearch {
	
	static ElasticsearchUtils elasticsearchUtils = new ElasticsearchUtils();
	public static void main(String[] args) {
		int typeMess = 0;
		int total = 0;
		JSONObject inputSearch = new JSONObject();
	    JSONObject input = null;
		if(typeMess == 0){
			try {
				input = (JSONObject) elasticsearchUtils.getListDeviceIdsFromAllCategories(inputSearch).get("data");
				System.out.println("input ............. " + input);
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}else{
			try {
				input = (JSONObject) elasticsearchUtils.getListAllDevices(inputSearch).get("data");
				System.out.println("input ............. " + input);
			} catch (JSONException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		}
		Iterator<?> keys = input.keys();
		total = Iterators.size(keys);
		System.out.println(total);
	}

}
