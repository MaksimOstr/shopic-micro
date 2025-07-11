package com.reviewservice.service;

import com.reviewservice.dto.ReportDto;
import com.reviewservice.dto.ReportStatusDto;
import com.reviewservice.dto.request.AdminReportParams;
import com.reviewservice.dto.request.CreateCommentReport;
import com.reviewservice.dto.request.CreateReviewReport;
import com.reviewservice.dto.request.ReportParams;
import com.reviewservice.dto.request.UserReportParams;
import com.reviewservice.entity.*;
import com.reviewservice.exception.NotFoundException;
import com.reviewservice.mapper.ReportMapper;
import com.reviewservice.repository.ReportRepository;
import com.reviewservice.utils.SpecificationUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.reviewservice.utils.SpecificationUtils.*;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReviewService reviewService;
    private final ReviewCommentService reviewCommentService;
    private final ReportMapper reportMapper;

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

    public ReportDto getReportDto(long reportId) {
        Report report = getReport(reportId);

        return reportMapper.toReportDto(report);
    }

    public Page<ReportStatusDto> getReports(UserReportParams dto, long userId, Pageable pageable) {
        Specification<Report> spec = SpecificationUtils.<Report>equalsLong("reporter", userId)
                .and(equalsEnum("status", dto.status()));

        Page<Report> reports = reportRepository.findAll(spec, pageable);
        List<Report> reportList = reports.getContent();
        List<ReportStatusDto> reportDtoList = reportMapper.toReportStatusDtoList(reportList);

        return new PageImpl<>(reportDtoList, pageable, reports.getTotalElements());
    }

    public Page<ReportDto> getReports(AdminReportParams dto, Pageable pageable) {
        Specification<Report> spec = SpecificationUtils.<Report>equalsLong("reporter", dto.userId())
                .and(hasChild("review", dto.reviewId()))
                .and(hasChild("comment", dto.commentId()))
                .and(equalsEnum("status", dto.status()))
                .and(iLike("reportType", dto.reportType()));

        Page<Report> reports = reportRepository.findAll(spec, pageable);
        List<Report> reportList = reports.getContent();
        List<ReportDto> reportDtoList = reportMapper.toReportDtoList(reportList);

        return new PageImpl<>(reportDtoList, pageable, reports.getTotalElements());
    }

    public ReportDto getReportDto(long reportId, long userId) {
        Report report = reportRepository. findByIdAndReporter(reportId, userId)
                .orElseThrow(() -> new NotFoundException("Report not found"));

        return reportMapper.toReportDto(report);
    }

    @Transactional
    public void changeReportStatus(long reportId, ReportStatus status) {
        Report report = getReport(reportId);

        report.setStatus(status);
    }

    private Report getReport(long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report not found"));
    }
}
