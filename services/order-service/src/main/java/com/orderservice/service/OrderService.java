package com.orderservice.service;

import com.orderservice.repository.OrderRepository;
import com.orderservice.service.grpc.CartGrpcService;
import com.orderservice.service.grpc.ProductGrpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final CartGrpcService cartGrpcService;
    private final ProductGrpcService productGrpcService;
    private final KafkaEventProducer kafkaEventProducer;
}
