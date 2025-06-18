package com.orderservice.mapper;

import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.ReservationItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GrpcMapper {
    ReservationItem toReservationItem(CartItem cartItem);
}
