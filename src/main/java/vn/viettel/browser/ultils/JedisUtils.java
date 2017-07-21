package vn.viettel.browser.ultils;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import vn.viettel.browser.config.ProductionConfig;

import java.util.HashSet;
import java.util.Set;

public class JedisUtils {

    Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
    JedisCluster jc = new JedisCluster(jedisClusterNodes);
    private String[] hosts = {""};

    public JedisUtils() {
        if (ProductionConfig.PRODUCTION_ENV == true) {
            this.hosts = ProductionConfig.REDIS_HOST_PRODUCTION;
        } else {
            this.hosts = ProductionConfig.REDIS_HOST_STAGING;
        }

        for (String host : this.hosts) {
            this.jedisClusterNodes.add(new HostAndPort(host, ProductionConfig.REDIS_PORT));
        }
        this.jc = new JedisCluster(this.jedisClusterNodes);
    }

    public String get(String key) {
        return jc.get(key);
    }

    public void set(String key, String value) {
        jc.set(key, value);
    }
}
