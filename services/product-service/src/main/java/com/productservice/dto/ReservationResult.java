package com.productservice.dto;

import com.productservice.entity.Product;

import java.util.List;

public record ReservationResult(
        List<Product> reservedProducts,
        List<ReservationError> errors
) {
}
