package specificavro.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import specificavro.LogLine;

import java.util.Properties;

public class KafkaAvroProducer {

    public static void main(String[] args) {
        String inputTopic = "logline-topic";
        String schemaUrl = "http://localhost:8081";
        String brokers = "localhost:9092";

        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        props.put("schema.registry.url", schemaUrl);

        Producer<String, LogLine> producer = new KafkaProducer<>(props);

        for (long nEvents = 0; nEvents < 1000; nEvents++) {
            LogLine event = EventGenerator.getNext();
            ProducerRecord<String, LogLine> record = new ProducerRecord<>(inputTopic, event.getIp(), event);
            producer.send(record);
        }
    }
}

