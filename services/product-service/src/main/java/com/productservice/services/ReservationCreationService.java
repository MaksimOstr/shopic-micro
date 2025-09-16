package com.productservice.services;

import com.productservice.dto.request.CreateReservationDto;
import com.productservice.dto.request.CreateReservationItem;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.exceptions.InsufficientStockException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ReservationRepository;
import com.productservice.services.products.ProductQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.productservice.utils.Utils.extractIds;

@Service
@RequiredArgsConstructor
public class ReservationCreationService {
    private final ReservationRepository reservationRepository;
    private final ReservationItemService reservationItemService;
    private final ProductQueryService productQueryService;


    @Transactional
    public void createReservation(CreateReservationDto dto) {
        List<ItemForReservationDto> reservationItems = dto.reservationItems();

        checkAndDecreaseStockQuantity(reservationItems);

        Reservation reservation = createAndSaveReservation(dto.orderId());
        long reservationId = reservation.getId();

        mapToReservationItemDtoAndSave(reservationItems, reservationId);
    }

    private void checkAndDecreaseStockQuantity(List<ItemForReservationDto> reservationItems) {
        List<Long> productIds = extractIds(reservationItems);
        Map<Long, Product> productMap = getProductQuantityMap(productIds);

        for (ItemForReservationDto reservationItem : reservationItems) {
            Integer stockQuantity = productMap.get(reservationItem.productId()).getStockQuantity();

            if (stockQuantity == null) {
                throw new NotFoundException("There is no product with id " + reservationItem.productId());
            }

            if (reservationItem.quantity() > stockQuantity) {
                throw new InsufficientStockException("Insufficient stock for product " + reservationItem.productId());
            }
        }

        decreaseStockQuantity(reservationItems, productMap);
    }

    private void mapToReservationItemDtoAndSave(List<ItemForReservationDto> reservationItems, long reservationId) {
        List<CreateReservationItem> dtoList = reservationItems.stream()
                .map(item -> new CreateReservationItem(
                        item.productId(),
                        item.quantity(),
                        reservationId
                )).toList();

        reservationItemService.saveReservationItems(dtoList);
    }

    private void decreaseStockQuantity(List<ItemForReservationDto> reservationItems, Map<Long, Product> productMap) {
        for (ItemForReservationDto reservationItem : reservationItems) {
            Product product = productMap.get(reservationItem.productId());

            product.setStockQuantity(product.getStockQuantity() - reservationItem.quantity());
        }
    }

    private Reservation createAndSaveReservation(long orderId) {
        Reservation reservation = Reservation.builder()
                .orderId(orderId)
                .build();

        return reservationRepository.save(reservation);
    }

    private Map<Long, Product> getProductQuantityMap(List<Long> productIds) {
        List<Product> products = productQueryService.getProductsForUpdate(productIds);

        return products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }
}
