package com.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.dto.event.CheckoutSuccessEvent;
import com.paymentservice.dto.event.UnpaidPaymentEvent;
import com.paymentservice.exception.InternalException;
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


    public void sendCheckoutSessionSuccess(long orderId) {
        try {
            CheckoutSuccessEvent event = new CheckoutSuccessEvent(orderId);

            kafkaTemplate.send("checkout-session-success", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
    }

    public void sendUnpaidPaymentEvent(long orderId) {
        try {
            UnpaidPaymentEvent event = new UnpaidPaymentEvent(orderId);

            kafkaTemplate.send("payment.unpaid", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }


}
