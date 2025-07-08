package com.reviewservice.service;

import com.reviewservice.dto.request.CreateReviewRequest;
import com.reviewservice.entity.Review;
import com.reviewservice.entity.ReviewStatus;
import com.reviewservice.exception.NotFoundException;
import com.reviewservice.repository.ReviewRepository;
import com.reviewservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.IsProductExistsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GrpcProductService grpcProductService;

    @Transactional
    public void createReview(CreateReviewRequest dto, long userId) {
        IsProductExistsResponse response = grpcProductService.isProductExists(dto.productId());

        if(!response.getIsExists()) {
            throw new NotFoundException("Product does not exist");
        }

        Review review = Review.builder()
                .comment(dto.comment())
                .productId(dto.productId())
                .userId(userId)
                .rating(dto.rating())
                .status(ReviewStatus.PENDING)
                .build();

        reviewRepository.save(review);
    }
}
