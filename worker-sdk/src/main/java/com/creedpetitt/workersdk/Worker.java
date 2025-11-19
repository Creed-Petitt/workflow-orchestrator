package com.creedpetitt.workersdk;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

public class Worker {

    private final Map<String, Handler> handlers = new HashMap<>();
    private KafkaConsumer<String, JobMessage> consumer;
    private KafkaProducer<String, ResultMessage> producer;

    public void register(String action, Handler handler) {

    }

    public void start() {

    }

}


