package com.orderservice.service.grpc;

import com.shopic.grpc.cartservice.CartServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartGrpcService {
    private final CartServiceGrpc.CartServiceBlockingStub cartGrpcService;
}
