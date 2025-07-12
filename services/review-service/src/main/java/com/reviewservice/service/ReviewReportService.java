package com.reviewservice.service;


import com.reviewservice.dto.request.CreateReviewReport;
import com.reviewservice.entity.ReportStatus;
import com.reviewservice.entity.Review;
import com.reviewservice.entity.ReviewReport;
import com.reviewservice.exception.ForbiddenException;
import com.reviewservice.repository.ReviewReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewReportService {
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewService reviewService;

    public void reportReview(CreateReviewReport dto, long userId) {
        Review review = reviewService.getReview(dto.reviewId());

        if(review.getUserId() == userId) {
            throw new ForbiddenException("You are not allowed to report your comment");
        }

        ReviewReport report = new ReviewReport();

        report.setDescription(dto.description());
        report.setReporter(userId);
        report.setReview(review);
        report.setStatus(ReportStatus.PENDING);

        reviewReportRepository.save(report);
    }
}
