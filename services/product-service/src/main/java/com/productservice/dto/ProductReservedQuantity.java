package com.productservice.dto;

import java.util.UUID;

public record ProductReservedQuantity(
        UUID productId,
        Integer reservedQuantity
) {
}
