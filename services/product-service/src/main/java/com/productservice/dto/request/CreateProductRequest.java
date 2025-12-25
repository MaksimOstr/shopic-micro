package com.productservice.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

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
        UUID categoryId,

        @NotNull
        UUID brandId,

        @NotNull
        @Min(0)
        Integer stockQuantity
) {}