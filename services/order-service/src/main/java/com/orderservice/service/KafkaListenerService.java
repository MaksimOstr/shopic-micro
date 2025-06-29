package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.CheckoutSuccessEvent;
import com.orderservice.entity.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 1000))
    @KafkaListener(topics = "checkout-session-success", groupId = "order-service")
    public void listenCheckoutSessionSuccess(String payload) {
        try {
            CheckoutSuccessEvent event = objectMapper.readValue(payload, CheckoutSuccessEvent.class);

            orderService.changeOrderStatus(event.orderId(), OrderStatusEnum.PAID);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
