package com.cartservice.mapper;

import com.cartservice.dto.CartDto;
import com.cartservice.entity.Cart;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartMapper {
    CartDto toDto(Cart cart);
}
