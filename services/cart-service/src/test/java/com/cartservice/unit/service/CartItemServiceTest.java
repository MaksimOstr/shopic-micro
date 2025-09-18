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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartItemService cartItemService;

    private static final long CART_ITEM_ID = 1L;

    @Test
    public void testGetCartIdFromCartItem_whenCalledWithNonExistingItem_thenThrowException() {
        when(cartItemRepository.getCartIdByCartItemId(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            cartItemService.getCartIdFromCartItem(CART_ITEM_ID);
        });

        verify(cartItemRepository).getCartIdByCartItemId(CART_ITEM_ID);
    }

    @Test
    public void testGetCartItemById_whenCalledWithNonExistingItem_thenThrowException() {
        when(cartItemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            cartItemService.getCartItemById(CART_ITEM_ID);
        });

        verify(cartItemRepository).findById(CART_ITEM_ID);
    }
}