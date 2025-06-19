package com.productservice.services;

import com.productservice.entity.Product;
import com.productservice.entity.ReservationItem;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ProductService productService;

    @Transactional
    public void cancelReservation(long reservationId) {
        List<ReservationItem> reservationItemList = reservationRepository.findByIdWithItems(reservationId)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + reservationId + " not found"))
                .getItems();

        List<Long> productIds = reservationItemList.stream().map(item -> item.getProduct().getId()).toList();

        List<Product> productList = productService.getProductsForUpdate(productIds);

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
}
