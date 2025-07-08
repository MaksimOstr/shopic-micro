package com.orderservice.service.grpc;


import com.orderservice.exception.InternalException;
import com.orderservice.mapper.GrpcMapper;
import com.shopic.grpc.paymentservice.CreatePaymentRequest;
import com.shopic.grpc.paymentservice.CreatePaymentResponse;
import com.shopic.grpc.paymentservice.OrderLineItem;
import com.shopic.grpc.paymentservice.PaymentServiceGrpc;
import com.shopic.grpc.productservice.ProductInfo;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentGrpcService {
    private final PaymentServiceGrpc.PaymentServiceBlockingStub paymentGrpcService;
    private final GrpcMapper grpcMapper;

    public CreatePaymentResponse createPayment(long orderId, long userId, List<ProductInfo> productInfoList, Map<Long, Integer> productQuantityMap) {
        List<OrderLineItem> orderLineItemList = grpcMapper.toOrderLineItemList(productInfoList, productQuantityMap);

        CreatePaymentRequest request = CreatePaymentRequest.newBuilder()
                .setOrderId(orderId)
                .setCustomerId(userId)
                .addAllLineItems(orderLineItemList)
                .build();

        log.info("Create payment request: {}", request);
        try {
            return paymentGrpcService.createPaymentForOrder(request);
        } catch (StatusRuntimeException e) {
            log.error(e.getMessage(), e);
            switch (e.getStatus().getCode()) {
                case INTERNAL, UNKNOWN, FAILED_PRECONDITION: throw new InternalException(e.getStatus().getDescription());
                default: throw e;
            }
        }
    }
}
