package com.productservice.config;

import com.productservice.config.converter.StringToProductAdminSortByEnumConverter;
import com.productservice.config.converter.StringToProductStatusEnumConverter;
import com.productservice.config.converter.StringToSortDirectionEnumConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToProductStatusEnumConverter());
        registry.addConverter(new StringToProductAdminSortByEnumConverter());
        registry.addConverter(new StringToSortDirectionEnumConverter());
    }
}
