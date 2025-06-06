package com.productservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "brands")
@Getter
@Setter
@NoArgsConstructor
public class Brand {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "brands_seq")
    private int id;

    @Column(nullable = false, unique = true, length = 30)
    private String name;

    public Brand(String name) {
        this.name = name;
    }
}
