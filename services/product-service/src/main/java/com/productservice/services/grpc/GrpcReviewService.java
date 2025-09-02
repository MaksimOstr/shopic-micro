package com.productservice.services.grpc;

import com.productservice.exceptions.ExternalServiceUnavailableException;
import com.shopic.grpc.reviewservice.*;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GrpcReviewService {
    private final ReviewServiceGrpc.ReviewServiceBlockingStub gRpcReviewService;

    @CircuitBreaker(name = "review-service", fallbackMethod = "getProductRatingsFallback")
    public ProductRatingsResponse getProductRatings(List<Long> productIds) {
        ProductRatingsRequest request = ProductRatingsRequest.newBuilder()
                .addAllProductId(productIds)
                .build();

        return gRpcReviewService.getProductRatings(request);
    }

    @CircuitBreaker(name = "review-service", fallbackMethod = "getProductRatingsFallback")
    public ProductRatingResponse getProductRating(long productId) {
        ProductRatingRequest request = ProductRatingRequest.newBuilder()
                .setProductId(productId)
                .build();

        return gRpcReviewService.getProductRating(request);
    }

    public ProductRatingsResponse getProductRatingsFallback(List<Long> productIds, Throwable throwable) {
        log.error("getProductRatingsFallback", throwable);
        throw new ExternalServiceUnavailableException("Something went wrong. Please try again later");
    }
}
