package com.reviewservice.service.grpc;

import com.reviewservice.dto.RatingDto;
import com.reviewservice.mapper.GrpcMapper;
import com.reviewservice.service.RatingService;
import com.shopic.grpc.reviewservice.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.math.BigDecimal;
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
            List<RatingDto> ratings = ratingService.getProductRatings(request.getProductIdList());
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

    @Override
    public void getProductRating(ProductRatingRequest request, StreamObserver<ProductRatingResponse> responseObserver) {
        try {
            BigDecimal rating = ratingService.getRatingByProductId(request.getProductId());

            ProductRatingResponse response = ProductRatingResponse.newBuilder().setRating(rating.toString()).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

}
