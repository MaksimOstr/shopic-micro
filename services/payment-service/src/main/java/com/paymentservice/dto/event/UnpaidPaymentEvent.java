package com.paymentservice.dto.event;

public record UnpaidPaymentEvent(
        long orderId
) {}
