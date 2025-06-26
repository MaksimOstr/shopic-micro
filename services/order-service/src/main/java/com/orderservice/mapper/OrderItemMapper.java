package com.orderservice.mapper;


import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.shopic.grpc.cartservice.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderItemMapper {
    public List<OrderItem> mapToOrderItems(List<CartItem> cartItems, Order order, Map<Long, BigDecimal> priceMap) {
        return cartItems.parallelStream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .productName(item.getProductName())
                        .productImageUrl(item.getProductImageUrl())
                        .priceAtPurchase(priceMap.get(item.getProductId()))
                        .order(order)
                        .build())
                .toList();
    }
}
