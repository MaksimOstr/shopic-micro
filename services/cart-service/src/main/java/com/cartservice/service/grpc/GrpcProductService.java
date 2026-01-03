package com.cartservice.service.grpc;

import com.cartservice.exception.ApiException;
import com.cartservice.exception.ExternalServiceBusinessException;
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

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductByIdFallback")
    public Product getProductById(UUID productId) {
        try {
            GetProductRequest request = GetProductRequest.newBuilder()
                    .setProductId(productId.toString())
                    .build();

            return productServiceGrpc.getProductById(request);
        } catch (StatusRuntimeException e) {
            switch (e.getStatus().getCode()) {
                case NOT_FOUND -> throw new ExternalServiceBusinessException(e.getMessage(), HttpStatus.NOT_FOUND);

                default -> throw e;
            }
        }
    }

    public Product getProductByIdFallback(UUID productId, Throwable e) {
        log.error("Product service unavailable for productId: {}", productId, e);
        throw new ApiException("Something went wrong. Try again later", HttpStatus.SERVICE_UNAVAILABLE);
    }
}
