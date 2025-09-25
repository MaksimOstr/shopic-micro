package com.productservice.dto.response;

public record CategoryDeactivationResponse(
        int categoryId,
        String categoryName,
        int deactivatedProductCount,
        String message
) {
}
