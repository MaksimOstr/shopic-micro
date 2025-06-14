package com.productservice.dto.request;

import lombok.Getter;
import java.math.BigDecimal;

@Getter
public class ProductParams {
    private final String name;
    private final BigDecimal fromPrice;
    private final BigDecimal toPrice;
    private final Integer brandId;
    private final Integer categoryId;



    public ProductParams(String name,
                                BigDecimal fromPrice,
                                BigDecimal toPrice,
                                Integer brandId,
                                Integer categoryId) {
        this.name = name;
        this.fromPrice = fromPrice;
        this.toPrice = toPrice;
        this.brandId = brandId;
        this.categoryId = categoryId;
    }

}
