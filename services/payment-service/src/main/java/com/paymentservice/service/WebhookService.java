package com.paymentservice.service;

import com.paymentservice.entity.Payment;
import com.paymentservice.entity.PaymentStatus;
import com.paymentservice.entity.RefundStatus;
import com.paymentservice.exception.NotFoundException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    private final KafkaService kafkaService;
    private final PaymentService paymentService;
    private final RefundService refundService;

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
            case "charge.refund.updated":
                handleRefundUpdated(event);
        }
    }


    private void handleRefundUpdated(Event event) {
        Refund refund = getRefundFromEvent(event);
        log.info("RefundStatus: {}", refund.getStatus());
        switch (refund.getStatus()) {
            case "succeeded":
                handleRefundSuccess(refund);
            case "failed":
                handleRefundFailed(refund);
        }
    }

    private void handleRefundFailed(Refund refund) {
        com.paymentservice.entity.Refund refundEntity = refundService.getRefundByStripeRefundId(refund.getId());

        refundEntity.setStatus(RefundStatus.FAILED);
        refundEntity.setFailureReason(refund.getFailureReason());
    }

    private void handleRefundSuccess(Refund refund) {
        com.paymentservice.entity.Refund refundEntity = refundService.getRefundWithPaymentByStripeRefundId(refund.getId());
        Payment payment = refundEntity.getPayment();

        refundEntity.setStatus(RefundStatus.SUCCEEDED);
        refundEntity.setRefundedAt(Instant.now());

        if (payment.getAmount().subtract(payment.getTotalRefundedAmount()).compareTo(BigDecimal.ZERO) == 0) {
            payment.setStatus(PaymentStatus.FULLY_REFUNDED);
        } else {
            payment.setStatus(PaymentStatus.PARTIALLY_REFUNDED);
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
