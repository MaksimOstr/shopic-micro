package com.reviewservice.service;

import com.reviewservice.dto.ReviewCommentDto;
import com.reviewservice.dto.request.CreateReviewCommentRequest;
import com.reviewservice.entity.Review;
import com.reviewservice.entity.ReviewComment;
import com.reviewservice.mapper.ReviewCommentMapper;
import com.reviewservice.repository.ReviewCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewCommentService {
    private final ReviewCommentRepository reviewCommentRepository;
    private final ReviewService reviewService;
    private final ReviewCommentMapper reviewCommentMapper;


    public void createReviewComment(CreateReviewCommentRequest dto, long userId, long reviewId) {
        Review review = reviewService.getReview(reviewId);

        ReviewComment reviewComment = ReviewComment.builder()
                .review(review)
                .comment(dto.comment())
                .userId(userId)
                .build();


        reviewCommentRepository.save(reviewComment);
    }

    public Page<ReviewCommentDto> getReviewComments(long reviewId, Pageable pageable) {
        Page<ReviewComment> comments = reviewCommentRepository.findByReviewId(reviewId, pageable);
        List<ReviewComment> commentList = comments.getContent();
        List<ReviewCommentDto> dtoList = reviewCommentMapper.toDtoList(commentList);

        return new PageImpl<>(dtoList, pageable, comments.getTotalElements());
    }
}
