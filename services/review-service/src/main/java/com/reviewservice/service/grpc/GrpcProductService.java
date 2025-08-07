package com.reviewservice.service.grpc;


import com.reviewservice.exception.ExternalServiceUnavailableException;
import com.shopic.grpc.productservice.IsProductExistsRequest;
import com.shopic.grpc.productservice.IsProductExistsResponse;
import com.shopic.grpc.productservice.ProductServiceGrpc;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcProductService {
    private final ProductServiceGrpc.ProductServiceBlockingStub productGrpcService;

    @CircuitBreaker(name = "product-service", fallbackMethod = "isProductExistsFallback")
    public IsProductExistsResponse isProductExists(long productId) {
        IsProductExistsRequest request = IsProductExistsRequest.newBuilder()
                .setProductId(productId)
                .build();

        return productGrpcService.isProductExists(request);
    }

    public IsProductExistsResponse isProductExistsFallback(long productId, Throwable throwable) {
        log.error("isProductExistsFallback", throwable);
        throw new ExternalServiceUnavailableException("Something went wrong. Please try again later");
    }
}
