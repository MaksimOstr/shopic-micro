package com.productservice.mapper;

import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductUserPreviewDto productToProductUserPreviewDto(Product product);

    ProductAdminPreviewDto productToProductAdminPreviewDto(Product product);
}
