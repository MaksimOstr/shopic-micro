package com.reviewservice.service;

import com.reviewservice.dto.RatingDto;
import com.reviewservice.projection.ReviewForRating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final ReviewService reviewService;

    public List<RatingDto> getProductRatings(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<ReviewForRating> reviewsForRating = reviewService.getReviewsForRating(productIds);

        if (reviewsForRating.isEmpty()) {
            return productIds.stream()
                    .map(productId -> new RatingDto(productId, BigDecimal.ZERO, 0))
                    .toList();
        }

        Map<Long, List<ReviewForRating>> reviewMap = getReviewMap(reviewsForRating);

        return productIds.stream()
                .map(productId -> {
                    List<ReviewForRating> productReviews = reviewMap.getOrDefault(productId, Collections.emptyList());
                    return createRatingDto(productId, productReviews);
                })
                .toList();
    }

    private RatingDto createRatingDto(Long productId, List<ReviewForRating> reviews) {
        return new RatingDto(
                productId,
                calculateAverage(reviews),
                reviews.size()
        );
    }

    public BigDecimal calculateAverage(List<ReviewForRating> reviewsForRating) {
        if (reviewsForRating == null || reviewsForRating.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = reviewsForRating.stream()
                .map(ReviewForRating::rating)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return sum.divide(
                new BigDecimal(reviewsForRating.size()),
                MathContext.DECIMAL128
        );
    }


    public Map<Long, List<ReviewForRating>> getReviewMap(List<ReviewForRating> reviewsForRating) {
        return reviewsForRating.stream()
                .collect(Collectors.groupingBy(ReviewForRating::productId));
    }
}
