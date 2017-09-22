package vn.viettel.browser.test;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;

/**
 * Created by giang on 14/09/2017.
 */
public class TestKafka {
    public static final String brokers = "10.240.152.148:6667";
    //public static final String brokers = "0.0.0.0:9092";
    public static Properties propPro;
    public static KafkaProducer<String,String> producer;

    public static void main(String[] args) {
        //System.out.println(brokers);
        propPro = createProducerConfig(brokers);
        producer = new KafkaProducer<String, String>(propPro);
        ProducerRecord<String,String> record = new ProducerRecord<String, String>("notification","cuongdm204");
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                System.out.println("success");
            }
        });
    }

    private static Properties createProducerConfig(String brokers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        //props.put("acks", "all");
        props.put("retries", 0);
        props.put("metadata.fetch.timeout.ms", "3000");
        props.put("request.timeout.ms", "300000");
        props.put("batch.size", 1);
        //props.put("linger.ms", 1);
        //props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }
}
