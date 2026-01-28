package com.productservice.dto;

import java.util.UUID;

public record AdminCategoryDto(
        UUID id,
        String name,
        String description,
        Boolean active
) {
}
