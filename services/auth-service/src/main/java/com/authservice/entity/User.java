package com.authservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    private String password;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "is_non_blocked", nullable = false)
    private Boolean isNonBlocked;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProviderEnum authProvider;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRolesEnum role = UserRolesEnum.ROLE_USER;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if(isVerified == null) {
            isVerified = false;
        }

        if(isNonBlocked == null) {
            isNonBlocked = true;
        }
    }
}
