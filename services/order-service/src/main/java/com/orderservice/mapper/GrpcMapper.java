package com.orderservice.mapper;

import com.orderservice.entity.OrderItem;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.Product;
import com.shopic.grpc.productservice.ReservationItem;
import com.shopic.grpc.productservice.ReservedProduct;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GrpcMapper {
    @Mapping(target = "allFields", ignore = true)
    ReservationItem toReservationItem(CartItem cartItem);

    List<ReservationItem> toReservationItemList(List<CartItem> cartItemList);

    default Map<String, CartItem> getCartItemMap(List<CartItem> cartItems) {
        return cartItems.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        Function.identity()
                ));
    }

    default List<com.orderservice.entity.OrderItem> toOrderItemList(
            List<ReservedProduct> productList,
            Map<String, CartItem> cartItemMap
    ) {
        return productList.stream()
                .map(product -> toOrderItem(product, cartItemMap))
                .toList();
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "priceAtPurchase", source = "product.price")
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.imageUrl")
    @Mapping(target = "quantity", expression = "java(cartItemMap.get(product.getId()).getQuantity())")
    OrderItem toOrderItem(
            ReservedProduct product,
            @Context Map<String, CartItem> cartItemMap
    );
}
