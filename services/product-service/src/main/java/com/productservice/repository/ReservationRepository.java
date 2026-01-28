package com.productservice.repository;

import com.productservice.entity.Reservation;
import com.productservice.enums.ReservationStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {

    @Query("SELECT r FROM Reservation r JOIN FETCH r.items WHERE r.id = :id")
    Optional<Reservation> findByIdWithItems(UUID id);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.items WHERE r.orderId = :orderId")
    Optional<Reservation> findByOrderIdWithItems(UUID orderId);

    Optional<Reservation> findByOrderId(UUID orderId);

    List<Reservation> findByStatusAndCreatedAtBefore(ReservationStatusEnum status, Instant before);
}
