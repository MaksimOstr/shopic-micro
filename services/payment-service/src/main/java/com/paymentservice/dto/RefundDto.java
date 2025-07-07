package com.paymentservice.dto;

import com.paymentservice.entity.RefundReason;
import com.paymentservice.entity.RefundStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record RefundDto(
        long id,
        long paymentId,
        RefundStatus status,
        String currency,
        BigDecimal amount,
        RefundReason reason,
        String stripeRefundId,
        String description,
        String failureReason,
        Instant createdAt,
        Instant updatedAt,
        Instant refundedAt
) {}
