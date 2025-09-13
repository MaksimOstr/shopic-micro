package com.cartservice.mapper;

import com.cartservice.dto.CartItemDto;
import com.cartservice.dto.CartItemDtoForOrder;
import com.cartservice.entity.CartItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {


    CartItemDtoForOrder toCartItemDtoForOrder(CartItem cartItem);

    List<CartItemDtoForOrder> toCartItemDtoListForOrder(List<CartItem> cartItems);

    CartItemDto toCartItemDto(CartItem cartItem);

    List<CartItemDto> toCartItemDtoList(List<CartItem> cartItems);
}
