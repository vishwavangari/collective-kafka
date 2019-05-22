package specificavro.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
public class KafkaAvroConsumerExample {

    private final KafkaConsumer<String, ?> consumer;
    private final String inputTopic;

    public static void main(String[] args) {

        String groupId = "twitter-source-topic-groupid1";
        String inputTopic = "twitter-source-topic";
        String url = "http://localhost:8081";
        String brokers = "localhost:9092";

        KafkaAvroConsumerExample kafkaAvroConsumerExample = new KafkaAvroConsumerExample(brokers, groupId, inputTopic, url);
        kafkaAvroConsumerExample.run();

    }

    public KafkaAvroConsumerExample(String brokers, String groupId, String inputTopic, String url) {
        this.consumer = new KafkaConsumer(createConsumerConfig(brokers, groupId, url));
        this.inputTopic = inputTopic;
    }

    private Properties createConsumerConfig(String brokers, String groupId, String url) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("group.id", groupId);
        props.put("auto.commit.enable", "false");
        props.put("auto.offset.reset", "earliest");
        props.put("schema.registry.url", url);
        props.put("specific.avro.reader", false);
        props.put("key.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
        props.put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");

        return props;
    }

    private void run() {
        consumer.subscribe(Collections.singletonList(inputTopic));
        System.out.println("Reading topic:" + inputTopic);

        while (true) {
            ConsumerRecords<String, ?> records = consumer.poll(Duration.ofSeconds(1000));
            for (ConsumerRecord<String, ?> record: records) {
               // log.info("Consumer Record Key.... {}", record.key());
               // log.info("Consumer Record Value....{}", record.value());
            }
            consumer.commitSync();
        }
    }
}
