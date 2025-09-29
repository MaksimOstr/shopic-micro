package com.orderservice.mapper;

import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.paymentservice.OrderLineItem;
import com.shopic.grpc.productservice.ProductInfo;
import com.shopic.grpc.productservice.ReservationItem;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface GrpcMapper {

    @Mapping(target = "allFields", ignore = true)
    @Mapping(target = "priceForOne", source = "price")
    @Mapping(target = "itemImage", source = "productImageUrl")
    @Mapping(target = "itemName", source = "productName")
    @Mapping(target = "quantity", expression = "java(productQuantityMap.get(productInfo.getProductId()))")
    OrderLineItem toOrderLineItem(
            ProductInfo productInfo,
            @Context Map<Long, Integer> productQuantityMap
    );

    List<OrderLineItem> toOrderLineItemList(List<ProductInfo> productInfoList, @Context Map<Long, Integer> productQuantityMap);


    @Mapping(target = "allFields", ignore = true)
    ReservationItem toReservationItem(CartItem cartItem);

    List<ReservationItem> toReservationItemList(List<CartItem> cartItemList);

    default Map<Long, Integer> getProductQuantityMap(List<CartItem> cartItems) {
        return cartItems.stream()
                .collect(Collectors.toMap(
                        CartItem::getProductId,
                        CartItem::getQuantity
                ));
    }

}
