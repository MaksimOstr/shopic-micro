package com.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public abstract class ProductReviewDto {
    private final long id;
    private final String name;
    private final String imageUrl;
    private final BigDecimal price;

    @Setter
    private boolean isLiked;

    @Setter
    private BigDecimal rating;

    @Setter
    private int reviewCount;

    public ProductReviewDto(long id, String name, String imageUrl, BigDecimal price, boolean isLiked, BigDecimal rating, int reviewCount) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.isLiked = isLiked;
        this.rating = rating;
        this.reviewCount = reviewCount;
    }
}
