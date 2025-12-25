package com.productservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.event.BaseOrderEvent;
import com.productservice.dto.event.BasePaymentEvent;
import com.productservice.entity.ReservationStatusEnum;
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
    private final KafkaService kafkaService;

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = {"order.returned", "order.canceled"}, groupId = "product-service")
    @Transactional
    public void listenReturnedOrder(String data, Acknowledgment ack) {
        try {
            log.info("listenReturnedOrCanceledOrder");
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent.class);

            reservationService.cancelReservation(event.orderId());
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = "order.completed", groupId = "product-service")
    @Transactional
    public void listenCompletedOrder(String data, Acknowledgment ack) {
        try {
            log.info("listenCompletedOrder");
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent.class);

            reservationService.updateReservationStatus(event.orderId(), ReservationStatusEnum.COMPLETED);
            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = {"payment.unpaid"}, groupId = "product-service")
    @Transactional
    public void listenPaymentUnpaid(String data, Acknowledgment ack) {
        try {
            log.info("listenPaymentUnpaid");
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent.class);

            reservationService.cancelReservation(event.orderId());

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @RetryableTopic(attempts = "2", backoff = @Backoff(delay = 5000))
    @KafkaListener(topics = {"payment.paid"}, groupId = "product-service")
    public void listenOrderPaid(String data, Acknowledgment ack) {
        try {
            log.info("listenOrderPaid");
            BaseOrderEvent event = objectMapper.readValue(data, BaseOrderEvent.class);

            reservationService.updateReservationStatus(event.orderId(), ReservationStatusEnum.COMPLETED);
            kafkaService.sendReservationConfirmed(event.orderId());

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
