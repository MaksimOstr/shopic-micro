package com.reviewservice.service;

import com.reviewservice.dto.ReviewCommentDto;
import com.reviewservice.dto.request.ReviewCommentParams;
import com.reviewservice.dto.request.CreateReviewCommentRequest;
import com.reviewservice.dto.request.UpdateReviewCommentRequest;
import com.reviewservice.entity.Review;
import com.reviewservice.entity.ReviewComment;
import com.reviewservice.exception.NotFoundException;
import com.reviewservice.mapper.ReviewCommentMapper;
import com.reviewservice.repository.ReviewCommentRepository;
import com.reviewservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.reviewservice.utils.SpecificationUtils.*;


@Service
@RequiredArgsConstructor
public class ReviewCommentService {
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewService reviewService;
    private final ReviewCommentMapper reviewCommentMapper;

    @Transactional
    public void createReviewComment(CreateReviewCommentRequest dto, long userId) {
        Review review = reviewService.getReview(dto.reviewId());

        ReviewComment reviewComment = ReviewComment.builder()
                .review(review)
                .lastName(dto.lastName())
                .firstName(dto.firstName())
                .comment(dto.comment())
                .userId(userId)
                .build();


        reviewCommentRepository.save(reviewComment);
    }

    @Transactional(readOnly = true)
    public Page<ReviewCommentDto> getReviewComments(long reviewId, Pageable pageable) {
        Page<ReviewComment> comments = reviewCommentRepository.findByReviewId(reviewId, pageable);
        List<ReviewComment> commentList = comments.getContent();
        List<ReviewCommentDto> dtoList = reviewCommentMapper.toDtoList(commentList);

        return new PageImpl<>(dtoList, pageable, comments.getTotalElements());
    }

    @Transactional
    public void updateReviewComment(UpdateReviewCommentRequest dto, long userId, long commentId) {
        boolean exists = reviewService.existsReview(dto.reviewId());

        if(!exists) {
            throw new NotFoundException("Review for comment not found");
        }

        ReviewComment reviewComment = reviewCommentRepository.findByUserIdAndId(userId, commentId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        reviewComment.setComment(dto.comment());
    }

    @Transactional(readOnly = true)
    public Page<ReviewCommentDto> getReviewCommentsBySpec(ReviewCommentParams params, Pageable pageable) {
        Specification<ReviewComment> spec = SpecificationUtils.<ReviewComment>equalsLong("userId", params.userId())
                .and(hasChild("review", params.reviewId()))
                .and(gte("createdAt", params.dateFrom()))
                .and(lte("createdAt", params.dateTo()));

        Page<ReviewComment> comments = reviewCommentRepository.findAll(spec, pageable);
        List<ReviewComment> commentList = comments.getContent();
        List<ReviewCommentDto> dtoList = reviewCommentMapper.toDtoList(commentList);

        return new PageImpl<>(dtoList, pageable, comments.getTotalElements());
    }

    public void deleteReviewComment(long reviewId, long userId) {
        int deleted = reviewCommentRepository.deleteByIdAndUserId(reviewId, userId);

        if(deleted == 0) {
            throw new NotFoundException("Review not found");
        }
    }

    public ReviewComment getReviewComment(long commentId) {
        return reviewCommentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
    }

    public void deleteReviewComment(long commentId) {
        int deleted = reviewCommentRepository.deleteById(commentId);

        if(deleted == 0) {
            throw new NotFoundException("Review not found");
        }
    }
}
