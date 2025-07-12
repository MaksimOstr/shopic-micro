package com.reviewservice.service;

import com.reviewservice.dto.request.CreateCommentReport;
import com.reviewservice.entity.ReportStatus;
import com.reviewservice.entity.ReviewComment;
import com.reviewservice.entity.ReviewCommentReport;
import com.reviewservice.exception.ForbiddenException;
import com.reviewservice.repository.CommentReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentReportService {
    private final CommentReportRepository commentReportRepository;
    private final ReviewCommentService reviewCommentService;


    public void reportComment(CreateCommentReport dto, long userId) {
        ReviewComment comment = reviewCommentService.getReviewComment(dto.commentId());

        if(comment.getUserId() == userId) {
            throw new ForbiddenException("You are not allowed to report your comment");
        }

        ReviewCommentReport report = new ReviewCommentReport();

        report.setComment(comment);
        report.setReporter(userId);
        report.setDescription(dto.description());
        report.setStatus(ReportStatus.PENDING);

        commentReportRepository.save(report);
    }
}
