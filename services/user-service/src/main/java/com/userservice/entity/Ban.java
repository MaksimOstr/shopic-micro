package com.userservice.entity;


import jakarta.persistence.*;
import lombok.*;
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

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public boolean isActive() {
        return banTo.isAfter(Instant.now());
    }
}
