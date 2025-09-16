package com.cartservice.service.grpc;

import com.cartservice.exception.ExternalServiceUnavailableException;
import com.cartservice.exception.InsufficientProductStockException;
import com.cartservice.exception.NotFoundException;
import com.shopic.grpc.productservice.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcProductService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceGrpc;

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductInfoForCartFallback")
    public ProductInfo getProductInfo(long productId) {
        ProductInfoRequest request = ProductInfoRequest.newBuilder()
                .setProductId(productId)
                .build();

        return productServiceGrpc.getProductInfo(request);
    }

    public ProductInfo getProductInfoForCartFallback(long productId, Throwable e) {
        if (e instanceof StatusRuntimeException exception) {
            Status.Code code = exception.getStatus().getCode();

            switch (code) {
                case NOT_FOUND -> throw new NotFoundException(exception.getStatus().getDescription());
                case FAILED_PRECONDITION ->
                        throw new InsufficientProductStockException(exception.getStatus().getDescription());
                default -> throw exception;
            }
        } else {
            log.error("product-service fall back, breaker is open", e);
            throw new ExternalServiceUnavailableException("Something went wrong. Try again later");
        }
    }
}
