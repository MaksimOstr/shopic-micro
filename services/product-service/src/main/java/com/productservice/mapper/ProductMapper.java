package com.productservice.mapper;

import com.productservice.entity.Product;
import com.productservice.projection.ProductDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "brand.name", target = "brandName")
    ProductDto productToProductDto(Product product);
}
