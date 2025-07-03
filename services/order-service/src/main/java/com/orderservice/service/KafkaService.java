package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.OrderCanceledEvent;
import com.orderservice.dto.event.OrderCompletedEvent;
import com.orderservice.exception.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendOrderCompletedEvent(long orderId) {
        try {
            OrderCompletedEvent event = new OrderCompletedEvent(orderId);

            kafkaTemplate.send("order.completed", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalException("Error while sending order completed event");
        }
    }

    public void sendOrderCanceledEvent(long orderId) {
        try {
            OrderCanceledEvent event = new OrderCanceledEvent(orderId);

            kafkaTemplate.send("order.canceled", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalException("Error while sending order completed event");
        }
    }
}
