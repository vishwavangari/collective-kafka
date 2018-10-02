package json.producer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.connect.json.JsonSerializer;

import java.io.File;
import java.util.Properties;

public class KafkaJSONProducerExample {

    private final static String TOPIC = "users_test_topic";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";
    private final static ObjectMapper objectMapper = new ObjectMapper();

    private static Producer<String, JsonNode> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer-test1235");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());

        //Set acknowledgements for producer requests.
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        //If the request fails, the producer can automatically retry,
        props.put(ProducerConfig.RETRIES_CONFIG, 0);

        //Specify buffer size in config
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);

        //Reduce the no of requests less than 0
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1);

        //The buffer.memory controls the total amount of memory available to the producer for buffering.
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);

        return new KafkaProducer<>(props);
    }
  //  { "id": "1", "firstName": "mynamefirst", "lastName": "mynamelast", "createdAt": "2018-09-26 21:04:10.692953Z", "updatedAt": "2018-09-26 21:04:10.692953Z" }
    static void runProducer(final int sendMessageCount) throws Exception {
        final Producer<String, JsonNode> producer = createProducer();
        try {
            for (int index = 0; index < sendMessageCount; index++) {
                final ProducerRecord<String, JsonNode> record = new ProducerRecord<>(TOPIC, Integer.toString(index),
                        objectMapper.readValue(new File("./src/main/resources/user.json"), JsonNode.class));

                RecordMetadata metadata = producer.send(record).get();
                System.out.printf("sent record(key=%s value=%s) " + "meta(partition=%d, offset=%d)\n",
                        record.key(), record.value(), metadata.partition(), metadata.offset());
            }
        } finally {
            producer.flush();
            producer.close();
        }
    }

    public static void main(String[] args) throws Exception {
        runProducer(10);
    }
}