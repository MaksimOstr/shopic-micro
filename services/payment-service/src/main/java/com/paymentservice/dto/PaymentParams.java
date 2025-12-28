package com.paymentservice.dto;

import com.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentParams(
        UUID userId,
        String paymentMethod,
        String currency,
        BigDecimal amountTo,
        BigDecimal amountFrom,
        PaymentStatus status
) {
}
