package com.productservice.services;

import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.entity.ReservationItem;
import com.productservice.entity.ReservationStatusEnum;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ReservationRepository;
import com.productservice.services.products.ProductQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
        reservation.setStatus(ReservationStatusEnum.CANCELLED);
    }

    public void updateReservationStatus(long orderId, ReservationStatusEnum status) {
        int updated = reservationRepository.updateStatus(orderId, status);

        if(updated == 0) {
            log.error("Failed to update reservation status for order with order id {}", orderId);
            throw new NotFoundException("Reservation with order id " + orderId + " not found");
        }
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
