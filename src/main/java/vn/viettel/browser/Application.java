package vn.viettel.browser;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import vn.viettel.browser.kafka.NotificationConsumerGroup;
import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.JedisUtils;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
@Configuration
@EnableCaching
public class Application {
	//public static final String brokers = "10.240.152.11:6667,10.240.152.12:6667,10.240.152.13:6667,10.240.152.14:6667";
	public static final String brokers = "lab02:6667,lab06:6667";
	public static final String groupId 	= "group01";
	public static final String topic 	= "notification";
	public static final int numberOfConsumer = 100;
	public static ElasticsearchUtils elasticsearchUtils;
	public static Properties propConsum;
	public static Properties propPro;
	public static JedisUtils jedisUtils;
	public static KafkaProducer<String, String> producer;

	public static void main(String[] args) throws IOException, JSONException {
		SpringApplication.run(Application.class, args);
		propPro = createProducerConfig(brokers);
		propConsum = createConsumerConfig(brokers, groupId);
		producer = new KafkaProducer<String, String>(propPro);
		elasticsearchUtils = new ElasticsearchUtils();
		jedisUtils = new JedisUtils();
		// Start group of Notification Consumers
		NotificationConsumerGroup consumerGroup = new NotificationConsumerGroup();
		consumerGroup.execute();
	}

	private static Properties createConsumerConfig(String brokers, String groupId) {
		Properties props = new Properties();
		props.put("bootstrap.servers", brokers);
		props.put("group.id", groupId);
		props.put("auto.offset.reset", "earliest");
		//props.put("enable.auto.commit", "true");
		//props.put("auto.commit.interval.ms", "1000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	private static Properties createProducerConfig(String brokers) {
		Properties props = new Properties();
		props.put("bootstrap.servers", brokers);
		props.put("acks", "all");
		/*props.put("acks", "all");
		props.put("retries", 0);
		props.put("metadata.fetch.timeout.ms", "3000");
		props.put("request.timeout.ms", "300000");
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);*/
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return props;
	}
}