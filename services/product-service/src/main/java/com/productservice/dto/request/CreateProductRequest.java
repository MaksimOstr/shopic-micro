package com.productservice.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String name,

        @NotBlank
        @Size(max = 1000)
        String description,

        @NotNull
        @DecimalMin("0")
        BigDecimal price,

        @NotNull
        long categoryId,

        @NotNull
        @Min(0)
        int stockQuantity
) {}