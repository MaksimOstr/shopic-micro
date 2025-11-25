package com.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "codes")
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Code {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "codes_seq")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private CodeScopeEnum scope;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
