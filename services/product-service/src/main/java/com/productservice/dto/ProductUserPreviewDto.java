package com.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
public class ProductUserPreviewDto extends ProductPreviewDto {
    @Setter
    private boolean isLiked;

    public ProductUserPreviewDto(long id, String name, String imageUrl, BigDecimal price, BigDecimal rating, int reviewCount) {
        super(id, name, imageUrl, price, rating, reviewCount);
    }

}
