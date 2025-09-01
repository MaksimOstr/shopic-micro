package com.paymentservice.controller;

import com.paymentservice.service.WebhookService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeController {

    @Value("${STRIPE_WEBHOOK_SECRET}")
    private String STRIPE_WEBHOOK_SECRET;

    private final WebhookService webhookService;


    @PostMapping("/webhook")
    public String handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, STRIPE_WEBHOOK_SECRET);
            webhookService.handleWebhookEvent(event);

            return "OK";
        } catch (SignatureVerificationException e) {
            log.error(e.getMessage());
            return "Error: " + e.getMessage();
        }
    };
}
