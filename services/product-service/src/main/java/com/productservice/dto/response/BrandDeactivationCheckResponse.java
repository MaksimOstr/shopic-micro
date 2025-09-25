package com.productservice.dto.response;

public record BrandDeactivationCheckResponse(
        int brandId,
        String brandName,
        int activeProductsCount,
        String message
) {
}
