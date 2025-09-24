package com.productservice.dto;

import com.productservice.entity.ProductStatusEnum;

import java.math.BigDecimal;

public class ProductAdminPreviewDto extends ProductPreviewDto {
    public final ProductStatusEnum status;

    public ProductAdminPreviewDto(long id, String name, String imageUrl, BigDecimal price, BigDecimal rating, int reviewCount, ProductStatusEnum status) {
        super(id, name, imageUrl, price, rating, reviewCount);
        this.status = status;
    }
}
