package com.productservice.dto.response;

public record BrandDeactivationResponse(
        int brandId,
        String brandName,
        int deactivatedProductCount,
        String message
) {
}
