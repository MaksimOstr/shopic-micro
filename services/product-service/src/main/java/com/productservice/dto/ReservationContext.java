package com.productservice.dto;

import com.productservice.entity.Product;

import java.util.Map;
import java.util.UUID;

public record ReservationContext(
        Map<UUID, Product> productMap,
        Map<UUID, Long> reservedMap
) {}