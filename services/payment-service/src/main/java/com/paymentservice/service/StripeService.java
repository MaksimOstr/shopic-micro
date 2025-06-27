package com.paymentservice.service;

import com.paymentservice.dto.request.ChargeRequest;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StripeService {
    private final PaymentService paymentService;

    public String createCheckoutSession(ChargeRequest dto) throws StripeException {

    }
}
