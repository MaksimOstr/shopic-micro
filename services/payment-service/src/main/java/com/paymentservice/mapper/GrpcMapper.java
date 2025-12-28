package com.paymentservice.mapper;

import com.paymentservice.dto.CheckoutItem;
import com.shopic.grpc.paymentservice.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    @Mapping(target = "name", source = "itemName")
    @Mapping(target = "imageUrl", source = "itemImage")
    @Mapping(target = "price", source = "priceForOne")
    CheckoutItem toCheckoutItem(OrderItem item);

    List<CheckoutItem> toCheckoutItemList(List<OrderItem> items);
}
