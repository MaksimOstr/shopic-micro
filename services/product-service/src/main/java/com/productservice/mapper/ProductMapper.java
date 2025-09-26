package com.productservice.mapper;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.ProductAdminPreviewDto;
import com.productservice.dto.ProductUserPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductUserPreviewDto productToProductUserPreviewDto(Product product);

    ProductAdminPreviewDto productToProductAdminPreviewDto(Product product);

    List<ProductUserPreviewDto> productToProductUserPreviewDtoList(List<Product> products);

    List<ProductAdminPreviewDto> productToProductAdminPreviewDtoList(List<Product> products);

    @Mapping(target = "brandName", source = "product.brand.name")
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "productName", source = "product.name")
    AdminProductDto productToAdminProductDto(Product product);
}
