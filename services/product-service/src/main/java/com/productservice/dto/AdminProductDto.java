package com.productservice.dto;


import com.productservice.entity.ProductStatusEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class AdminProductDto {
    private final long id;
    private final String productName;
    private final String description;
    private final String imageUrl;
    private final UUID sku;
    private final BigDecimal price;
    private final String brandName;
    private final String categoryName;
    private final ProductStatusEnum status;
    private final Instant createdAt;

    @Setter
    private boolean isLiked;

    @Setter
    private BigDecimal rating;

    @Setter
    private int reviewCount;
}
