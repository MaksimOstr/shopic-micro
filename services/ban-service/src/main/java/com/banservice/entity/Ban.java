package com.banservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "bans")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Ban {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bans_seq")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ban_to", nullable = false)
    private Instant banTo;

    private String reason;

    @Column(name = "banner_id", nullable = false)
    @CreatedBy
    private Long bannerId;

    @Column(name = "unbanner_id", nullable = false)
    private Long unbannerId;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
