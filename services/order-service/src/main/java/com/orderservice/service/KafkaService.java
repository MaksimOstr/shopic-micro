package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.BasicOrderEvent;
import com.orderservice.exception.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class KafkaService {
    private final KafkaTemplate<String, String> atLeastOnceBatchTemplate;
    private final ObjectMapper objectMapper;

    public KafkaService(
            @Qualifier("atLeastOnceBatchTemplate") KafkaTemplate<String, String> atLeastOnceBatchTemplate,
            ObjectMapper objectMapper
    ) {
        this.atLeastOnceBatchTemplate = atLeastOnceBatchTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendOrderCompletedEvent(long orderId) {
        sendEvent("order.completed", orderId);
    }

    public void sendOrderCanceledEvent(long orderId) {
        sendEvent("order.canceled", orderId);
    }

    public void sendOrderReturnEvent(long orderId) {
        sendEvent("order.returned", orderId);
    }

    private void sendEvent(String topic, long orderId) {
        try {
            BasicOrderEvent event = new BasicOrderEvent(orderId);
            String message = objectMapper.writeValueAsString(event);
            atLeastOnceBatchTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            log.error("Error while sending event to topic {}: {}", topic, e.getMessage());
            throw new InternalException("Error while sending event to Kafka");
        }
    }
}
