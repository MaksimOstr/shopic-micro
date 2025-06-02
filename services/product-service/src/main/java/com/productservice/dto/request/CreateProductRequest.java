package com.productservice.dto.request;

import com.productservice.entity.ProductCategoryEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank
        @Size(min = 3, max = 100)
        String name,

        @NotBlank
        String description,

        @NotNull
        BigDecimal price,

        @NotBlank
        ProductCategoryEnum category,

        @NotNull
        @Min(0)
        int stockQuantity
) {}
