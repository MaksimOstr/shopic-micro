package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.BasicOrderEvent;
import com.orderservice.exception.InternalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//refactor
@Slf4j
@Service
public class KafkaService {
    private final KafkaTemplate<String, String> atLeastOnceBatchTemplate;
    private final ObjectMapper objectMapper;

    public KafkaService (
            @Qualifier("atLeastOnceBatchTemplate") KafkaTemplate<String, String> atLeastOnceBatchTemplate,
            ObjectMapper objectMapper
    ) {
        this.atLeastOnceBatchTemplate = atLeastOnceBatchTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendOrderCompletedEvent(long orderId) {
        try {
            BasicOrderEvent event = new BasicOrderEvent(orderId);

            atLeastOnceBatchTemplate.send("order.completed", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalException("Error while sending order completed event");
        }
    }

    public void sendOrderCanceledEvent(long orderId) {
        try {
            BasicOrderEvent event = new BasicOrderEvent(orderId);

            atLeastOnceBatchTemplate.send("order.canceled", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalException("Error while sending order completed event");
        }
    }

    public void sendOrderReturnEvent(long orderId) {
        try {
            BasicOrderEvent event = new BasicOrderEvent(orderId);

            atLeastOnceBatchTemplate.send("order.returned", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new InternalException("Error while sending order completed event");
        }
    }
}
