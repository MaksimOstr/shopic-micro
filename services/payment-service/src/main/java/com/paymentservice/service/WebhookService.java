package com.paymentservice.service;

import com.paymentservice.entity.PaymentStatus;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final KafkaService kafkaService;
    private final PaymentService paymentService;

    public void handleWebhookEvent(Event event) {
        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSuccess(event);
                break;
        }
    }

    private void handleCheckoutSuccess(Event event) {
        String paymentId = event.getId();
        long orderId = paymentService.getOrderIdByPaymentId(paymentId);

        paymentService.changePaymentStatus(paymentId, PaymentStatus.SUCCEEDED);
        kafkaService.sendCheckoutSessionSuccess(orderId);
    }
}
