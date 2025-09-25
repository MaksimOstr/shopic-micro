package com.productservice.dto.response;

public record CategoryDeactivationCheckResponse(
        int categoryId,
        String categoryName,
        int activeProductsCount,
        String message
) {
}
