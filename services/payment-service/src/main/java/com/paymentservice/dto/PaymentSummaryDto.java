package com.paymentservice.dto;

import com.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentSummaryDto(
        long id,
        long userId,
        long orderId,
        String stripePaymentId,
        String paymentMethod,
        String currency,
        String sessionId,
        BigDecimal amount,
        PaymentStatus status,
        Instant createdAt
) {}
