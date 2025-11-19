package com.creedpetitt.workersdk;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Worker {

    private final Map<String, Handler> handlers = new HashMap<>();
    private KafkaConsumer<String, String> consumer;
    private KafkaProducer<String, String> producer;

    public void register(String action, Handler handler) {
        handlers.put(action, handler);
    }

    public void start() {

        // Init
        this.consumer = new KafkaConsumer<>(getConsumerProps());
        this.producer = new KafkaProducer<>(getProducerProps());

    }

    // Serialize/deserialize helper methods
    private String serializeResult(ResultMessage result) {
        try {
            return new ObjectMapper().writeValueAsString(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JobMessage deserializeJob(String json) {
        try {
            return new ObjectMapper().readValue(json, JobMessage.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Kafka consumer/producer properties

    private Properties getConsumerProps() {
        Properties consumerProps = new Properties();
        consumerProps.put("bootstrap.servers", "localhost:9092");
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
        producerProps.put("bootstrap.servers", "localhost:9092");
        producerProps.put("key.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.put("value.serializer",
                "org.apache.kafka.common.serialization.StringSerializer");

        return producerProps;
    }


}


