package com.cartservice.mapper;

import com.cartservice.dto.CartItemDto;
import com.cartservice.dto.CartItemDtoForOrder;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
    CartItemDto toDto(CartItem cartItem);

    List<CartItemDto> toDtoList(List<CartItem> cartItems);
}
