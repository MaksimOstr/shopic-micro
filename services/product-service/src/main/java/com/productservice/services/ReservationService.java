package com.productservice.services;

import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.entity.ReservationItem;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ReservationRepository;
import com.productservice.services.products.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ProductQueryService productQueryService;

    @Transactional
    public void cancelReservation(long reservationId) {
        List<ReservationItem> reservationItemList = reservationRepository.findByIdWithItems(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + reservationId + " not found"))
                .getItems();

        List<Long> productIds = reservationItemList.stream().map(item -> item.getProduct().getId()).toList();

        List<Product> productList = productQueryService.getProductsForUpdate(productIds);

        updateProductQuantity(productList, reservationItemList);

        deleteReservation(reservationId);
    }

    public void deleteReservation(long reservationId) {
        int delete = reservationRepository.deleteById(reservationId);

        if(delete == 0) {
            throw new NotFoundException("Reservation with id " + reservationId + " not found");
        }
    }

    public void updateProductQuantity(List<Product> productList, List<ReservationItem> reservationItemList) {
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (ReservationItem item : reservationItemList) {
            Product product = productMap.get(item.getProduct().getId());
            if(product == null) {
                throw new NotFoundException("Product with id " + item.getProduct().getId() + " not found");
            }

            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
    }

    @Scheduled
    public void checkForUnpaidReservations() {
        Instant expirationThreshold = Instant.now().minus(30, ChronoUnit.MINUTES);

        List<Reservation> reservations = reservationRepository.findByCreatedAtBefore(expirationThreshold);
    }
}
