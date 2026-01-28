package com.paymentservice.dto;

import com.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PaymentDto(
        UUID id,
        UUID userId,
        UUID orderId,
        String stripePaymentId,
        String paymentMethod,
        String currency,
        String sessionId,
        BigDecimal amount,
        PaymentStatus status,
        Instant createdAt
) {
}
