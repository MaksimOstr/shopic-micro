package com.reviewservice.service;

import com.reviewservice.dto.request.CreateCommentReport;
import com.reviewservice.dto.request.CreateReviewReport;
import com.reviewservice.entity.*;
import com.reviewservice.repository.ReportRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReviewService reviewService;
    private final ReviewCommentService reviewCommentService;

    public void reportComment(CreateCommentReport dto, long userId) {
        ReviewComment comment = reviewCommentService.getReviewComment(dto.commentId());
        ReviewCommentReport report = new ReviewCommentReport();

        report.setComment(comment);
        report.setReporter(userId);
        report.setDescription(dto.description());
        report.setStatus(ReportStatus.PENDING);

        reportRepository.save(report);
    }

    public void reportReview(CreateReviewReport dto, long userId) {
        Review review = reviewService.getReview(dto.reviewId());

        ReviewReport report = new ReviewReport();

        report.setDescription(dto.description());
        report.setReporter(userId);
        report.setReview(review);
        report.setStatus(ReportStatus.PENDING);

        reportRepository.save(report);
    }
}
