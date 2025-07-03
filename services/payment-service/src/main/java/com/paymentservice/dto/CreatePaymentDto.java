package com.paymentservice.dto;

import java.math.BigDecimal;

public record CreatePaymentDto(
        long userId,
        long orderId,
        String sessionId,
        String currency,
        BigDecimal amount,
        Long totalInSmallestUnit
) {
}
