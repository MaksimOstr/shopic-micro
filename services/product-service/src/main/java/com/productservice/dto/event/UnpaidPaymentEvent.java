package com.productservice.dto.event;

public record UnpaidPaymentEvent(
        long orderId
) {
}
