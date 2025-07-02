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

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ProductQueryService productQueryService;

    @Transactional
    public void cancelReservation(long orderId) {
        List<ReservationItem> reservationItemList = reservationRepository.findByOrderIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + orderId + " not found"))
                .getItems();
        List<Long> productIds = reservationItemList.stream().map(item -> item.getProduct().getId()).toList();
        List<Product> productList = productQueryService.getProductsForUpdate(productIds);

        updateProductQuantity(productList, reservationItemList);
        deleteReservation(orderId);
    }

    public void deleteReservation(long orderId) {
        int delete = reservationRepository.deleteByOrderId(orderId);

        if(delete == 0) {
            throw new NotFoundException("Reservation with orderId: " + orderId + " not found");
        }
    }

    public void updateProductQuantity(List<Product> productList, List<ReservationItem> reservationItemList) {
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        for (ReservationItem item : reservationItemList) {
            Product product = productMap.get(item.getProduct().getId());
            if(product == null) {
                log.error("Product with id " + item.getProduct().getId() + " not found");
                throw new NotFoundException("Product with id " + item.getProduct().getId() + " not found");
            }

            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
    }
}
