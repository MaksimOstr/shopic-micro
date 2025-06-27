package com.paymentservice.dto;

import java.util.List;

public record CreateCheckoutSessionDto (
        long orderId,
        long userId,
        List<CheckoutItem> checkoutItems
) {}
