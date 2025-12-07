package com.cartservice.service;

import com.cartservice.entity.CartItem;
import com.cartservice.exception.NotFoundException;
import com.cartservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    private static final String CART_ITEM_NOT_FOUND = "CartItem not found";

    public CartItem getCartItemById(UUID cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND));
    }

    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    public int countCartItems(UUID cartId) {
        return cartItemRepository.countByCart_Id(cartId);
    }

    public void deleteCartItem(UUID id, UUID cartId) {
        cartItemRepository.deleteByCart_IdAndId(cartId, id);
    }

    public void deleteCartItem(UUID id) {
        cartItemRepository.deleteById(id);
    }

    public Optional<CartItem> getOptionalByCartIdAndProductId(UUID cartId, long productId) {
        return cartItemRepository.findByCart_IdAndProductId(cartId, productId);
    }
}
