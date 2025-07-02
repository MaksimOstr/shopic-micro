package com.paymentservice.service;

import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.exception.NotFoundException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
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
            case "charge.failed":
                handleChargeFailed(event);
                break;
        }
    }

    private void handleCheckoutSuccess(Event event) {
        String sessionId = getSessionIdFromEvent(event);

        long orderId = paymentService.getOrderIdByPaymentId(sessionId);

        paymentService.changePaymentStatus(sessionId, PaymentStatus.SUCCEEDED);
        kafkaService.sendCheckoutSessionSuccess(orderId);
    }

    private void handleChargeFailed(Event event) {
        String sessionId = getSessionIdFromEvent(event);

        paymentService.changePaymentStatus(sessionId, PaymentStatus.FAILED);
    }

    private String getSessionIdFromEvent(Event event) {
        Optional<StripeObject> optionalSession = event.getDataObjectDeserializer().getObject();

        if (optionalSession.isPresent()) {
            Session session = (Session) optionalSession.get();
            return session.getId();
        } else {
            log.error("Checkout session not found");
            throw new NotFoundException("Checkout session not found");
        }
    }
}
