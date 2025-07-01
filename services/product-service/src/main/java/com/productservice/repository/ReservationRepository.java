package com.productservice.repository;

import com.productservice.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    int deleteByOrderId(Long orderId);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.items WHERE r.orderId = :orderId")
    Optional<Reservation> findByOrderIdWithItems(long orderId);
}
