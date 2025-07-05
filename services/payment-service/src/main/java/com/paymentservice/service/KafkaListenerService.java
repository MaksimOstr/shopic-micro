package com.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentservice.dto.event.OrderCanceledEvent;
import com.paymentservice.entity.RefundReason;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final StripeRefundService stripeRefundService;
    private final ObjectMapper objectMapper;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = "order.canceled", groupId = "payment-service")
    public void listenOrderCanceledEvent(String payload, Acknowledgment ack) {
        try {
            OrderCanceledEvent event = objectMapper.readValue(payload, OrderCanceledEvent.class);

            stripeRefundService.processFullRefund(event.orderId(), RefundReason.ORDER_CANCELLATION);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
