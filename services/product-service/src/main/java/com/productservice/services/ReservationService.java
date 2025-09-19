package com.productservice.services;

import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.entity.ReservationItem;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ReservationRepository;
import com.productservice.services.products.ProductQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.productservice.utils.ProductUtils.toProductMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ProductQueryService productQueryService;

    @Transactional
    public void cancelReservation(long orderId) {
        Reservation reservation = reservationRepository.findByOrderIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + orderId + " not found"));
        List<ReservationItem> reservationItemList = reservation.getItems();
        List<Long> productIds = reservationItemList.stream().map(item -> item.getProduct().getId()).toList();
        List<Product> productList = productQueryService.getProductsForUpdate(productIds);

        updateProductQuantity(productList, reservationItemList);
        reservationRepository.delete(reservation);
    }

    public void deleteReservationByOrderId(long orderId) {
        reservationRepository.deleteByOrderId(orderId);
    }

    private void updateProductQuantity(List<Product> productList, List<ReservationItem> reservationItemList) {
        Map<Long, Product> productMap = toProductMap(productList);

        for (ReservationItem item : reservationItemList) {
            Product product = productMap.get(item.getProduct().getId());
            if(product == null) {
                log.error("Product with id {} not found", item.getProduct().getId());
                continue;
            }

            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
    }
}
