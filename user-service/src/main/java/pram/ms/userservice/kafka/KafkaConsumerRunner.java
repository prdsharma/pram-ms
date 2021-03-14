package pram.ms.userservice.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;


public class KafkaConsumerRunner {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private String bootstrapServer;

    public KafkaConsumerRunner(String bootstrapServer) {
        this.bootstrapServer = bootstrapServer;
    }

    public void run(String topic, String groupId) {
        CountDownLatch latch = new CountDownLatch(1);
        KafkaConsumerRunnable consumerRunnable = new KafkaConsumerRunnable(topic, groupId, latch);
        Thread thr = new Thread(new KafkaConsumerRunnable(topic, groupId, latch));
        thr.start();

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            consumerRunnable.shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                logger.warn("consumer await interuppted in runtime shutdown hook.");
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.warn("consumer await interuppted in main.");
        }
    }

    class KafkaConsumerRunnable implements Runnable {

        private String topic;
        private String groupId;
        private KafkaConsumer consumer;
        private final CountDownLatch latch;

        public KafkaConsumerRunnable(String topic, String groupId, CountDownLatch latch) {
            this.topic = topic;
            this.groupId = groupId;
            this.latch = latch;

            Properties properties = new Properties();
            properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, groupId);
            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            consumer = new KafkaConsumer(properties);
        }

        @Override
        public void run() {
            consumer.subscribe(Collections.singleton(topic));
            try {
                logger.info("Starting consuming messages");
                while (true) {
                    ConsumerRecords<String, String> consumerRecords = consumer.poll(Duration.ofSeconds(5));

                    for (ConsumerRecord record : consumerRecords) {
                        //messages.add((String) record.value());
                        logger.info("Key: {}, Value: {}, Partition: {}, Offset: {}", record.key(), record.value(),
                                record.partition(), record.offset());
                    }
                }
            } catch(WakeupException wakeupException) {
                logger.error("Consumer waked up or interrupted.");
                latch.countDown();
            } finally {
                consumer.close();
                logger.info("Consumer closed.");
            }

        }

        public void shutdown() {
            consumer.wakeup();
        }
    }
}
