package com.productservice.dto.event;

import java.util.UUID;

public record BaseReservationEvent(
        UUID id,
        UUID orderId
) {}
