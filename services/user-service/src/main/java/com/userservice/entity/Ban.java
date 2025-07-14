package com.userservice.entity;


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

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Column(name = "ban_to", nullable = false)
    private Instant banTo;

    private String reason;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @CreatedBy
    @JoinColumn(name = "banned_by", nullable = false)
    private User bannedBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unbanned_by")
    private User unbannedBy;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
