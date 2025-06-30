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

    @Transactional
    @Modifying
    @Query("DELETE FROM Reservation r WHERE r.id = :id")
    int deleteById(long id);

    @Query("SELECT r FROM Reservation r JOIN FETCH r.items WHERE r.id = :id")
    Optional<Reservation> findByIdWithItems(long id);

    @Query("SELECT r FROM Reservation r WHERE r.createdAt < :threshold")
    List<Reservation> findByCreatedAtBefore(Instant threshold);
}
