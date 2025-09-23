package com.productservice.config.converter;

import com.productservice.enums.SortDirectionEnum;
import org.springframework.core.convert.converter.Converter;

public class StringToSortDirectionEnumConverter implements Converter<String, SortDirectionEnum> {
    @Override
    public SortDirectionEnum convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        return SortDirectionEnum.fromString(source.toUpperCase());
    }
}
