package com.productservice.dto;

import java.util.UUID;

public record AdminBrandDto(
        UUID id,
        String name,
        Boolean active
) {
}
