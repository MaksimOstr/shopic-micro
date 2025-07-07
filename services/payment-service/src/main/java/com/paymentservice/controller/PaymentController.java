package com.paymentservice.controller;

import com.paymentservice.dto.request.FullRefundRequest;
import com.paymentservice.dto.request.PartialRefundRequest;
import com.paymentservice.service.StripeRefundService;
import com.paymentservice.service.WebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String STRIPE_WEBHOOK_SECRET;

    private final WebhookService webhookService;
    private final StripeRefundService stripeRefundService;

    @PostMapping("/{orderId}/full-refund")
    public ResponseEntity<Void> fullRefund(
            @PathVariable("orderId") long orderId,
            @RequestBody @Valid FullRefundRequest body
    ) {
        stripeRefundService.processFullRefund(orderId, body.reason(), body.description());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/partial-refund")
    public ResponseEntity<Void> partialRefund(
            @PathVariable("orderId") long orderId,
            @RequestBody @Valid PartialRefundRequest body
    ) {
        stripeRefundService.processPartialRefund(orderId, body.reason(), body.amount(), body.description());

        return ResponseEntity.ok().build();
    }


    @PostMapping("/webhook")
    public String handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);
            System.out.println(event.getType());
            webhookService.handleWebhookEvent(event);

            return "OK";
        } catch (SignatureVerificationException e) {
            return "Error: " + e.getMessage();
        }
    };
}
