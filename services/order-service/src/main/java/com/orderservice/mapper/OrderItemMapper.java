package com.orderservice.mapper;

import com.orderservice.entity.OrderItem;
import com.shopic.grpc.productservice.Product;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "priceForOne", source = "priceAtPurchase")
    @Mapping(target = "itemImage", source = "productImageUrl")
    @Mapping(target = "itemName", source = "productName")
    com.shopic.grpc.paymentservice.OrderItem toGrpcOrderItem(OrderItem orderItem);

    List<com.shopic.grpc.paymentservice.OrderItem> toGrpcOrderItems(List<OrderItem> orderItemList);
}
