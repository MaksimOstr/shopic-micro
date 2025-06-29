package com.paymentservice.controller;

import com.paymentservice.service.WebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String STRIPE_WEBHOOK_SECRET;

    private final WebhookService webhookService;


    @PostMapping("/webhook")
    public String handleWebhook(
            String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);

            webhookService.handleWebhookEvent(event);

            return "OK";
        } catch (SignatureVerificationException e) {
            return "Error: " + e.getMessage();
        }
    };
}
