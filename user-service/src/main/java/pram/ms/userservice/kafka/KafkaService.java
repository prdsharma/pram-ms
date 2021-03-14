package pram.ms.userservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
public class KafkaService {

    private static final String CONSUMER_GROUP = "my_user_app";

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${kafka.bootstrap-server}")
    private String bootstrapServer;

    public void produce(String topic, String key, String message) {

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);

        kafkaProducer.send(record, (recordMetadata, e) -> {
            if(e == null) {
                logger.info("message produced. topic= {} partitions= {} offset= {} timestamp= {}",
                        recordMetadata.topic(), recordMetadata.partition(),
                        recordMetadata.offset(), recordMetadata.timestamp());
            }
            else {
                 logger.error("Error while producing", e);
            }
        });

        kafkaProducer.flush();
        kafkaProducer.close();
    }

    public List<String> consume(String topic) throws InterruptedException {

        Properties properties = new Properties();
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, CONSUMER_GROUP);
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Collections.singleton(topic));

        List<String> messages = new ArrayList<>();

        new Thread( () -> {

             while (true) {
            ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(5));

            for (ConsumerRecord record : consumerRecords) {
                messages.add((String) record.value());
                logger.info("Key: {}, Value: {}, Partition: {}, Offset: {}", record.key(), record.value(),
                        record.partition(), record.offset());
            }
            }
        }).start();
        Thread.sleep(1000);
        return  messages;

    }
}
