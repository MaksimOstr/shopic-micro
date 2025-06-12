package com.productservice.mapper;

import com.productservice.projection.ProductForCartDto;
import com.shopic.grpc.productservice.CartItemAddGrpcResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    CartItemAddGrpcResponse toCartItemAddGrpcResponse(ProductForCartDto dto);
}
