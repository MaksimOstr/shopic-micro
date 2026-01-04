package com.productservice.unit;

import com.productservice.dto.ProductReservedQuantity;
import com.productservice.dto.ReservationDto;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.entity.ReservationItem;
import com.productservice.enums.ReservationStatusEnum;
import com.productservice.exceptions.ApiException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.mapper.ReservationMapper;
import com.productservice.repository.ReservationItemRepository;
import com.productservice.repository.ReservationRepository;
import com.productservice.services.ProductService;
import com.productservice.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationItemRepository reservationItemRepository;

    @Mock
    private ProductService productService;

    @Mock
    private ReservationMapper reservationMapper;

    private UUID orderId;
    private UUID productId;
    private Product product;

    @BeforeEach
    void setUp() {
        orderId = UUID.randomUUID();
        productId = UUID.randomUUID();

        product = new Product();
        product.setId(productId);
        product.setStockQuantity(10);
    }


    @Test
    void cancelReservation_whenCalledWithNotExistingReservation_thenThrowException() {
        when(reservationRepository.findByOrderId(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> reservationService.cancelReservation(orderId)
        );

        verify(reservationRepository).findByOrderId(orderId);
        verifyNoMoreInteractions(reservationRepository);
    }

    @Test
    void cancelReservation_whenReservationIsCompleted_thenThrowException() {
        Reservation reservation = Reservation.builder()
                .orderId(orderId)
                .status(ReservationStatusEnum.COMPLETED)
                .build();

        when(reservationRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(reservation));

        ApiException ex = assertThrows(
                ApiException.class,
                () -> reservationService.cancelReservation(orderId)
        );

        assertEquals("Cannot change status of a completed reservation", ex.getMessage());
    }

    @Test
    void cancelReservation_whenReservationIsCancelled_thenThrowException() {
        Reservation reservation = Reservation.builder()
                .orderId(orderId)
                .status(ReservationStatusEnum.CANCELLED)
                .build();

        when(reservationRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(reservation));

        ApiException ex = assertThrows(
                ApiException.class,
                () -> reservationService.cancelReservation(orderId)
        );

        assertEquals("Cannot change status of a cancelled reservation", ex.getMessage());
    }

    @Test
    void cancelReservation_whenReservationIsPending_thenChangeStatusToCancelled() {
        Reservation reservation = Reservation.builder()
                .orderId(orderId)
                .status(ReservationStatusEnum.PENDING)
                .build();

        when(reservationRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(reservation));

        reservationService.cancelReservation(orderId);

        assertEquals(ReservationStatusEnum.CANCELLED, reservation.getStatus());
    }

    @Test
    void completeReservation_whenReservationNotFound_thenThrowException() {
        when(reservationRepository.findByOrderId(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> reservationService.completeReservation(orderId)
        );
    }

    @Test
    void completeReservation_whenReservationIsCancelled_thenThrowException() {
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .status(ReservationStatusEnum.CANCELLED)
                .build();

        when(reservationRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                ApiException.class,
                () -> reservationService.completeReservation(orderId)
        );
    }

    @Test
    void completeReservation_whenReservationIsPending_thenDecreaseStockAndComplete() {
        Reservation reservation = Reservation.builder()
                .id(UUID.randomUUID())
                .orderId(orderId)
                .status(ReservationStatusEnum.PENDING)
                .build();

        ReservationItem item = new ReservationItem();
        item.setProduct(product);
        item.setQuantity(3);

        when(reservationRepository.findByOrderId(orderId))
                .thenReturn(Optional.of(reservation));

        when(reservationItemRepository.findByReservationIdWithProductsLocked(reservation.getId()))
                .thenReturn(List.of(item));

        reservationService.completeReservation(orderId);

        assertEquals(ReservationStatusEnum.COMPLETED, reservation.getStatus());
        assertEquals(7, product.getStockQuantity());

        verify(reservationRepository).save(reservation);
    }

    @Test
    void createReservation_whenInsufficientStock_thenThrowException() {
        ItemForReservationDto itemDto =
                new ItemForReservationDto(productId, 10);

        when(reservationItemRepository
                .findReservedQuantitiesByProductIdsAndStatus(
                        List.of(productId),
                        ReservationStatusEnum.PENDING))
                .thenReturn(List.of(
                        new ProductReservedQuantity(productId, 8)
                ));

        when(productService.getProductsByIdsWithLock(List.of(productId)))
                .thenReturn(List.of(product));

        assertThrows(
                ApiException.class,
                () -> reservationService.createReservation(List.of(itemDto), orderId)
        );
    }

    @Test
    void createReservation_whenStockIsEnough_thenSaveReservation() {
        ItemForReservationDto itemDto =
                new ItemForReservationDto(productId, 3);

        when(reservationItemRepository
                .findReservedQuantitiesByProductIdsAndStatus(
                        List.of(productId),
                        ReservationStatusEnum.PENDING))
                .thenReturn(List.of());

        when(productService.getProductsByIdsWithLock(List.of(productId)))
                .thenReturn(List.of(product));

        reservationService.createReservation(List.of(itemDto), orderId);

        ArgumentCaptor<Reservation> captor =
                ArgumentCaptor.forClass(Reservation.class);

        verify(reservationRepository).save(captor.capture());

        Reservation saved = captor.getValue();

        assertEquals(orderId, saved.getOrderId());
        assertEquals(ReservationStatusEnum.PENDING, saved.getStatus());
        assertEquals(1, saved.getItems().size());
    }

    @Test
    void getReservationAdminDtoByOrderId_whenReservationExists_thenReturnDto() {
        Reservation reservation = Reservation.builder()
                .orderId(orderId)
                .status(ReservationStatusEnum.PENDING)
                .build();

        ReservationDto dto = mock(ReservationDto.class);

        when(reservationRepository.findByOrderIdWithItems(orderId))
                .thenReturn(Optional.of(reservation));
        when(reservationMapper.toAdminReservationDto(reservation))
                .thenReturn(dto);

        ReservationDto result =
                reservationService.getReservationAdminDtoByOrderId(orderId);

        assertSame(dto, result);
    }

    @Test
    void getReservationAdminDtoByOrderId_whenReservationNotFound_thenThrowException() {
        when(reservationRepository.findByOrderIdWithItems(orderId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> reservationService.getReservationAdminDtoByOrderId(orderId)
        );
    }

    @Test
    void getReservationAdminDto_whenReservationExists_thenReturnDto() {
        UUID reservationId = UUID.randomUUID();

        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatusEnum.PENDING)
                .build();

        ReservationDto dto = mock(ReservationDto.class);

        when(reservationRepository.findByIdWithItems(reservationId))
                .thenReturn(Optional.of(reservation));
        when(reservationMapper.toAdminReservationDto(reservation))
                .thenReturn(dto);

        ReservationDto result =
                reservationService.getReservationAdminDto(reservationId);

        assertSame(dto, result);
    }

    @Test
    void getReservationAdminDto_whenReservationNotFound_thenThrowException() {
        UUID reservationId = UUID.randomUUID();

        when(reservationRepository.findByIdWithItems(reservationId))
                .thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> reservationService.getReservationAdminDto(reservationId)
        );
    }
}


