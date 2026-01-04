package com.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.event.BasePaymentEvent;
import com.orderservice.dto.event.BaseReservationEvent;
import com.orderservice.dto.event.RefundEvent;
import com.orderservice.dto.event.ReservationCancelledEvent;
import com.orderservice.entity.OrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ObjectMapper objectMapper;
    private final OrderService orderService;



    @KafkaListener(topics = "payment.unpaid", groupId = "orderService")
    @Transactional
    public void listenPaymentUnpaid(String data, Acknowledgment ack) {
        try {
            BasePaymentEvent event = objectMapper.readValue(data, BasePaymentEvent.class);
            log.info("listenPaymentUnpaid for orderId: {}", event.orderId());

            orderService.updateOrderStatus(event.orderId(), OrderStatusEnum.CANCELLED);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }

    @KafkaListener(topics = "payment.paid", groupId = "orderService")
    @Transactional
    public void listenPaymentPaid(String data, Acknowledgment ack) {
        try {
            BasePaymentEvent event = objectMapper.readValue(data, BasePaymentEvent.class);
            log.info("listenPaymentPaid for orderId: {}", event.orderId());

            orderService.updateOrderStatus(event.orderId(), OrderStatusEnum.PROCESSING);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }

    @KafkaListener(topics = "reservation.cancelled", groupId = "orderService")
    @Transactional
    public void listenReservationCancelled(String data, Acknowledgment ack) {
        try {
            ReservationCancelledEvent event = objectMapper.readValue(data, ReservationCancelledEvent.class);
            log.info("listenReservationCancelled for orderId: {} and reservationId: {}", event.orderId(), event.reservationId());

            orderService.updateOrderStatus(event.orderId(), OrderStatusEnum.CANCELLED);

            ack.acknowledge();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }

    }

}
