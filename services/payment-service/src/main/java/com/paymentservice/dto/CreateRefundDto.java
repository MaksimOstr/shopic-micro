package com.paymentservice.dto;

import com.paymentservice.entity.Payment;
import com.paymentservice.entity.RefundReason;

import java.math.BigDecimal;

public record CreateRefundDto(
        Payment payment,
        String currency,
        BigDecimal amount,
        RefundReason reason,
        String stripeRefundId
) {
}
