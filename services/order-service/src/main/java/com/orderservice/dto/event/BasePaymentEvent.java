package com.orderservice.dto.event;

import java.util.UUID;

public record BasePaymentEvent (
        UUID orderId
) {}
