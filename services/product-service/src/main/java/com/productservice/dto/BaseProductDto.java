package com.productservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class BaseProductDto {
    private final UUID id;
    private final String productName;
    private final String description;
    private final String imageUrl;
    private final int stockQuantity;
    private final BigDecimal price;
    private final String brandName;
    private final String categoryName;

    @Setter
    private BigDecimal rating;

    @Setter
    private int reviewCount;
}
