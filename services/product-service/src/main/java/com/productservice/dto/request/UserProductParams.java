package com.productservice.dto.request;


import java.math.BigDecimal;

public record UserProductParams(
        String productName,
        BigDecimal toPrice,
        BigDecimal fromPrice,
        Long categoryId,
        Long brandId
) {
}
