package com.paymentservice.controller;

import com.paymentservice.dto.request.ChargeRequest;
import com.stripe.model.Charge;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    @PostMapping
    public ResponseEntity<?> charge(
            @RequestBody @Valid ChargeRequest body
    ) {
        return ResponseEntity.ok().build();
    }
}
