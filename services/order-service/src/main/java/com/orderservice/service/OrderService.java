package com.orderservice.service;

import com.orderservice.repository.OrderRepository;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import com.shopic.grpc.cartservice.OrderCartInfoGrpcResponse;
import com.shopic.grpc.cartservice.OrderCartItem;
import com.shopic.grpc.productservice.CheckProductGrpcResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final CartGrpcService cartGrpcService;
    private final ProductGrpcService productGrpcService;
    private final KafkaEventProducer kafkaEventProducer;


    public void createOrder(long userId) {
        OrderCartInfoGrpcResponse cartInfo = cartGrpcService.getCartInfo(userId);
        List<Long> productIds = getProductIds(cartInfo.getOrderCartItemsList());
        CheckProductGrpcResponse productInfoList = productGrpcService.checkProducts(productIds);

        System.out.println(productInfoList);
    }

    private List<Long> getProductIds(List<OrderCartItem> orderCartItems) {
        return orderCartItems.stream().map(OrderCartItem::getProductId).toList();
    }
}
