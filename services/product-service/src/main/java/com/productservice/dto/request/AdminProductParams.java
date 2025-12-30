package com.productservice.dto.request;

import java.math.BigDecimal;
import java.util.UUID;


public record AdminProductParams(
        String productName,
        BigDecimal fromPrice,
        BigDecimal toPrice,
        UUID brandId,
        UUID categoryId,
        Boolean isDeleted
) {}
