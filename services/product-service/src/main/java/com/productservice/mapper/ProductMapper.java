package com.productservice.mapper;

import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.entity.Product;
import org.mapstruct.Mapper;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductUserPreviewDto productToProductUserPreviewDto(Product product);

    ProductAdminPreviewDto productToProductAdminPreviewDto(Product product);

    List<ProductUserPreviewDto> productToProductUserPreviewDtoList(List<Product> products);

    List<ProductAdminPreviewDto> productToProductAdminPreviewDtoList(List<Product> products);
}
