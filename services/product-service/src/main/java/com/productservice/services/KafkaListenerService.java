package com.productservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.event.BaseOrderEvent;
import com.productservice.dto.event.BasePaymentEvent;
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
    private final ReservationService reservationService;

    @KafkaListener(topics = {"payment.unpaid"}, groupId = "product-service")
    @Transactional
    public void listenPaymentUnpaid(String data, Acknowledgment ack) {
        try {
            BasePaymentEvent event = objectMapper.readValue(data, BasePaymentEvent.class);
            log.info("listenPaymentUnpaid for orderId: {} and paymentId: {}", event.orderId(), event.paymentId());

            reservationService.cancelReservation(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize order.failed event: {}", data, e);
        }
    }

    @KafkaListener(topics = {"order.failed"}, groupId = "product-service")
    public void listenOrderFailed(String data, Acknowledgment ack) {
        try {
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent.class);
            log.info("listenOrderFailed for orderId: {}", event.orderId());

            reservationService.cancelReservation(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize order.failed event: {}", data, e);
        }
    }

    @KafkaListener(topics = {"payment.paid"}, groupId = "product-service")
    public void listenOrderPaid(String data, Acknowledgment ack) {
        try {
            BasePaymentEvent event = objectMapper.readValue(data, BasePaymentEvent.class);
            log.info("listenOrderPaid for orderId: {} and paymentId: {}", event.orderId(), event.paymentId());

            reservationService.completeReservation(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize order.failed event: {}", data, e);
        }
    }
}
