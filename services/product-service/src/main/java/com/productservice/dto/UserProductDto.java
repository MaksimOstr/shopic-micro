package com.productservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
@RequiredArgsConstructor
public class UserProductDto {
    private final long id;
    private final String productName;
    private final String description;
    private final String imageUrl;
    private final UUID sku;
    private final BigDecimal price;
    private final String brandName;
    private final String categoryName;

    @Setter
    private boolean isLiked;

    @Setter
    private BigDecimal rating;

    @Setter
    private int reviewCount;
}
