package com.orderservice.service.grpc;


import com.orderservice.exception.ExternalServiceUnavailableException;
import com.orderservice.exception.InternalException;
import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.paymentservice.CreatePaymentRequest;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.paymentservice.OrderItem;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import com.shopic.grpc.productservice.Product;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final GrpcMapper grpcMapper;

    @CircuitBreaker(name = "payment-service", fallbackMethod = "createPaymentFallback")
    public CreatePaymentResponse createPayment(UUID orderId, UUID userId, List<Product> productInfoList, Map<Long, Integer> productQuantityMap, BigDecimal deliveryPrice) {
        log.info("Create payment gRpc request");

        List<OrderItem> orderItems = grpcMapper.toOrderLineItemList(productInfoList, productQuantityMap);

        OrderItem deliveryLineItem = OrderItem.newBuilder()
                .setQuantity(1)
                .setPriceForOne(deliveryPrice.toString())
                .setItemName("Delivery")
                .setItemImage("")
                .build();

        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setOrderId(orderId.toString())
                .setUserId(userId.toString())
                .addAllOrderItems(orderItems)
                .build();

        return paymentGrpcService.createPayment(request);
    }

    public CreatePaymentResponse createPaymentFallback(UUID orderId, UUID userId, List<Product> productInfoList, Map<Long, Integer> productQuantityMap, Throwable exception) {
        log.error(exception.getMessage(), exception);
        if (exception instanceof StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case INTERNAL, FAILED_PRECONDITION:
                    throw new InternalException(e.getStatus().getDescription());
                default:
                    throw e;
            }
        } else {
            throw new ExternalServiceUnavailableException("Something went wrong. Please try again later");
        }
    }
}
