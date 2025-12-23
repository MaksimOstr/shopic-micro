package com.productservice.dto;


import com.productservice.entity.ProductStatusEnum;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
public class AdminProductDto extends BaseProductDto {
    private final ProductStatusEnum status;
    private final Instant createdAt;

    public AdminProductDto(
            UUID id,
            String productName,
            String description,
            String imageUrl,
            BigDecimal price,
            String brandName,
            String categoryName,
            ProductStatusEnum status,
            int stockQuantity,
            Instant createdAt
    ) {
        super(id, productName, description, imageUrl, stockQuantity, price, brandName, categoryName);
        this.status = status;
        this.createdAt = createdAt;
    }
}
