package com.reviewservice.repository;

import com.reviewservice.entity.Review;
import com.reviewservice.projection.ReviewForRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProductId(Long productId, Pageable pageable);

    @Query("SELECT new com.reviewservice.projection.ReviewForRating(" +
            "r.productId," +
            "r.rating" +
            ")" +
            "FROM Review r WHERE r.productId IN :productIds")
    List<ReviewForRating> findReviewsForRatingByProductId(List<Long> productIds);
}
