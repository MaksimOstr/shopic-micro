package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.BasePaymentEvent;
import com.orderservice.dto.event.BaseReservationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ObjectMapper objectMapper;
    private final OrderEventService orderEventService;


    @KafkaListener(topics = "reservation.confirmed", groupId = "orderService")
    @Transactional
    public void listenReservationConfirmed(String data, Acknowledgment ack) {
        try {
            log.info("listenReservationConfirmed");
            BaseReservationEvent event = objectMapper.readValue(data, BaseReservationEvent.class);

            orderEventService.confirmOrder(event.orderId());

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "reservation.canceled", groupId = "orderService")
    @Transactional
    public void listenReservationCanceled(String data, Acknowledgment ack) {
        log.info("listenReservationCanceled");
        try {
            BaseReservationEvent event = objectMapper.readValue(data, BaseReservationEvent.class);

            orderEventService.cancelOrder(event.orderId());

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @KafkaListener(topics = "payment.unpaid", groupId = "orderService")
    @Transactional
    public void listenPaymentUnpaid(String data, Acknowledgment ack) {
        log.info("listenReservationCanceled");
        try {
            BasePaymentEvent event = objectMapper.readValue(data, BasePaymentEvent.class);

            orderEventService.cancelOrder(event.orderId());

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }
}
