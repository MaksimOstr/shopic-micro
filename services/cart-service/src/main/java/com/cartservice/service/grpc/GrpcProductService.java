package com.cartservice.service.grpc;

import com.cartservice.exception.ExternalServiceUnavailableException;
import com.cartservice.exception.InsufficientProductStockException;
import com.cartservice.exception.NotFoundException;
import com.shopic.grpc.productservice.GetProductDetailsRequest;
import com.shopic.grpc.productservice.ProductDetailsResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcProductService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceGrpc;

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductInfoForCartFallback")
    public ProductDetailsResponse getProductInfoForCart(long productId, int quantity) {
        GetProductDetailsRequest request = GetProductDetailsRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        return productServiceGrpc.getProductPriceAndStock(request);
    }

    public ProductDetailsResponse getProductInfoForCartFallback(long productId, int quantity, Throwable e) {
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
