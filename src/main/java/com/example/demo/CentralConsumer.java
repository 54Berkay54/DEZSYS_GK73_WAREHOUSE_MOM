package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CentralConsumer {

    @Autowired
    private CentralDataAggregator aggregator;

    @KafkaListener(topics = "warehouse-input")
    public void receiveMessage(String message) throws Exception {
        WarehouseData data = new ObjectMapper().readValue(message, WarehouseData.class);

        System.out.println("SUCCESS" + message);

        aggregator.add(data);
    }
}