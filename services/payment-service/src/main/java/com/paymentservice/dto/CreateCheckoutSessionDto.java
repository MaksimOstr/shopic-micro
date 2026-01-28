package com.paymentservice.dto;

import java.util.List;
import java.util.UUID;

public record CreateCheckoutSessionDto (
        UUID orderId,
        UUID userId,
        List<CheckoutItem> checkoutItems
) {}
