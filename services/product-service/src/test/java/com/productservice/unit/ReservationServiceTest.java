package com.productservice.unit;

import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.entity.ReservationItem;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ReservationRepository;
import com.productservice.services.ReservationService;
import com.productservice.services.products.ProductQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ProductQueryService productQueryService;

    @InjectMocks
    private ReservationService reservationService;

    private static final long ORDER_ID = 1L;
    private static final long PRODUCT_ID_1 = 2L;
    private static final long PRODUCT_ID_2 = 3L;
    private static final int PRODUCT_QUANTITY_1 = 5;
    private static final int PRODUCT_QUANTITY_2 = 10;
    private static final int RESERVATION_QUANTITY_1 = 15;
    private static final int RESERVATION_QUANTITY_2 = 20;

    private Reservation reservation;
    private ReservationItem reservationItem1;
    private ReservationItem reservationItem2;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setup() {
        product1 = Product.builder()
                .id(PRODUCT_ID_1)
                .stockQuantity(PRODUCT_QUANTITY_1)
                .build();

        product2 = Product.builder()
                .id(PRODUCT_ID_2)
                .stockQuantity(PRODUCT_QUANTITY_2)
                .build();

        reservationItem1 = ReservationItem.builder()
                .product(product1)
                .quantity(RESERVATION_QUANTITY_1)
                .build();

        reservationItem2 = ReservationItem.builder()
                .product(product2)
                .quantity(RESERVATION_QUANTITY_2)
                .build();

        reservation = Reservation.builder()
                .orderId(ORDER_ID)
                .items(List.of(reservationItem1, reservationItem2))
                .build();
    }

    @Test
    public void testCancelReservation_whenCalledWithNotExistingReservation_thenThrowException() {
        when(reservationRepository.findByOrderIdWithItems(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
           reservationService.cancelReservation(ORDER_ID);
        });

        verify(reservationRepository).findByOrderIdWithItems(ORDER_ID);
        verifyNoInteractions(productQueryService);
        verifyNoMoreInteractions(reservationRepository);

        assertEquals(PRODUCT_QUANTITY_1, product1.getStockQuantity());
        assertEquals(PRODUCT_QUANTITY_2, product2.getStockQuantity());
    }

    @Test
    public void testCancelReservation_whenCalledWithExistingReservation_thenCancelReservationAndUpdateProductQuantity() {
        when(reservationRepository.findByOrderIdWithItems(anyLong())).thenReturn(Optional.of(reservation));
        when(productQueryService.getProductsForUpdate(anyList())).thenReturn(List.of(product1, product2));

        reservationService.cancelReservation(ORDER_ID);

        verify(reservationRepository).findByOrderIdWithItems(ORDER_ID);
        verify(productQueryService).getProductsForUpdate(List.of(PRODUCT_ID_1, PRODUCT_ID_2));
        verify(reservationRepository).delete(reservation);

        assertEquals(PRODUCT_QUANTITY_1 + RESERVATION_QUANTITY_1, product1.getStockQuantity());
        assertEquals(PRODUCT_QUANTITY_2 + RESERVATION_QUANTITY_2, product2.getStockQuantity());
    }

    @Test
    public void testCancelReservation_whenCalledWithExistingReservationAndNotExistingProduct_thenThrowException() {
        when(reservationRepository.findByOrderIdWithItems(anyLong())).thenReturn(Optional.of(reservation));
        when(productQueryService.getProductsForUpdate(anyList())).thenReturn(List.of(product1));

        reservationService.cancelReservation(ORDER_ID);

        verify(reservationRepository).findByOrderIdWithItems(ORDER_ID);
        verify(productQueryService).getProductsForUpdate(List.of(PRODUCT_ID_1, PRODUCT_ID_2));
        verify(reservationRepository).delete(reservation);

        assertEquals(PRODUCT_QUANTITY_2, product2.getStockQuantity());
        assertEquals(PRODUCT_QUANTITY_1 + RESERVATION_QUANTITY_1, product1.getStockQuantity());
    }
}
