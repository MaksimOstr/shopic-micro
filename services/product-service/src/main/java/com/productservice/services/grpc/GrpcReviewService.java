package com.productservice.services.grpc;

import com.shopic.grpc.reviewservice.ProductRatingsRequest;
import com.shopic.grpc.reviewservice.ProductRatingsResponse;
import com.shopic.grpc.reviewservice.ReviewServiceGrpc;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GrpcReviewService {
    private final ReviewServiceGrpc.ReviewServiceBlockingStub gRpcReviewService;

    public ProductRatingsResponse getProductRatings(List<Long> productIds) {
        ProductRatingsRequest request = ProductRatingsRequest.newBuilder()
                .addAllProductId(productIds)
                .build();

        return gRpcReviewService.getProductRatings(request);
    }
}
