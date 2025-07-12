package com.reviewservice.repository;

import com.reviewservice.entity.ReviewCommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReportRepository extends JpaRepository<ReviewCommentReport, Long> {
}
