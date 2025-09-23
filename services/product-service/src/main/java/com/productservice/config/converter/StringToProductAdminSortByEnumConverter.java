package com.productservice.config.converter;

import com.productservice.enums.ProductAdminSortByEnum;
import org.springframework.core.convert.converter.Converter;

public class StringToProductAdminSortByEnumConverter implements Converter<String, ProductAdminSortByEnum> {
    @Override
    public ProductAdminSortByEnum convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        return ProductAdminSortByEnum.fromString(source.toUpperCase());
    }
}
