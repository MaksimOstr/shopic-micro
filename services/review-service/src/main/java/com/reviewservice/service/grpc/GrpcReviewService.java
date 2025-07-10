package com.reviewservice.service.grpc;

import com.reviewservice.dto.RatingDto;
import com.reviewservice.mapper.GrpcMapper;
import com.reviewservice.service.RatingService;
import com.shopic.grpc.reviewservice.ProductRating;
import com.shopic.grpc.reviewservice.ProductRatingsRequest;
import com.shopic.grpc.reviewservice.ProductRatingsResponse;
import com.shopic.grpc.reviewservice.ReviewServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class GrpcReviewService extends ReviewServiceGrpc.ReviewServiceImplBase {
    private final RatingService ratingService;
    private final GrpcMapper grpcMapper;

    @Override
    public void getProductRatings(ProductRatingsRequest request, StreamObserver<ProductRatingsResponse> responseObserver) {
        try {
            List<RatingDto> ratings = ratingService.getProductRating(request.getProductIdList());
            List<ProductRating> responseList = grpcMapper.toProductRatingList(ratings);

            ProductRatingsResponse response = ProductRatingsResponse.newBuilder()
                    .addAllProductRating(responseList)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
