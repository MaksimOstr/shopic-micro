package com.paymentservice.service;

import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.entity.RefundStatus;
import com.paymentservice.exception.NotFoundException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final KafkaService kafkaService;
    private final PaymentService paymentService;
    private final RefundService refundService;

    @Async
    public void handleWebhookEvent(Event event) {
        switch (event.getType()) {
            case "checkout.session.completed":
                handleCheckoutSuccess(event);
                break;
            case "charge.failed":
                handleChargeFailed(event);
                break;
            case "charge.refund.updated":
                handleRefundUpdated(event);
        }
    }


    private void handleRefundUpdated(Event event) {
        Refund refund = getRefundFromEvent(event);

        switch (refund.getStatus()) {
            case "succeeded":
                handleRefundSuccess(event, refund);
        }
    }

    private void handleRefundSuccess(Event event, Refund refund) {
        com.paymentservice.entity.Refund refundEntity = refundService.getRefundByStripeRefundId(refund.getId());

        refundEntity.setStatus(RefundStatus.SUCCEEDED);
        refundEntity.getPayment().setStatus(PaymentStatus.SUCCEEDED);
    }

    public void handleCheckoutSuccess(Event event) {
        Session session = getSessionFromEvent(event);
        String sessionId = session.getId();
        Payment payment = paymentService.getPaymentBySessionId(sessionId);

        payment.setStripePaymentId(session.getPaymentIntent());
        payment.setStatus(PaymentStatus.SUCCEEDED);

        paymentService.save(payment);
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

    private Refund getRefundFromEvent(Event event) {
        Optional<StripeObject> optionalSession = event.getDataObjectDeserializer().getObject();
        if (optionalSession.isPresent()) {
            return (Refund) optionalSession.get();
        } else {
            log.error("Refund not found");
            throw new NotFoundException("Refund not found");
        }
    }
}
