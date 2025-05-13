package com.codeservice.entity;

import com.codeservice.enums.CodeScopeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table(name = "codes")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Code {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "codes_seq")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false)
    private CodeScopeEnum scope;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public Code (String code, Instant expiresAt, CodeScopeEnum scope, long userId) {
        this.code = code;
        this.expiresAt = expiresAt;
        this.scope = scope;
        this.userId = userId;
        this.createdAt = Instant.now();
    }
}
