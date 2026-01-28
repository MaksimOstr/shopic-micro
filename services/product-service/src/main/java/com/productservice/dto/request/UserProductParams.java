package com.productservice.dto.request;


import java.math.BigDecimal;
import java.util.UUID;

public record UserProductParams(
        String productName,
        BigDecimal toPrice,
        BigDecimal fromPrice,
        UUID categoryId,
        UUID brandId
) {
}
