package com.productservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brands_seq")
    private int id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;
}
