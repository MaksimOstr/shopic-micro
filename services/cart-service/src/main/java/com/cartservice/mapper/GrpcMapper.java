package com.cartservice.mapper;

import com.cartservice.dto.CartItemDtoForOrder;
import com.shopic.grpc.cartservice.CartItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    CartItem toOrderCartItem(CartItemDtoForOrder cartItem);

    List<CartItem> toOrderCartItems(List<CartItemDtoForOrder> cartItems);
}
