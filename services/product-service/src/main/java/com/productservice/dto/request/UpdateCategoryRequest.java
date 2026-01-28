package com.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(
        @Size(min = 2, max = 20)
        @NotBlank
        String name,

        @Size(min = 2, max = 255)
        @NotBlank
        String description,

        @NotNull
        Boolean isActive
) {
}
