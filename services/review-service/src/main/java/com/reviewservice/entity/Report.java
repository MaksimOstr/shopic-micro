package com.reviewservice.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "report_type")
@Setter
@Getter
@Table(name = "reports")
@EntityListeners(AuditingEntityListener.class)
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reports_seq")
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    @Column(name = "report_type", nullable = false)
    private String reportType;

    private Long reporter;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
