package com.productservice.dto;

import java.util.UUID;

public record UserCategoryDto(
        UUID id,
        String name
) {
}
