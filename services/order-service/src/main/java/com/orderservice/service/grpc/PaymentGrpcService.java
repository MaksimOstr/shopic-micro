package com.orderservice.service.grpc;


import com.orderservice.entity.Order;
import com.orderservice.exception.ApiException;
import com.orderservice.exception.ExternalServiceBusinessException;
import com.orderservice.mapper.GrpcMapper;
import com.orderservice.mapper.OrderItemMapper;
import com.shopic.grpc.paymentservice.CreatePaymentRequest;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.paymentservice.OrderItem;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import com.shopic.grpc.productservice.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGrpcService {
    private final PaymentServiceGrpc.PaymentServiceBlockingStub paymentGrpcService;
    private final OrderItemMapper orderItemMapper;

    @CircuitBreaker(name = "payment-service", fallbackMethod = "createPaymentFallback")
    public CreatePaymentResponse createPayment(UUID userId, Order order) {
        log.info("Create payment gRpc request");

        List<OrderItem> orderItems = orderItemMapper.toGrpcOrderItems(order.getOrderItems());

        OrderItem deliveryLineItem = OrderItem.newBuilder()
                .setQuantity(1)
                .setPriceForOne(order.getDeliveryPrice().toString())
                .setItemName("Delivery")
                .setItemImage("")
                .build();

        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setOrderId(order.getId().toString())
                .setUserId(userId.toString())
                .addAllOrderItems(orderItems)
                .addOrderItems(deliveryLineItem)
                .build();

        return paymentGrpcService.createPayment(request);
    }

    public CreatePaymentResponse createPaymentFallback(UUID userId, Order order, Throwable exception) {
        log.error("Payment service fallback for orderId={} userId={}", order.getId(), userId, exception);

        throw new ApiException("Something went wrong, try again later", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
