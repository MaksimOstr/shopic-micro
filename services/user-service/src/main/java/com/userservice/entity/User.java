package com.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;
import java.util.Set;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String email;

    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Ban> bans;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    private AuthProviderEnum authProvider;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "user")
    private Profile profile;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @Transient
    public Boolean isAccountNonLocked() {
        return bans.stream().noneMatch(Ban::isActive);
    }

    public User(String email, String password, Set<Role> roles) {
        this.email = email;
        this.password = password;
        this.roles = roles;
        authProvider = AuthProviderEnum.LOCAL;
    }

    @PrePersist
    protected void onCreate() {
        if(isVerified == null) {
            isVerified = true;
        }
    }
}
