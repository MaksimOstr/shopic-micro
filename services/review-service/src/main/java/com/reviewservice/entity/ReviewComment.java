package com.reviewservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "review_comments")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ReviewComment {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_comments_seq")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Review review;

    @Column(length = 2000, nullable = false)
    private String comment;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
