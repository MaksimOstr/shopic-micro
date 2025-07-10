package com.productservice.projection;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ProductDto {
    private final long id;
    private final String name;
    private final String description;
    private final String imageUrl;
    private final UUID sku;
    private final BigDecimal price;
    private final String brandName;
    private final String categoryName;
    private final int stockQuantity;
    private final boolean enabled;

    @Setter
    private boolean isLiked;

    @Setter
    private BigDecimal rating;

    @Setter
    private int reviewCount;
}
