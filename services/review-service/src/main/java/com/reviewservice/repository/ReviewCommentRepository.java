package com.reviewservice.repository;

import com.reviewservice.entity.ReviewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    Page<ReviewComment> findByReviewId(Long reviewId, Pageable pageable);
}
