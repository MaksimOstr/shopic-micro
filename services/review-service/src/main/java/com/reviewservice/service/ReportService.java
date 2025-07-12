package com.reviewservice.service;

import com.reviewservice.dto.ReportDto;
import com.reviewservice.dto.ReportStatusDto;
import com.reviewservice.dto.request.AdminReportParams;
import com.reviewservice.dto.request.CreateCommentReport;
import com.reviewservice.dto.request.CreateReviewReport;
import com.reviewservice.dto.request.UserReportParams;
import com.reviewservice.entity.*;
import com.reviewservice.exception.ForbiddenException;
import com.reviewservice.exception.NotFoundException;
import com.reviewservice.mapper.ReportMapper;
import com.reviewservice.repository.ReportRepository;
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
public class ReportService {
    private final ReportRepository reportRepository;
    private final ReportMapper reportMapper;


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
