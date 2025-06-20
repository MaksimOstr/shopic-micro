package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<Object, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendOrderCreatedEvent(

    ) throws JsonProcessingException {
        OrderCreatedEvent event = new OrderCreatedEvent();
        kafkaTemplate.send("order-created", objectMapper.writeValueAsString(event));
    }
}
