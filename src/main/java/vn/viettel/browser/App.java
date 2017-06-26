package vn.viettel.browser;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import vn.viettel.browser.global.Variables;

import java.io.IOException;

/**
 * Hello world!
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@Configuration
@EnableCaching
@EnableScheduling
public class App {
    public static void main(String[] args) throws IOException {
       // init();
        SpringApplication.run(App.class, args);

    }

    private static void init() throws IOException {
        // Config proxy
        int debug = 0;
        if (debug == 1) {
            System.getProperties().put("http.proxySet", "true");
            System.getProperties().put("http.proxyHost", "192.168.10.30");
            System.getProperties().put("http.proxyPort", "6969");
            System.getProperties().put("https.proxySet", "true");
            System.getProperties().put("https.proxyHost", "192.168.10.30");
            System.getProperties().put("https.proxyPort", "6969");
        }

        // Config Hbase
        org.apache.hadoop.conf.Configuration configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.property.clientPort", "2181");
        configuration.set("hbase.zookeeper.quorum", Variables.quorum_hbase);
        configuration.set("hbase.master", "16000");
        configuration.set("zookeeper.znode.parent", "/hbase-unsecure");
        configuration.set("hbase.rpc.timeout", "1800000");
        configuration.set("hbase.client.retries.number", "1000");
        Connection connection = ConnectionFactory.createConnection(configuration);
        Variables.table = connection.getTable(TableName.valueOf(Variables.nameHtable));
    }
}