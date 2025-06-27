package com.paymentservice.dto;

public record CreatePaymentDto(
        long userId,
        long orderId,
        String paymentId
) {
}
