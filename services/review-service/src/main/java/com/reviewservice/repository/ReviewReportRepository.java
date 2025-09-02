package com.reviewservice.repository;

import com.reviewservice.entity.ReviewReport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewReportRepository extends CrudRepository<ReviewReport, Long> {
    boolean existsByReview_IdAndReporterId(Long reviewId, Long reporter);
}
