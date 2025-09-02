package com.productservice.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public abstract class BaseProductDto {
    protected final long id;
    protected final String name;
    protected final String description;
    protected final String imageUrl;
    protected final UUID sku;
    protected final BigDecimal price;
    protected final String brandName;
    protected final String categoryName;

    @Setter
    protected boolean isLiked;

    @Setter
    protected BigDecimal rating;

    @Setter
    protected int reviewCount;
}
