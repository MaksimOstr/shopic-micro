package com.productservice.unit;

import com.productservice.dto.request.CreateReservationDto;
import com.productservice.dto.request.CreateReservationItem;
import com.productservice.dto.request.ItemForReservationDto;
import com.productservice.entity.Product;
import com.productservice.entity.Reservation;
import com.productservice.exceptions.InsufficientStockException;
import com.productservice.exceptions.NotFoundException;
import com.productservice.repository.ReservationRepository;
import com.productservice.services.ProductService;
import com.productservice.services.ReservationCreationService;
import com.productservice.services.ReservationItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationCreationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationItemService reservationItemService;

    @Captor
    private ArgumentCaptor<Reservation> reservationArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<CreateReservationItem>> createReservationItemListArgumentCaptor;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ReservationCreationService reservationCreationService;


    private static final long RESERVATION_ID = 5L;
    private static final long ORDER_ID = 1L;
    private static final long PRODUCT_ID_1 = 2L;
    private static final long PRODUCT_ID_2 = 3L;
    private static final int PRODUCT_QUANTITY_1 = 5;
    private static final int PRODUCT_QUANTITY_2 = 10;
    private static final ItemForReservationDto ITEM_FOR_RESERVATION_DTO_1 = new ItemForReservationDto(PRODUCT_ID_1, PRODUCT_QUANTITY_1);
    private static final ItemForReservationDto ITEM_FOR_RESERVATION_DTO_2 = new ItemForReservationDto(PRODUCT_ID_2, PRODUCT_QUANTITY_2);
    private static final ItemForReservationDto ITEM_FOR_RESERVATION_DTO_3 = new ItemForReservationDto(PRODUCT_ID_2, PRODUCT_QUANTITY_2 + 5); // - DTO for unsufficient product stock quantity test
    private static final CreateReservationDto CREATE_RESERVATION_DTO = new CreateReservationDto(
            List.of(ITEM_FOR_RESERVATION_DTO_1, ITEM_FOR_RESERVATION_DTO_2),
            ORDER_ID
    );
    private static final CreateReservationDto CREATE_RESERVATION_DTO_2 = new CreateReservationDto(
            List.of(ITEM_FOR_RESERVATION_DTO_1, ITEM_FOR_RESERVATION_DTO_3),
            ORDER_ID
    ); // - DTO for unsufficient product stock quantity test


    private static Product product1;
    private static Product product2;
    private static Reservation reservation;

    @BeforeEach
    public void setUp() {
        product1 = Product.builder()
                .stockQuantity(PRODUCT_QUANTITY_1)
                .id(PRODUCT_ID_1)
                .build();

        product2 = Product.builder()
                .stockQuantity(PRODUCT_QUANTITY_2)
                .id(PRODUCT_ID_2)
                .build();

        reservation = Reservation.builder()
                .id(RESERVATION_ID)
                .orderId(ORDER_ID)
                .build();
    }

    @Test
    public void testCreateReservation_whenCalledWithCorrectArguments_thenCreateReservation() {
        when(productService.getProductsForUpdate(anyList())).thenReturn(List.of(product1, product2));
        when(reservationRepository.save(any(Reservation.class))).thenReturn(reservation);

        reservationCreationService.createReservation(CREATE_RESERVATION_DTO);

        verify(productService).getProductsForUpdate(List.of(PRODUCT_ID_1, PRODUCT_ID_2));
        verify(reservationRepository).save(reservationArgumentCaptor.capture());
        verify(reservationItemService).saveReservationItems(createReservationItemListArgumentCaptor.capture());

        Reservation capturedReservation = reservationArgumentCaptor.getValue();
        List<CreateReservationItem> capturedCreateReservationItemList = createReservationItemListArgumentCaptor.getValue();

        assertEquals(PRODUCT_QUANTITY_1 - ITEM_FOR_RESERVATION_DTO_1.quantity(), product1.getStockQuantity());
        assertEquals(PRODUCT_QUANTITY_2 - ITEM_FOR_RESERVATION_DTO_2.quantity(), product2.getStockQuantity());
        assertEquals(ORDER_ID, capturedReservation.getOrderId());

        assertThat(capturedCreateReservationItemList)
                .hasSize(2)
                .extracting(CreateReservationItem::productId,
                        CreateReservationItem::quantity,
                        CreateReservationItem::reservationId)
                .containsExactly(
                        tuple(PRODUCT_ID_1, PRODUCT_QUANTITY_1, RESERVATION_ID),
                        tuple(PRODUCT_ID_2, PRODUCT_QUANTITY_2, RESERVATION_ID)
                );
    }

    @Test
    public void testCreateReservation_whenCalledNonExistingProduct_thenThrowException() {
        when(productService.getProductsForUpdate(anyList())).thenReturn(List.of(product1));

        assertThrows(NotFoundException.class, () -> {
            reservationCreationService.createReservation(CREATE_RESERVATION_DTO);
        });

        verify(productService).getProductsForUpdate(List.of(PRODUCT_ID_1, PRODUCT_ID_2));
        verifyNoInteractions(reservationRepository, reservationItemService);

        assertEquals(PRODUCT_QUANTITY_1, product1.getStockQuantity());
        assertEquals(PRODUCT_QUANTITY_2, product2.getStockQuantity());
    }

    @Test
    public void testCreateReservation_whenCalledUnsufficientStock_thenThrowException() {
        when(productService.getProductsForUpdate(anyList())).thenReturn(List.of(product1, product2));

        assertThrows(InsufficientStockException.class, () -> {
            reservationCreationService.createReservation(CREATE_RESERVATION_DTO_2);
        });

        verify(productService).getProductsForUpdate(List.of(PRODUCT_ID_1, PRODUCT_ID_2));
        verifyNoInteractions(reservationRepository, reservationItemService);

        assertEquals(PRODUCT_QUANTITY_1, product1.getStockQuantity());
        assertEquals(PRODUCT_QUANTITY_2, product2.getStockQuantity());
    }
}
