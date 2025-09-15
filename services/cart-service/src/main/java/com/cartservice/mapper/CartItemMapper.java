package com.cartservice.mapper;

import com.cartservice.dto.CartItemDto;
import com.cartservice.dto.CartItemDtoForOrder;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.shopic.grpc.productservice.ProductInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CartItemMapper {


    CartItemDtoForOrder toCartItemDtoForOrder(CartItem cartItem);

    List<CartItemDtoForOrder> toCartItemDtoListForOrder(List<CartItem> cartItems);

    CartItemDto toCartItemDto(CartItem cartItem);

    List<CartItemDto> toCartItemDtoList(List<CartItem> cartItems);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "productId", source = "dto.productId")
    @Mapping(target = "cart", source = "cart")
    CartItem toEntity(AddItemToCartRequest dto, ProductInfo productInfo, Cart cart);

    @AfterMapping
    default void setPriceAtAdd(@MappingTarget CartItem cartItem, ProductInfo productInfo) {
        cartItem.setPriceAtAdd(new BigDecimal(productInfo.getPrice()));
    }
}
