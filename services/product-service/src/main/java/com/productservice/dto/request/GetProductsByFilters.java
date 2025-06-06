package com.productservice.dto.request;

import java.math.BigDecimal;

public record GetProductsByFilters(
        String name,
        BigDecimal fromPrice,
        BigDecimal toPrice,
        Integer brandId,
        Integer categoryId
) {}
