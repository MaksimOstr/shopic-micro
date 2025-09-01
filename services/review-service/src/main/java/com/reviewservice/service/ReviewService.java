package com.reviewservice.service;

import com.reviewservice.dto.ReviewDto;
import com.reviewservice.dto.request.CreateReviewRequest;
import com.reviewservice.dto.request.ReviewParams;
import com.reviewservice.dto.request.UpdateReviewRequest;
import com.reviewservice.entity.Review;
import com.reviewservice.exception.NotFoundException;
import com.reviewservice.mapper.ReviewMapper;
import com.reviewservice.projection.ReviewForRating;
import com.reviewservice.repository.ReviewRepository;
import com.reviewservice.service.grpc.GrpcProductService;
import com.reviewservice.service.grpc.GrpcProfileService;
import com.reviewservice.utils.SpecificationUtils;
import com.shopic.grpc.productservice.IsProductExistsResponse;
import com.shopic.grpc.profileservice.ProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.reviewservice.utils.SpecificationUtils.*;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final GrpcProductService grpcProductService;
    private final ReviewMapper reviewMapper;
    private final GrpcProfileService grpcProfileService;

    @Transactional
    public void createReview(CreateReviewRequest dto, long userId) {
        IsProductExistsResponse response = grpcProductService.isProductExists(dto.productId());

        if(!response.getIsExists()) {
            throw new NotFoundException("Product does not exist");
        }

        ProfileResponse profile = grpcProfileService.getUserProfile(userId);

        Review review = Review.builder()
                .comment(dto.comment())
                .productId(dto.productId())
                .lastName(profile.getLastName())
                .firstName(profile.getFirstName())
                .userId(userId)
                .rating(dto.rating())
                .build();

        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(UpdateReviewRequest dto, long userId, long reviewId) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new NotFoundException("Review does not exist"));

        Optional.ofNullable(dto.comment()).ifPresent(review::setComment);
        Optional.ofNullable(dto.rating()).ifPresent(review::setRating);
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsByProductId(long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        List<Review> reviewList = reviews.getContent();
        List<ReviewDto> reviewDtoList = reviewMapper.toDto(reviewList);

        return new PageImpl<>(reviewDtoList, pageable, reviews.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ReviewDto> getReviewsBySpec(Pageable pageable, ReviewParams params) {
        Specification<Review> spec = SpecificationUtils.<Review> equalsLong("userId", params.userId())
                .and(equalsLong("productId", params.productId()))
                .and(gte("rating", params.dateFrom()))
                .and(lte("rating", params.dateTo()))
                .and(gte("createdAt", params.dateFrom()))
                .and(lte("createdAt", params.dateTo()));

        Page<Review> reviews = reviewRepository.findAll(spec, pageable);
        List<Review> reviewList = reviews.getContent();
        List<ReviewDto> reviewDtoList = reviewMapper.toDto(reviewList);

        return new PageImpl<>(reviewDtoList, pageable, reviews.getTotalElements());
    }

    public void deleteReview(long reviewId, long userId) {
        int deleted = reviewRepository.deleteByIdAndUserId(reviewId, userId);

        if(deleted == 0) {
            throw new NotFoundException("Review was not found");
        }
    }

    public void deleteReview(long reviewId) {
        int deleted = reviewRepository.deleteById(reviewId);

        if(deleted == 0) {
            throw new NotFoundException("Review was not found");
        }
    }

    public Review getReview(long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
    }

    public boolean existsReview(long reviewId) {
        return reviewRepository.existsById(reviewId);
    }

    public List<ReviewForRating> getReviewsForRating(List<Long> productIds) {
        return reviewRepository.findReviewsForRatingByProductId(productIds);
    }
}
