package com.reviewservice.service;

import com.reviewservice.dto.ReviewDto;
import com.reviewservice.dto.request.CreateReviewRequest;
import com.reviewservice.entity.Review;
import com.reviewservice.exception.NotFoundException;
import com.reviewservice.mapper.ReviewMapper;
import com.reviewservice.projection.ReviewForRating;
import com.reviewservice.repository.ReviewRepository;
import com.reviewservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.IsProductExistsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GrpcProductService grpcProductService;
    private final ReviewMapper reviewMapper;

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
                .build();

        reviewRepository.save(review);
    }

    public Page<ReviewDto> getReviewsByProductId(long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        List<Review> reviewList = reviews.getContent();
        List<ReviewDto> reviewDtoList = reviewMapper.toDto(reviewList);

        return new PageImpl<>(reviewDtoList, pageable, reviews.getTotalElements());
    }

    public Review getReview(long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
    }

    public List<ReviewForRating> getReviewsForRating(List<Long> productIds) {
        return reviewRepository.findReviewsForRatingByProductId(productIds);
    }
}
