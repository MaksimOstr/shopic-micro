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
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ObjectMapper objectMapper;
    private final OrderEventService orderEventService;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = "checkout-session-success", groupId = "order-service")
    @Transactional
    public void listenCheckoutSessionSuccess(String payload, Acknowledgment ack) {
        try {
            CheckoutSuccessEvent event = objectMapper.readValue(payload, CheckoutSuccessEvent.class);

            orderEventService.payOrder(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = "payment.unpaid", groupId = "order-service")
    @Transactional
    public void listenUnpaidPayment(String payload, Acknowledgment ack) {
        try {
            UnpaidPaymentEvent event = objectMapper.readValue(payload, UnpaidPaymentEvent.class);

            orderEventService.failOrder(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
