package com.cartservice.service.grpc;

import com.cartservice.exception.ApiException;
import com.cartservice.exception.NotFoundException;
import com.shopic.grpc.productservice.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.http.HttpStatus;

import java.util.UUID;


@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcProductService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceGrpc;

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductInfoForCartFallback")
    public ProductInfo getProductInfo(UUID productId) {
        ProductInfoRequest request = ProductInfoRequest.newBuilder()
                .setProductId(productId.toString())
                .build();

        return productServiceGrpc.getProductInfo(request);
    }

    public ProductInfo getProductInfoForCartFallback(long productId, Throwable e) {
        if (e instanceof StatusRuntimeException exception) {
            Status.Code code = exception.getStatus().getCode();

            switch (code) {
                case NOT_FOUND -> throw new NotFoundException(exception.getStatus().getDescription());
                case FAILED_PRECONDITION ->
                        throw new ApiException(exception.getStatus().getDescription(), HttpStatus.BAD_REQUEST);
                default -> throw exception;
            }
        } else {
            log.error("product-service fall back, breaker is open", e);
            throw new ApiException("Something went wrong. Try again later", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
