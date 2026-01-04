package com.productservice.services;

import com.productservice.dto.ReservationContext;
import com.productservice.dto.ReservationDto;
import com.productservice.dto.ReservationError;
import com.productservice.dto.ReservationPreviewDto;
import com.productservice.dto.ProductReservedQuantity;
import com.productservice.dto.ReservationResult;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.entity.ReservationItem;
import com.productservice.enums.ReservationErrorType;
import com.productservice.enums.ReservationStatusEnum;
import com.productservice.exceptions.ApiException;
import com.productservice.exceptions.NotFoundException;
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
    public ReservationResult createReservation(List<ItemForReservationDto> items, UUID orderId) {
        Reservation reservation = Reservation.builder()
                .status(ReservationStatusEnum.PENDING)
                .orderId(orderId)
                .build();

        ReservationContext context = loadContext(items);
        List<ReservationError> errors = validateReservationItems(items, context);

        if (!errors.isEmpty()) {
            return new ReservationResult(List.of(), errors);
        }

        List<ReservationItem> reservationItems = buildReservationItems(items, context, reservation);

        reservation.setItems(reservationItems);
        reservationRepository.save(reservation);

        return new ReservationResult(
                context.productMap().values().stream().toList(),
                List.of()
        );
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

        for (ReservationItem item : items) {
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

    public long getAvailableQuantityByProductId(UUID productId) {
        return reservationItemRepository.findReservedQuantityByProductIdAndStatus(productId, ReservationStatusEnum.PENDING);
    }


    public Page<ReservationPreviewDto> getReservationList(Pageable pageable, ReservationStatusEnum status) {
        Specification<Reservation> spec = SpecificationUtils.equalsField("status", status);
        Page<Reservation> reservationPage = reservationRepository.findAll(spec, pageable);
        List<Reservation> reservationList = reservationPage.getContent();
        List<ReservationPreviewDto> previewDtoList = reservationMapper.toAdminReservationPreviewDtoList(reservationList);

        return new PageImpl<>(previewDtoList, pageable, reservationPage.getTotalElements());
    }


    private ReservationContext loadContext(
            List<ItemForReservationDto> items
    ) {
        List<UUID> productIds = extractIds(items);

        List<Product> products =
                productService.getActiveProductsByIdsWithLock(productIds);

        Map<UUID, Product> productMap = toProductMap(products);

        Map<UUID, Long> reservedMap = reservationItemRepository.findReservedQuantitiesByProductIdsAndStatus(
                        productIds,
                        ReservationStatusEnum.PENDING
                )
                .stream()
                .collect(Collectors.toMap(
                        ProductReservedQuantity::productId,
                        ProductReservedQuantity::reservedQuantity
                ));

        return new ReservationContext(productMap, reservedMap);
    }

    private List<ReservationError> validateReservationItems(
            List<ItemForReservationDto> items,
            ReservationContext context
    ) {
        List<ReservationError> errors = new ArrayList<>();

        for (ItemForReservationDto dto : items) {
            Product product = context.productMap().get(dto.productId());

            if (product == null) {
                log.warn("Reservation failed: Product not found in DB. ProductId: {}", dto.productId());
                errors.add(new ReservationError(
                        dto.productId(),
                        ReservationErrorType.NOT_FOUND,
                        0,
                        0
                ));
                continue;
            }

            long reserved = context.reservedMap().getOrDefault(product.getId(), 0L);
            long available = product.getStockQuantity() - reserved;

            if (available < dto.quantity()) {
                log.warn("Reservation failed: Insufficient stock for Product {}. Requested: {}, Available: {})",
                        product.getId(), dto.quantity(), available);
                errors.add(new ReservationError(
                        dto.productId(),
                        ReservationErrorType.INSUFFICIENT_STOCK,
                        dto.quantity(),
                        available
                ));
            }
        }

        return errors;
    }

    private List<ReservationItem> buildReservationItems(
            List<ItemForReservationDto> items,
            ReservationContext context,
            Reservation reservation
    ) {
        List<ReservationItem> result = new ArrayList<>();

        for (ItemForReservationDto dto : items) {
            Product product = context.productMap().get(dto.productId());

            ReservationItem item = new ReservationItem();
            item.setProduct(product);
            item.setReservation(reservation);
            item.setQuantity(dto.quantity());

            result.add(item);
        }

        return result;
    }
}
