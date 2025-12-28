package com.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.dto.event.BasePaymentEvent;
import com.paymentservice.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, String> reliableKafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendCheckoutSessionSuccess(UUID orderId) {
        try {
            BasePaymentEvent event = new BasePaymentEvent(orderId);
            reliableKafkaTemplate.send("payment.paid", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Failed to send payment.paid event for orderId {}: {}", orderId, e.getMessage(), e);
            throw new ApiException("Failed to send payment event, try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void sendUnpaidPaymentEvent(UUID orderId) {
        try {
            BasePaymentEvent event = new BasePaymentEvent(orderId);
            reliableKafkaTemplate.send("payment.unpaid", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error("Failed to send payment.unpaid event for orderId {}: {}", orderId, e.getMessage(), e);
            throw new ApiException("Failed to send payment event, try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
