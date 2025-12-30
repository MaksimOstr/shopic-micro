package com.productservice.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Getter
@Setter
public class UserProductPreviewDto {
    private final UUID id;
    private final String name;
    private final String imageUrl;
    private final BigDecimal price;
    private final String categoryName;
    private final String brandName;
    private boolean isLiked;
}
