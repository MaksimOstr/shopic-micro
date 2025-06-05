package com.productservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;


public record UpdateProductRequest(
        @Size(min = 3, max = 10)
        String name,

        @Size(max = 1000)
        String description,

        @DecimalMin("0")
        BigDecimal price,

        Long categoryId,

        @Min(0)
        Integer stockQuantity
) {}
