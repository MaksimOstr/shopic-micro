package com.productservice.dto;

import java.util.UUID;

public record UserBrandDto(
        UUID id,
        String name
) {
}
