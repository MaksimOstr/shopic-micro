package com.orderservice.mapper;

import com.orderservice.dto.OrderItemDto;
import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.shopic.grpc.productservice.Product;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {
    OrderItemDto toOrderItemDto(OrderItem orderItem);

    default List<OrderItem> toOrderItemList(
            Order order,
            List<Product> productInfoList,
            Map<Long, Integer> productQuantityMap
    ) {
        return productInfoList.stream()
                .map(productInfo -> toOrderItem(order, productInfo, productQuantityMap))
                .toList();
    }

    @Mapping(target = "priceAtPurchase", source = "productInfo.price")
    @Mapping(target = "order", source = "order")
    @Mapping(target = "quantity", expression = "java(productQuantityMap.get(productInfo.getProductId()))")
    OrderItem toOrderItem(
            Order order,
            Product productInfo,
            @Context Map<Long, Integer> productQuantityMap
    );
}
