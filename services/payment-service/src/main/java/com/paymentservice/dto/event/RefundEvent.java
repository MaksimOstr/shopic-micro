package com.paymentservice.dto.event;

import java.math.BigDecimal;

public record RefundEvent(
        long orderId,
        BigDecimal refundAmount
) {
}
