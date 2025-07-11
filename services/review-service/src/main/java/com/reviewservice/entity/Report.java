package com.reviewservice.entity;


import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "report_type")
@Setter
@Table(name = "reports")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reports_seq")
    private Long id;

    private String description;

    private ReportStatus status;

    private Long reporter;
}
