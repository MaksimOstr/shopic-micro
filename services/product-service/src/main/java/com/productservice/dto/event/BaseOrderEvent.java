package com.productservice.dto.event;

import java.util.UUID;

public record BaseOrderEvent(
        UUID orderId
) {
}
