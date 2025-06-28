package com.paymentservice.mapper;

import com.paymentservice.dto.CheckoutItem;
import com.shopic.grpc.paymentservice.OrderLineItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    @Mapping(target = "name", source = "productName")
    @Mapping(target = "imageUrl", source = "productImage")
    @Mapping(target = "price", source = "priceForOne")
    CheckoutItem toCheckoutItem(OrderLineItem item);
}
