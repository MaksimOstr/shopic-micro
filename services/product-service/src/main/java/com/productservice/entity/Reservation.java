package com.productservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reservations_seq")
    private Long id;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReservationItem> items;

    @Column(name = "order_id", nullable = false, unique = true)
    private long orderId;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
