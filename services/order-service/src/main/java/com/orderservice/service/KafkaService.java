package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.BasicOrderEvent;
import com.orderservice.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


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

    public void sendOrderCompletedEvent(UUID orderId) {
        sendEvent("order.completed", orderId);
    }

    public void sendOrderFailedEvent(UUID orderId) {
        sendEvent("order.failed", orderId);
    }

    public void sendOrderReturnEvent(UUID orderId) {
        sendEvent("order.returned", orderId);
    }

    private void sendEvent(String topic, UUID orderId) {
        try {
            BasicOrderEvent event = new BasicOrderEvent(orderId);
            String message = objectMapper.writeValueAsString(event);
            atLeastOnceBatchTemplate.send(topic, message);
        } catch (JsonProcessingException e) {
            log.error("Error while sending event to topic {}: {}", topic, e.getMessage());
            throw new ApiException("Error while sending event to Kafka", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
