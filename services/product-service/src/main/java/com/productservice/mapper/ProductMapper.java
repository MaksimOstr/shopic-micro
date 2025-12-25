package com.productservice.mapper;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.AdminProductPreviewDto;
import com.productservice.dto.UserProductPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    UserProductPreviewDto toProductUserPreviewDto(Product product);

    AdminProductPreviewDto toProductAdminPreviewDto(Product product);

    List<UserProductPreviewDto> toProductUserPreviewDtoList(List<Product> products);

    List<AdminProductPreviewDto> toProductAdminPreviewDtoList(List<Product> products);

    @Mapping(target = "brandName", source = "product.brand.name")
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "productName", source = "product.name")
    AdminProductDto toAdminProductDto(Product product);

    UserProductDto toUserProductDto(Product product);

    LikedProductDto toLikedProductDto(Product product);

    List<LikedProductDto> toLikedProductDtoList(List<Product> products);

    com.shopic.grpc.productservice.Product toGrpcProduct(Product product);

    List<com.shopic.grpc.productservice.Product> toGrpcProductList(List<Product> products);
}
