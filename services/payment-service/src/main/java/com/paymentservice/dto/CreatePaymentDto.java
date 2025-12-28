package com.paymentservice.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentDto(
        UUID userId,
        UUID orderId,
        String sessionId,
        BigDecimal amount,
        Long totalInSmallestUnit
) {
}
