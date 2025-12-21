package com.orderservice.service.grpc;

import com.orderservice.exception.ExternalServiceUnavailableException;
import com.orderservice.exception.NotFoundException;
import com.shopic.grpc.cartservice.CartServiceGrpc;
import com.shopic.grpc.cartservice.GetCartRequest;
import com.shopic.grpc.cartservice.CartResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class CartGrpcService {
    private final CartServiceGrpc.CartServiceBlockingStub cartGrpcService;

    @CircuitBreaker(name = "cart-service", fallbackMethod = "getCartFallback")
    public CartResponse getCart(UUID userId) {
        GetCartRequest request = GetCartRequest.newBuilder()
                .setUserId(userId.toString())
                .build();

        return cartGrpcService.getCart(request);
    }

    public CartResponse getCartFallback(long userId, Throwable exception) {
        if (exception instanceof StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                throw new NotFoundException(e.getStatus().getDescription());
            }
            throw e;
        } else {
            throw new ExternalServiceUnavailableException("Something went wrong. Please try again later");
        }
    }
}
