package com.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank
        @Size(min = 2, max = 20)
        String name,

        @NotNull
        Boolean isActive,

        @NotBlank
        @Size(min = 2, max = 255)
        String description
) {}
