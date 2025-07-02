package com.orderservice.mapper;


import com.orderservice.entity.Order;
import com.orderservice.entity.OrderItem;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.productservice.ActualProductInfoResponse;
import com.shopic.grpc.productservice.ProductInfo;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OrderItemClassMapper {
    public List<OrderItem> mapToOrderItems(Order order, List<ProductInfo> productInfoList, Map<Long, Integer> productQuantityMap) {
        return productInfoList.stream()
                .map(item -> OrderItem.builder()
                        .productId(item.getProductId())
                        .quantity(productQuantityMap.get(item.getProductId()))
                        .productName(item.getProductName())
                        .productImageUrl(item.getProductImageUrl())
                        .priceAtPurchase(new BigDecimal(item.getPrice()))
                        .order(order)
                        .build()).toList();
    }
}
