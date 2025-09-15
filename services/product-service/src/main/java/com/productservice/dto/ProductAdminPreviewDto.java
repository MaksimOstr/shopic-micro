package com.productservice.dto;

import java.math.BigDecimal;

public class ProductAdminPreviewDto extends ProductPreviewDto {
    public ProductAdminPreviewDto(long id, String name, String imageUrl, BigDecimal price, boolean isLiked, BigDecimal rating, int reviewCount) {
        super(id, name, imageUrl, price, isLiked, rating, reviewCount);
    }
}
