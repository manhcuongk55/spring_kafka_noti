package vn.viettel.browser.ultils;

import redis.clients.jedis.Jedis;

public class JedisUtils {
	static Jedis jedis = new Jedis("localhost");
    public static String get(String key){
    	return jedis.get(key);
    }
    public static void set(String key, String value){
    	jedis.set(key, value);
    }
}
