package com.productservice.dto.request;

import com.productservice.entity.ProductStatusEnum;
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
        ProductStatusEnum status,

        @NotNull
        @DecimalMin("0")
        BigDecimal price,

        @NotNull
        Integer categoryId,

        @NotNull
        Integer brandId,

        @NotNull
        @Min(0)
        Integer stockQuantity
) {}