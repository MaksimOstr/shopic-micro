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
    List<com.shopic.grpc.paymentservice.OrderItem> toGrpcOrderItems(List<OrderItem> orderItemList);

    default List<OrderItem> toOrderItemList(
            List<Product> productInfoList,
            Map<String, Integer> productQuantityMap
    ) {
        return productInfoList.stream()
                .map(productInfo -> toOrderItem(productInfo, productQuantityMap))
                .toList();
    }

    @Mapping(target = "priceAtPurchase", source = "productInfo.price")
    @Mapping(target = "quantity", expression = "java(productQuantityMap.get(productInfo.getProductId()))")
    OrderItem toOrderItem(
            Product productInfo,
            @Context Map<String, Integer> productQuantityMap
    );
}
