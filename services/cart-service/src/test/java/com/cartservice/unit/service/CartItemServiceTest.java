package com.cartservice.unit.service;

import com.cartservice.exception.NotFoundException;
import com.cartservice.repository.CartItemRepository;
import com.cartservice.service.CartItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartItemService cartItemService;

    private static final UUID CART_ITEM_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @Test
    public void testGetCartItemById_whenCalledWithNonExistingItem_thenThrowException() {
        when(cartItemRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            cartItemService.getCartItemById(CART_ITEM_ID);
        });

        verify(cartItemRepository).findById(CART_ITEM_ID);
    }
}
