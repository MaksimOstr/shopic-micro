package com.productservice.mapper;

import com.productservice.projection.ProductPriceAndQuantityDto;
import com.shopic.grpc.productservice.CartItemAddGrpcResponse;
import com.shopic.grpc.productservice.ProductInfoForOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    CartItemAddGrpcResponse toCartItemAddGrpcResponse(ProductPriceAndQuantityDto dto);

    @Mapping(target = "availableQuantity", source = "stockQuantity")
    ProductInfoForOrder toProductInfoForOrder(ProductPriceAndQuantityDto dto);
}
