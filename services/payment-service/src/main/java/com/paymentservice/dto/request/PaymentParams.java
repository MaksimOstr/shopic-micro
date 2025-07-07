package com.paymentservice.dto.request;

import com.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentParams(
        Long userId,
        String paymentMethod,
        String currency,
        BigDecimal amountTo,
        BigDecimal amountFrom,
        PaymentStatus status
) {
}
