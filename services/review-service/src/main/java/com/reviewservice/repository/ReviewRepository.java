package com.reviewservice.repository;

import com.reviewservice.entity.Review;
import com.reviewservice.projection.ReviewForRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    Page<Review> findByProductId(Long productId, Pageable pageable);

    int deleteByIdAndUserId(Long id, Long userId);

    int deleteById(long id);

    Optional<Review> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT new com.reviewservice.projection.ReviewForRating(" +
            "r.productId," +
            "r.rating" +
            ")" +
            "FROM Review r WHERE r.productId IN :productIds")
    List<ReviewForRating> findReviewsForRatingByProductId(List<Long> productIds);
}
