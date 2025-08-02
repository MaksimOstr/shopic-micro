package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.CheckoutSuccessEvent;
import com.orderservice.dto.event.UnpaidPaymentEvent;
import com.orderservice.entity.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ObjectMapper objectMapper;
    private final OrderEventService orderEventService;


    @KafkaListener(topics = "checkout-session-success", containerFactory = "batchFactory", batch = "true")
    @Transactional
    public void listenCheckoutSessionSuccess(@Payload List<String> messages, Acknowledgment ack) {
        try {
            log.info("listenCheckoutSessionSuccess");
            for (String message : messages) {
                CheckoutSuccessEvent event = objectMapper.readValue(message, CheckoutSuccessEvent.class);

                orderEventService.payOrder(event.orderId());
            }

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }


    @KafkaListener(topics = "payment.unpaid", containerFactory = "batchFactory", batch = "true")
    @Transactional
    public void listenUnpaidPayment(@Payload List<String> messages, Acknowledgment ack) {
        log.info("listenUnpaidPayment");
        try {
            for (String message : messages) {
                UnpaidPaymentEvent event = objectMapper.readValue(message, UnpaidPaymentEvent.class);

                orderEventService.failOrder(event.orderId());
            }

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }
}
