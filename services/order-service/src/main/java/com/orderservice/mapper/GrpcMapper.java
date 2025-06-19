package com.orderservice.mapper;

import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.ReservationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GrpcMapper {
    @Mapping(target = "productId", source = "productId")
    @Mapping(target = "quantity", source = "quantity")
    ReservationItem toReservationItem(CartItem cartItem);
}
