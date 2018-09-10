package json.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerExample {
    private final static String TOPIC = "newsRestSourceDestinationTopic";
    private final static String BOOTSTRAP_SERVERS = "localhost:9092";

  public static Consumer<String, String> createConsumer() {
      final Properties props = new Properties();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaExampleConsumer12345_Test");
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
      props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
      // Create the consumer using props.
      final Consumer<String, String> consumer = new KafkaConsumer<>(props);

      return consumer;
  }

    static void runConsumer() {
        final Consumer<String, String> consumer = createConsumer();
        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(TOPIC));

        final int giveUp = 100;
        int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<String, String> consumerRecords =
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
