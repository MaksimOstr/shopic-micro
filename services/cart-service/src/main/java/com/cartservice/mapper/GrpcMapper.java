package com.cartservice.mapper;

import com.cartservice.projection.CartItemForOrderProjection;
import com.shopic.grpc.cartservice.OrderCartItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    OrderCartItem toOrderCartItem(CartItemForOrderProjection cartItem);
}
