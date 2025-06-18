package com.orderservice.service.grpc;

import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.cartservice.OrderCartInfoGrpcRequest;
import com.shopic.grpc.cartservice.OrderCartInfoGrpcResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartGrpcService {
    private final CartServiceGrpc.CartServiceBlockingStub cartGrpcService;

    public OrderCartInfoGrpcResponse getCartInfo(long userId) {
        OrderCartInfoGrpcRequest request = OrderCartInfoGrpcRequest.newBuilder()
                .setUserId(userId)
                .build();

        return cartGrpcService.getOrderCartInfo(request);
    }

}
