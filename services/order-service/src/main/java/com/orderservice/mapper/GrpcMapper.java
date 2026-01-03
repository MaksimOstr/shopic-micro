package com.orderservice.mapper;

import com.orderservice.entity.OrderItem;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.Product;
import com.shopic.grpc.productservice.ReservationItem;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GrpcMapper {
    @Mapping(target = "allFields", ignore = true)
    ReservationItem toReservationItem(CartItem cartItem);

    List<ReservationItem> toReservationItemList(List<CartItem> cartItemList);

    default Map<String, Integer> getProductQuantityMap(List<CartItem> cartItems) {
        return cartItems.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        CartItem::getQuantity
                ));
    }

    default List<com.orderservice.entity.OrderItem> toOrderItemList(
            List<Product> productInfoList,
            Map<String, Integer> productQuantityMap
    ) {
        return productInfoList.stream()
                .map(productInfo -> toOrderItem(productInfo, productQuantityMap))
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceAtPurchase", source = "product.price")
    @Mapping(target = "quantity", expression = "java(productQuantityMap.get(product.getId()))")
    OrderItem toOrderItem(
            Product product,
            @Context Map<String, Integer> productQuantityMap
    );
}
