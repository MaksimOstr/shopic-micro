package com.cartservice.service;


import com.cartservice.entity.CartItem;
import com.cartservice.exception.NotFoundException;
import com.cartservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    private static final String CART_ITEM_NOT_FOUND = "CartItem not found";

    public long getCartIdFromCartItem(long cartItemId) {
        return cartItemRepository.getCartIdByCartItemId(cartItemId)
                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND));
    }

    public CartItem getCartItemById(long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException(CART_ITEM_NOT_FOUND));
    }

    public CartItem save(CartItem cartItem) {
        return cartItemRepository.save(cartItem);
    }

    public int countCartItems(long cartId) {
        return cartItemRepository.countByCart_Id(cartId);
    }

    public void deleteCartItemById(long id) {
        int deleted = cartItemRepository.deleteById(id);

        if(deleted == 0) {
            throw new NotFoundException(CART_ITEM_NOT_FOUND);
        }
    }

    public Optional<CartItem> getOptionalByCartIdAndProductId(long cartId, long productId) {
        return cartItemRepository.findByCart_IdAndProductId(cartId, productId);
    }
}
