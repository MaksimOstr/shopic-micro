package com.productservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public record UpdateProductRequest(
        @NotBlank
        @Size(min = 3, max = 10)
        String name,

        @Size(max = 1000)
        String description,

        @DecimalMin("0")
        BigDecimal price,

        @NotBlank
        String category,

        @Min(0)
        Integer stockQuantity
) {}
