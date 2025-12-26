package com.productservice.repository;

import com.productservice.dto.ProductReservedQuantity;
import com.productservice.entity.ReservationItem;
import com.productservice.entity.ReservationStatusEnum;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

@Repository
public interface ReservationItemRepository extends JpaRepository<ReservationItem, UUID> {
    @Query("""
                SELECT ri.product.id AS productId, SUM(ri.quantity) AS reservedQuantity
                FROM ReservationItem ri
                JOIN ri.reservation r
                WHERE ri.product.id IN :productIds
                  AND r.status = :status
                GROUP BY ri.product.id
            """)
    List<ProductReservedQuantity> findReservedQuantitiesByProductIdsAndStatus(
            @Param("productIds") List<UUID> productIds,
            @Param("status") ReservationStatusEnum status
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT ri FROM ReservationItem ri
            JOIN FETCH ri.product
            WHERE ri.reservation.id = :reservationId
            """)
    List<ReservationItem> findByReservationIdWithProductsLocked(UUID reservationId);
}
