package com.orderservice.service.grpc;

import com.orderservice.exception.ApiException;
import com.orderservice.exception.ExternalServiceBusinessException;
import com.orderservice.exception.NotFoundException;
import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.cartservice.GetCartRequest;
import com.shopic.grpc.cartservice.CartResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.UUID;

@GrpcService
@Slf4j
@RequiredArgsConstructor
public class CartGrpcService {
    private final CartServiceGrpc.CartServiceBlockingStub cartGrpcService;

    @CircuitBreaker(name = "cart-service", fallbackMethod = "getCartFallback")
    public CartResponse getCart(UUID userId) {
        try {
            log.info("Sending gRPC request to get cart for userId={}", userId);

            GetCartRequest request = GetCartRequest.newBuilder()
                    .setUserId(userId.toString())
                    .build();

            return cartGrpcService.getCart(request);
        } catch (StatusRuntimeException e) {
            log.error("gRPC error while fetching cart for userId={}", userId, e);
            switch (e.getStatus().getCode()) {
                case NOT_FOUND: throw new ExternalServiceBusinessException(e.getMessage(), HttpStatus.NOT_FOUND);

                default: throw e;
            }
        }
    }

    public CartResponse getCartFallback(UUID userId, Throwable exception) {
        log.error("Cart service unavailable for userId: {}", userId, exception);

        if(exception instanceof ExternalServiceBusinessException e) {
            throw e;
        }

        return CartResponse.newBuilder()
                .addAllCartItems(Collections.emptyList())
                .build();
    }
}
