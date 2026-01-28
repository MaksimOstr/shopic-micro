package com.orderservice.dto.event;

import java.util.UUID;

public record BasicOrderEvent(
        UUID orderId
) {}
