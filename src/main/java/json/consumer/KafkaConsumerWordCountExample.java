package json.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerWordCountExample {

    private final static String TOPIC = "word-count-topic7";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";

  public static Consumer<String, Long> createConsumer() {
      final Properties props = new Properties();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer11");
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      // Create the consumer using props.
      final Consumer<String, Long> consumer = new KafkaConsumer<>(props);

      return consumer;
  }

    static void runConsumer() {
        final Consumer<String, Long> consumer = createConsumer();
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));

        final int giveUp = 100;
        int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<String, Long> consumerRecords =
                    consumer.poll(Duration.ofSeconds(1000));

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
                        record.key(), record.value(), record.partition(), record.offset());
            });

            consumer.commitAsync();
        }
        consumer.close();
        System.out.println("DONE");
    }

    public static void main(String[] args) {
//        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//        root.setLevel(Level.ERROR);
        runConsumer();
    }

}
