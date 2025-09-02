package com.productservice.dto;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class ProductUserPreviewDto extends ProductReviewDto {

    public ProductUserPreviewDto(long id, String name, String imageUrl, BigDecimal price, boolean isLiked, BigDecimal rating, int reviewCount) {
        super(id, name, imageUrl, price,  isLiked, rating, reviewCount);
    }
}
