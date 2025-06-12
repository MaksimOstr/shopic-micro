package com.cartservice.entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;


@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carts_seq")
    private Long id;

    @Column(nullable = false, unique = true, name = "user_id")
    private Long userId;

    @CreatedDate
    @Column(nullable = false, name = "created_at")
    private Instant createdAt;
}
