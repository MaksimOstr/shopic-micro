package com.orderservice.service.grpc;


import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.paymentservice.CreatePaymentRequest;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.paymentservice.OrderLineItem;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGrpcService {
    private final PaymentServiceGrpc.PaymentServiceBlockingStub paymentGrpcService;
    private final GrpcMapper grpcMapper;

    public CreatePaymentResponse createPayment(long orderId, long userId, Map<Long, BigDecimal> priceMap, List<CartItem> cartItems) {
        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setCustomerId(userId)
                .addAllLineItems(toOrderLineItemList(priceMap, cartItems))
                .build();


        log.info("Create payment request: {}", request);
        return paymentGrpcService.createPaymentForOrder(request);
    }

    private List<OrderLineItem> toOrderLineItemList(Map<Long, BigDecimal> priceMap, List<CartItem> cartItems) {
        return cartItems.stream().map(item -> {
            BigDecimal price = priceMap.get(item.getProductId());
            return OrderLineItem.newBuilder()
                    .setPriceForOne(price.toString())
                    .setQuantity(item.getQuantity())
                    .setProductName(item.getProductName())
                    .setProductImage(item.getProductImageUrl())
                    .build();
        }).toList();
    }
}
