package com.productservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;


@Getter
public class UserProductDto extends BaseProductDto {

    @Setter
    private boolean isLiked;

    public UserProductDto(
            long id,
            String productName,
            String description,
            String imageUrl,
            UUID sku,
            BigDecimal price,
            String brandName,
            String categoryName
    ) {
        super(id, productName, description, imageUrl, sku, price, brandName, categoryName);
    }
}
