package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.BasePaymentEvent;
import com.orderservice.dto.event.BaseReservationEvent;
import com.orderservice.dto.event.RefundEvent;
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
    private final OrderService orderService;


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

    @KafkaListener(topics = "refund.success", groupId = "orderService")
    @Transactional
    public void listenRefundSuccess(String data, Acknowledgment ack) {
        log.info("listenReservationCanceled");
        try {
            RefundEvent event = objectMapper.readValue(data, RefundEvent.class);

            orderService.changeRefundStatus(event.orderId(), true);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }
}
