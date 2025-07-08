package com.productservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.event.BaseOrderEvent;
import com.productservice.services.products.ProductQueryService;
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
    private final ObjectMapper objectMapper;
    private final ReservationService reservationService;
    private final ProductQueryService productQueryService;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = {"payment.unpaid", "order.canceled"}, groupId = "product-service")
    public void listenUnpaidPayment(String data, Acknowledgment ack) {
        try {
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent .class);

            reservationService.cancelReservation(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = "order.canceled", groupId = "product-service")
    public void listenCanceledOrder(String data, Acknowledgment ack) {
        try {
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent.class);

            reservationService.cancelReservation(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = "order.returned", groupId = "product-service")
    public void listenReturnedOrder(String data, Acknowledgment ack) {
        try {
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent.class);

            reservationService.cancelReservation(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = "order.completed", groupId = "product-service")
    public void listenCompletedOrder(String data, Acknowledgment ack) {

        try {
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent.class);

            reservationService.deleteReservation(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
