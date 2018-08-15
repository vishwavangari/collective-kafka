import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class KafkaConsumerOffsetLevel {

    private final static String TOPIC = "collective-talk6";

    private static Consumer seekConsumerToBeginning(Consumer<String, String> kafkaConsumer, String topicName) {
        final List<TopicPartition> topicPartitionList = getPartitions(kafkaConsumer, topicName);
        kafkaConsumer.assign(topicPartitionList);
        kafkaConsumer.seekToBeginning(topicPartitionList);
        return kafkaConsumer;
    }

    private static List<TopicPartition> getPartitions(Consumer<String, String> kafkaConsumer, String topicName) {
        return kafkaConsumer.partitionsFor(topicName)
                     .stream()
                     .map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition()))
                     .collect(Collectors.toList());
    }


    private static Consumer seekConsumerToEnd(Consumer<String, String> kafkaConsumer, String topicName) {
        final List<TopicPartition> topicPartitionList = getPartitions(kafkaConsumer, topicName);
        kafkaConsumer.assign(topicPartitionList);
        kafkaConsumer.seekToEnd(topicPartitionList);
        return kafkaConsumer;
    }

    private static Consumer seekConsumerToOffset(Consumer<String, String> kafkaConsumer, TopicPartition topicPartition, long offset) {
        kafkaConsumer.seek(topicPartition, offset);
        return kafkaConsumer;
    }

    static void runConsumer() {
        final Consumer<String, String> kafkaConsumer = KafkaConsumerExample.createConsumer();

     //   seekConsumerToBeginning(kafkaConsumer, TOPIC);
    //    seekConsumerToEnd(kafkaConsumer, TOPIC);

//        final List<TopicPartition> topicPartitionList = getPartitions(kafkaConsumer, TOPIC);
//        seekConsumerToOffset(kafkaConsumer, topicPartitionList.get(0), 1);
//        seekConsumerToOffset(kafkaConsumer, topicPartitionList.get(1), 4);
//        kafkaConsumer.assign(topicPartitionList);

        final int giveUp = 100;
        int noRecordsCount = 0;

        while (true) {
            final ConsumerRecords<String, String> consumerRecords =
                    kafkaConsumer.poll(Duration.ofSeconds(100));

            if (consumerRecords.count()==0) {
                noRecordsCount++;
                if (noRecordsCount > giveUp) break;
                else continue;
            }

            consumerRecords.forEach(record -> {
                System.out.printf("Consumer Record:(%s, %s, %d, %d)\n",
                        record.key(), record.value(), record.partition(), record.offset());
            });

            kafkaConsumer.commitAsync();
        }
        kafkaConsumer.close();
        System.out.println("DONE");
    }

    public static void main(String[] args) {
//        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//        root.setLevel(Level.ERROR);
        runConsumer();
    }
}
