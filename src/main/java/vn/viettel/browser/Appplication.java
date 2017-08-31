package vn.viettel.browser;

import java.io.IOException;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import vn.viettel.browser.kafka.NotificationConsumerGroup;
import vn.viettel.browser.ultils.ElasticsearchUtils;
import vn.viettel.browser.ultils.JedisUtils;

@SpringBootApplication
@ComponentScan(basePackages = { "vn.com" })
@Configuration
public class Appplication {
	public static final String brokers = "localhost:9092";
	public static final String groupId = "group05";
	public static final String topic = "cuong1";
	public static final int numberOfConsumer = 10;
	public static ElasticsearchUtils elasticsearchUtils;
	public static Properties propConsum;
	public static Properties propPro;
	public static JedisUtils jedisUtils;
	public static KafkaProducer<String, String> producer;

	public static void main(String[] args) throws IOException, JSONException {
		SpringApplication.run(Appplication.class, args);
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
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "300000");
		props.put("request.timeout.ms", "3000000");
		props.put("auto.offset.reset", "earliest");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		return props;
	}

	private static Properties createProducerConfig(String brokers) {
		Properties props = new Properties();
		props.put("bootstrap.servers", brokers);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return props;
	}
}