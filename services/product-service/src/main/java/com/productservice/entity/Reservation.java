package com.productservice.entity;

import com.productservice.enums.ReservationStatusEnum;
import com.productservice.exceptions.ApiException;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "reservation", cascade = { CascadeType.REMOVE, CascadeType.PERSIST }, orphanRemoval = true)
    private List<ReservationItem> items;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatusEnum status;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public void changeStatus(ReservationStatusEnum newStatus) {
        if (this.status == ReservationStatusEnum.COMPLETED) {
            throw new ApiException("Cannot change status of a completed reservation", HttpStatus.CONFLICT);
        }

        if (this.status == ReservationStatusEnum.CANCELLED) {
            throw new ApiException("Cannot change status of a cancelled reservation", HttpStatus.CONFLICT);
        }

        this.status = newStatus;
    }
}
