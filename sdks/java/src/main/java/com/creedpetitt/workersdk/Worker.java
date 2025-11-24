package com.creedpetitt.workersdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Worker {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final Map<String, Handler> handlers = new HashMap<>();
    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;
    private String bootstrapServers;

    public void register(String action, Handler handler) {
        handlers.put(action, handler);
    }

    public void start() {
        start("kafka:9092");
    }

    public void start(String bootstrapServers) {

        this.bootstrapServers = bootstrapServers;

        // Init consumer and producer
        this.consumer = new KafkaConsumer<>(getConsumerProps());
        this.producer = new KafkaProducer<>(getProducerProps());

        consumer.subscribe(Collections.singleton("workflow-jobs"));

        System.out.println("Workflow started, listening for jobs...");

        while (true) {
            var records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
                JobMessage msg = deserializeJob(record.value());

                System.out.println("Received job: " + msg);

                Handler handler = handlers.get(msg.action());
                if (handler == null) {
                    System.err.println("No handler for " + msg.action());
                    continue;
                }

                String output;
                try {
                    output = handler.handle(msg.payload());
                } catch (Exception e) {
                    output = "{\"error\":\"" + e.getMessage() + "\"}";
                }

                ResultMessage res =
                        new ResultMessage(msg.workflowRunId(), msg.action(), output);

                producer.send(
                        new ProducerRecord<>("workflow-results", serializeResult(res))
                );
            }
        }
    }

    // Serialize/deserialize helper methods
    private String serializeResult(ResultMessage result) {
        try {
            return MAPPER.writeValueAsString(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JobMessage deserializeJob(String json) {
        try {
            return MAPPER.readValue(json, JobMessage.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Kafka consumer/producer properties
    private Properties getConsumerProps() {
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", bootstrapServers);
        consumerProps.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("group.id", "workflow-workers");
        consumerProps.put("auto.offset.reset", "earliest");

        return consumerProps;
    }

    private Properties getProducerProps() {
        Properties producerProps = new Properties();
        producerProps.put("bootstrap.servers", bootstrapServers);
        producerProps.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        return producerProps;
    }
}