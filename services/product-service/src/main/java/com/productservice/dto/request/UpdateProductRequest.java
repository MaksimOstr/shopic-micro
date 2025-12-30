package com.productservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;


public record UpdateProductRequest(
        String name,

        @Size(max = 1000)
        String description,

        Boolean deleted,

        @DecimalMin("0")
        BigDecimal price,

        UUID categoryId,

        UUID brandId,

        @Min(0)
        Integer stockQuantity
) {}
