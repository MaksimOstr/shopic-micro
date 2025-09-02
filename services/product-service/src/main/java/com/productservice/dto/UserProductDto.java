package com.productservice.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class UserProductDto extends BaseProductDto {
    public UserProductDto(long id, String name, String description, String imageUrl, UUID sku, BigDecimal price, String brandName, String categoryName) {
        super(id, name, description, imageUrl, sku, price, brandName, categoryName);
    }
}
