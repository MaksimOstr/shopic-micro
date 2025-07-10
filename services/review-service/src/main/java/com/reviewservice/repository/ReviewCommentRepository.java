package com.reviewservice.repository;

import com.reviewservice.entity.ReviewComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long>, JpaSpecificationExecutor<ReviewComment> {
    Page<ReviewComment> findByReviewId(Long reviewId, Pageable pageable);

    Optional<ReviewComment> findByUserIdAndId(Long userId, Long id);


    @Transactional
    int deleteById(long id);

    @Transactional
    int deleteByIdAndUserId(Long id, Long userId);
}
