package com.orderservice.service.grpc;


import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.paymentservice.CreatePaymentRequest;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.paymentservice.OrderLineItem;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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

        return paymentGrpcService.createPaymentForOrder(request);
    }

    private List<OrderLineItem> toOrderLineItemList(Map<Long, BigDecimal> priceMap, List<CartItem> cartItems) {
        return cartItems.stream().map(item -> {
            BigDecimal price = priceMap.get(item.getProductId());
            return grpcMapper.toOrderLineItem(
                    item,
                    price
            );
        }).toList();
    }
}
