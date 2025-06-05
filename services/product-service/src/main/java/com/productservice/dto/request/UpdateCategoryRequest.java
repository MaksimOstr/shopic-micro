package com.productservice.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(min = 2, max = 20)
        String name,

        @Size(min = 2, max = 255)
        String description
) {
}
