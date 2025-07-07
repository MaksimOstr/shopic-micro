package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.BasicOrderEvent;
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
            BasicOrderEvent event = new BasicOrderEvent(orderId);

            kafkaTemplate.send("order.completed", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalException("Error while sending order completed event");
        }
    }

    public void sendOrderCanceledEvent(long orderId) {
        try {
            BasicOrderEvent event = new BasicOrderEvent(orderId);

            kafkaTemplate.send("order.canceled", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalException("Error while sending order completed event");
        }
    }

    public void sendOrderReturnEvent(long orderId) {
        try {
            BasicOrderEvent event = new BasicOrderEvent(orderId);

            kafkaTemplate.send("order.returned", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalException("Error while sending order completed event");
        }
    }
}
