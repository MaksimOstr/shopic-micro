package com.productservice.projection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ProductDto {
    private final long productId;
    private final String name;
    private final String description;
    private final UUID sku;
    private final BigDecimal price;
    private final String brandName;
    private final String categoryName;
    private final int stockQuantity;

    @Setter
    private boolean isLiked;
}
