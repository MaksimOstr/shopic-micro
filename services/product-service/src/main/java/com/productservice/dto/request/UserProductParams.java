package com.productservice.dto.request;

import com.productservice.entity.ProductStatusEnum;

import java.math.BigDecimal;

public record UserProductParams(
        String productName,
        ProductStatusEnum status,
        BigDecimal toPrice,
        BigDecimal fromPrice,
        Long categoryId,
        Long brandId
) {
}
