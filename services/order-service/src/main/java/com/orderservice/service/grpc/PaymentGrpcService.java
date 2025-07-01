package com.orderservice.service.grpc;


import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.cartservice.CartItem;
import com.shopic.grpc.paymentservice.CreatePaymentRequest;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.paymentservice.OrderLineItem;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import com.shopic.grpc.productservice.ProductInfo;
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

    public CreatePaymentResponse createPayment(long orderId, long userId, List<ProductInfo> productInfoList, Map<Long, Integer> productQuantityMap) {
        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setCustomerId(userId)
                .addAllLineItems(toOrderLineItemList(productInfoList, productQuantityMap))
                .build();


        log.info("Create payment request: {}", request);
        return paymentGrpcService.createPaymentForOrder(request);
    }

    private List<OrderLineItem> toOrderLineItemList(List<ProductInfo> productInfoList, Map<Long, Integer> productQuantityMap) {
        return productInfoList.stream().map(item -> OrderLineItem.newBuilder()
                .setPriceForOne(item.getPrice())
                .setQuantity(productQuantityMap.get(item.getProductId()))
                .setProductName(item.getProductName())
                .setProductImage(item.getProductImageUrl())
                .build()).toList();
    }
}
