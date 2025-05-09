package com.authservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@NoArgsConstructor
@Getter
@Setter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.SEQUENCE, generator = "refresh_tokens_seq")
    private Long id;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
