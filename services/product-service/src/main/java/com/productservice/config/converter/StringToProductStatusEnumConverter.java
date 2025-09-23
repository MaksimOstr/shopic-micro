package com.productservice.config.converter;

import com.productservice.entity.ProductStatusEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProductStatusEnumConverter implements Converter<String, ProductStatusEnum> {
    @Override
    public ProductStatusEnum convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        return ProductStatusEnum.valueOf(source.toUpperCase());
    }
}