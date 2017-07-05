package vn.viettel.browser.ultils;

import redis.clients.jedis.Jedis;

public class JedisUtils {
	
    Jedis jedis = new Jedis("localhost");
    public  String get(String key){
    	return jedis.get(key);
    }
    public  void set(String key, String value){
    	jedis.set(key, value);
    }
}
