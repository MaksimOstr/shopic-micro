package com.productservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.productservice.dto.event.UnpaidPaymentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaListenerService {
    private final ObjectMapper objectMapper;
    private final ReservationService reservationService;

    @KafkaListener(topics = "payment.unpaid", groupId = "product-service")
    public void listenUnpaidPayment(String data, Acknowledgment ack) {

        try {
            log.info("Order cancelled");
            UnpaidPaymentEvent event = objectMapper.readValue(data, UnpaidPaymentEvent.class);

            reservationService.cancelReservation(event.orderId());
            ack.acknowledge();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
