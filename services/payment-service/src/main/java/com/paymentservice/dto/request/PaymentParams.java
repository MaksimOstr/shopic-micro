package com.paymentservice.dto.request;

import com.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentParams(
        Long userId,
        String paymentMethod,
        String currency,
        BigDecimal amountTo,
        BigDecimal amountFrom,
        PaymentStatus status
) {
}
