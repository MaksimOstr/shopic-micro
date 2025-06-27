package com.orderservice.mapper;

import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.paymentservice.OrderLineItem;
import com.shopic.grpc.productservice.ReservationItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    OrderLineItem toOrderLineItem(CartItem cartItem, BigDecimal priceForOne);

    @AfterMapping
    default void afterMapping(@MappingTarget OrderLineItem.Builder orderLineItemBuilder, BigDecimal priceForOne) {
        orderLineItemBuilder.setUnitAmountInCents(priceForOne.toString());
    }

}
