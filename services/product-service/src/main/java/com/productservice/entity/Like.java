package com.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "likes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "likes_seq")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

}
