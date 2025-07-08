package com.cartservice.mapper;

import com.cartservice.projection.CartItemForOrderProjection;
import com.shopic.grpc.cartservice.CartItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    CartItem toOrderCartItem(CartItemForOrderProjection cartItem);

    List<CartItem> toOrderCartItems(List<CartItemForOrderProjection> cartItems);
}
