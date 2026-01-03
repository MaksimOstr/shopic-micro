package com.productservice.services;

import com.productservice.dto.ReservationDto;
import com.productservice.dto.ReservationPreviewDto;
import com.productservice.dto.ProductReservedQuantity;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.entity.ReservationItem;
import com.productservice.entity.ReservationStatusEnum;
import com.productservice.exceptions.ApiException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.ReservationItemMapper;
import com.productservice.mapper.ReservationMapper;
import com.productservice.repository.ReservationItemRepository;
import com.productservice.repository.ReservationRepository;
import com.productservice.utils.SpecificationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
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
    private final ReservationItemRepository reservationItemRepository;

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
        List<ProductReservedQuantity> productReservedQuantityList = reservationItemRepository.findReservedQuantitiesByProductIdsAndStatus(productIds, ReservationStatusEnum.PENDING);
        List<Product> products = productService.getProductsByIdsWithLock(productIds);
        Map<UUID, ProductReservedQuantity> reservedMap = productReservedQuantityList.stream()
                .collect(Collectors.toMap(ProductReservedQuantity::productId, Function.identity()));
        Map<UUID, Product> productMap = toProductMap(products);
        List<ReservationItem> reservationItemList = new ArrayList<>();

        for (ItemForReservationDto reservationItem : reservationItems) {
            Product product = productMap.get(reservationItem.productId());
            ProductReservedQuantity reservationInfo = reservedMap.get(reservationItem.productId());

            long reservedQty = reservationInfo != null ? reservationInfo.reservedQuantity() : 0;
            long available = product.getStockQuantity() - reservedQty;

            if(available < reservationItem.quantity()) {
                throw new ApiException("Insufficient stock for product with id: " + product.getId(), HttpStatus.CONFLICT);
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
    public void cancelReservation(UUID orderId) {
        Reservation reservation = reservationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));

        reservation.changeStatus(ReservationStatusEnum.CANCELLED);
    }

    @Transactional
    public void completeReservation(UUID orderId) {
        Reservation reservation = reservationRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Reservation not found"));
        reservation.changeStatus(ReservationStatusEnum.COMPLETED);
        List<ReservationItem> items = reservationItemRepository.findByReservationIdWithProductsLocked(reservation.getId());

        for(ReservationItem item : items){
            Product product = item.getProduct();

            product.decreaseStock(item.getQuantity());
        }

        reservationRepository.save(reservation);
    }

    public ReservationDto getReservationAdminDto(UUID id) {
        Reservation reservation = reservationRepository.findByIdWithItems(id)
                .orElseThrow(() -> new NotFoundException("Reservation with id " + id + " not found"));

        return reservationMapper.toAdminReservationDto(reservation);
    }

    public ReservationDto getReservationAdminDtoByOrderId(UUID orderId) {
        Reservation reservation = reservationRepository.findByOrderIdWithItems(orderId)
                .orElseThrow(() -> new NotFoundException("Reservation with order id " + orderId + " not found"));

        return reservationMapper.toAdminReservationDto(reservation);
    }


    public Page<ReservationPreviewDto> getReservationList(Pageable pageable, ReservationStatusEnum status) {
        Specification<Reservation> spec = SpecificationUtils.equalsField("status", status);
        Page<Reservation> reservationPage = reservationRepository.findAll(spec, pageable);
        List<Reservation> reservationList = reservationPage.getContent();
        List<ReservationPreviewDto> previewDtoList = reservationMapper.toAdminReservationPreviewDtoList(reservationList);

        return new PageImpl<>(previewDtoList, pageable, reservationPage.getTotalElements());
    }
}
