package com.productservice.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class AdminProductDto extends BaseProductDto {

    private final boolean enabled;
    private final Instant createdAt;

    public AdminProductDto(long id, String name, String description, String imageUrl, UUID sku, BigDecimal price, String brandName, String categoryName, boolean enabled, Instant createdAt) {
        super(id, name, description, imageUrl, sku, price, brandName, categoryName);
        this.enabled = enabled;
        this.createdAt = createdAt;
    }
}
