package com.orderservice.mapper;

import com.orderservice.dto.request.CreateOrderItem;
import com.orderservice.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    @Mapping(target = "order.id", source = "orderId")
    @Mapping(target = "priceAtPurchase", source = "price")
    OrderItem toOrderItem(CreateOrderItem dto);
}
