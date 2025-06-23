package com.userservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class EmailChangeRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "email_change_requests_seq")
    private Long id;

    @Column(name = "new_email", unique = true, nullable = false)
    private String newEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
