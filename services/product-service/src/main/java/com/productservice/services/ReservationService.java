package com.productservice.services;

import com.productservice.dto.AdminReservationDto;
import com.productservice.dto.AdminReservationPreviewDto;
import com.productservice.dto.ProductReservedQuantity;
import com.productservice.dto.request.CreateReservationDto;
import com.productservice.dto.request.CreateReservationItem;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.entity.ReservationItem;
import com.productservice.entity.ReservationStatusEnum;
import com.productservice.exceptions.InsufficientStockException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.ReservationMapper;
import com.productservice.repository.ReservationItemRepository;
import com.productservice.repository.ReservationRepository;
import com.productservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ApiException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.productservice.utils.ProductUtils.toProductMap;
import static com.productservice.utils.Utils.extractIds;


@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ProductService productService;
    private final ReservationMapper reservationMapper;
    private final ReservationItemRepository findReservedQuantitiesByProductIdsAndStatus;

    @Transactional
    public void createReservation(List<ItemForReservationDto> reservationItems, UUID orderId) {
        Reservation reservation = Reservation.builder()
                .status(ReservationStatusEnum.PENDING)
                .orderId(orderId)
                .build();
        List<ReservationItem> reservationItemList = createReservationItems(reservationItems, reservation);

        reservation.setItems(reservationItemList);

        reservationRepository.save(reservation);
    }

    private List<ReservationItem> createReservationItems(List<ItemForReservationDto> reservationItems, Reservation reservation) {
        List<UUID> productIds = extractIds(reservationItems);
        List<ProductReservedQuantity> productReservedQuantityList = findReservedQuantitiesByProductIdsAndStatus.findReservedQuantitiesByProductIdsAndStatus(productIds, ReservationStatusEnum.PENDING);
        List<Product> products = productService.getProductsByIdsWithLock(productIds);
        Map<UUID, ProductReservedQuantity> reservedMap = productReservedQuantityList.stream()
                .collect(Collectors.toMap(ProductReservedQuantity::productId, Function.identity()));
        Map<UUID, Product> productMap = toProductMap(products);
        List<ReservationItem> reservationItemList = new ArrayList<>();

        for (ItemForReservationDto reservationItem : reservationItems) {
            Product product = productMap.get(reservationItem.productId());
            ProductReservedQuantity reservationInfo = reservedMap.get(reservationItem.productId());

            int reservedQty = reservationInfo != null ? reservationInfo.reservedQuantity() : 0;
            int available = product.getStockQuantity() - reservedQty;

            if(available < reservationItem.quantity()){
                throw new ApiException("Insufficient stock for product with id: " + product.getId());
            }

            ReservationItem reservationItemEntity = new ReservationItem();
            reservationItemEntity.setProduct(product);
            reservationItemEntity.setReservation(reservation);
            reservationItemEntity.setQuantity(reservationItem.quantity());

            reservationItemList.add(reservationItemEntity);
        }

        return reservationItemList;
    }


    @Transactional
    public void cancelReservation(long orderId) {
        Reservation reservation = reservationRepository.findByOrderIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + orderId + " not found"));

        if(reservation.getStatus().equals(ReservationStatusEnum.CANCELLED)) {
            log.info("Reservation already cancelled for order: {}", orderId);
            return;
        }

        List<ReservationItem> reservationItemList = reservation.getItems();
        List<Long> productIds = reservationItemList.stream().map(item -> item.getProduct().getId()).toList();
        List<Product> productList = productService.getProductsForUpdate(productIds);

        updateProductQuantity(productList, reservationItemList);
        reservation.setStatus(ReservationStatusEnum.CANCELLED);
    }

    public AdminReservationDto getReservationAdminDto(long id) {
        Reservation reservation = reservationRepository.findByIdWithItems(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));

        return reservationMapper.toAdminReservationDto(reservation);
    }

    public AdminReservationDto getReservationAdminDtoByOrderId(long orderId) {
        Reservation reservation = reservationRepository.findByOrderIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException("Reservation with order id " + orderId + " not found"));

        return reservationMapper.toAdminReservationDto(reservation);
    }


    public Page<AdminReservationPreviewDto> getAdminReservationPreviewDtoList(Pageable pageable, ReservationStatusEnum status) {
        Specification<Reservation> spec = SpecificationUtils.equalsEnum("status", status);
        Page<Reservation> reservationPage = reservationRepository.findAll(spec, pageable);
        List<Reservation> reservationList = reservationPage.getContent();
        List<AdminReservationPreviewDto> previewDtoList = reservationMapper.toAdminReservationPreviewDtoList(reservationList);

        return new PageImpl<>(previewDtoList, pageable, reservationPage.getTotalElements());
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
        System.out.println(productMap);
        for (ReservationItem item : reservationItemList) {
            System.out.println(item);
            Product product = productMap.get(item.getProduct().getId());
            System.out.println(product);
            if(product == null) {
                log.error("Product with id {} not found", item.getProduct().getId());
                throw new  NotFoundException("Product with id " + item.getProduct().getId() + " not found");
            }
            System.out.println(product.getStockQuantity() + item.getQuantity());
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }
    }
}
