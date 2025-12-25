package com.productservice.dto.request;

import com.productservice.entity.ProductStatusEnum;
import lombok.*;

import java.math.BigDecimal;


public record AdminProductParams(
        String productName,
        BigDecimal fromPrice,
        BigDecimal toPrice,
        Integer brandId,
        Integer categoryId,
        Boolean isDeleted
) {}
