package com.cartservice.mapper;

import com.cartservice.dto.CartDto;
import com.cartservice.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {


    @Mapping(target = "cartItemList", source = "cartItems")
    @Mapping(target = "totalPrice", expression = "java(cart.calculateTotal())")
    CartDto toDto(Cart cart);

}
