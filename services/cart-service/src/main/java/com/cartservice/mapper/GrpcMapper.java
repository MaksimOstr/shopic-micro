package com.cartservice.mapper;

import com.cartservice.dto.CartItemDto;
import com.shopic.grpc.cartservice.CartItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrpcMapper {
    List<CartItem> toOrderCartItems(List<CartItemDto> cartItems);
}
