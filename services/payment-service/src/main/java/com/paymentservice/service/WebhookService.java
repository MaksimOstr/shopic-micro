package com.paymentservice.service;

import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.exception.NotFoundException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final KafkaService kafkaService;
    private final PaymentService paymentService;

    @Async
    @Transactional
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

    public void handleCheckoutSuccess(Event event) {
        Session session = getSessionFromEvent(event);
        String sessionId = session.getId();
        Payment payment = paymentService.getPaymentBySessionId(sessionId);
        String intentId = session.getPaymentIntent();

        payment.setStripePaymentId(intentId);
        payment.setStatus(PaymentStatus.SUCCEEDED);
        kafkaService.sendCheckoutSessionSuccess(payment.getOrderId());
    }

    private void handleChargeFailed(Event event) {
        Session session = getSessionFromEvent(event);

        paymentService.changePaymentStatus(session.getId(), PaymentStatus.FAILED);
    }

    private Session getSessionFromEvent(Event event) {
        Optional<StripeObject> optionalSession = event.getDataObjectDeserializer().getObject();

        if (optionalSession.isPresent()) {
            return (Session) optionalSession.get();
        } else {
            log.error("Checkout session not found");
            throw new NotFoundException("Checkout session not found");
        }
    }
}
