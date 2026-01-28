package com.paymentservice.dto;

import java.util.UUID;

public record BasePaymentEvent(
        UUID orderId
) {}
