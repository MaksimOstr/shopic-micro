package com.productservice.mapper;

import com.productservice.dto.AdminProductDto;
import com.productservice.dto.LikedProductDto;
import com.productservice.dto.AdminProductPreviewDto;
import com.productservice.dto.UserProductPreviewDto;
import com.productservice.dto.UserProductDto;
import com.productservice.entity.Product;
import com.shopic.grpc.productservice.ReservedProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "liked", ignore = true)
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "category.name", target = "categoryName")
    UserProductPreviewDto toProductUserPreviewDto(Product product);

    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "category.name", target = "categoryName")
    AdminProductPreviewDto toProductAdminPreviewDto(Product product);

    List<UserProductPreviewDto> toProductUserPreviewDtoList(List<Product> products);

    List<AdminProductPreviewDto> toProductAdminPreviewDtoList(List<Product> products);

    @Mapping(target = "brandName", source = "product.brand.name")
    @Mapping(target = "categoryName", source = "product.category.name")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "categoryId", source = "product.category.id")
    @Mapping(target = "brandId", source = "product.brand.id")
    AdminProductDto toAdminProductDto(Product product);

    @Mapping(target = "liked", ignore = true)
    @Mapping(source = "brand.name", target = "brandName")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(target = "productName", source = "name")
    UserProductDto toUserProductDto(Product product);

    LikedProductDto toLikedProductDto(Product product);

    List<LikedProductDto> toLikedProductDtoList(List<Product> products);

    ReservedProduct toReservedProduct(Product product);

    List<ReservedProduct> toReservedProductList(List<Product> products);

    @Mapping(target = "availableQuantity", source = "stockQuantity")
    com.shopic.grpc.productservice.Product toGrpcProduct(Product product);
}
