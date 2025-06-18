package com.productservice.mapper;

import com.productservice.projection.ProductForCartDto;
import com.productservice.projection.ProductForOrderDto;
import com.shopic.grpc.productservice.ProductDetailsResponse;
import com.shopic.grpc.productservice.ProductInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    ProductDetailsResponse toCartItemAddGrpcResponse(ProductForCartDto dto);

    @Mapping(target = "availableQuantity", source = "stockQuantity")
    ProductInfo toProductInfo(ProductForOrderDto dto);
}
