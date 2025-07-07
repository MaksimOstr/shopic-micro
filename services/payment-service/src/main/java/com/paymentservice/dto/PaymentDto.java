package com.paymentservice.dto;

import com.paymentservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record PaymentDto(
        long id,
        long userId,
        long orderId,
        String stripePaymentId,
        String paymentMethod,
        String currency,
        String sessionId,
        BigDecimal amount,
        PaymentStatus status,
        Instant createdAt,
        List<RefundSummaryDto> refunds
) {}
