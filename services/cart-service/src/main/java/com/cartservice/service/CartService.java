package com.cartservice.service;

import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.cartservice.exception.NotFoundException;
import com.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    public void addItemToCart() {

    }

    public long getCartIdByUserId(long userId) {
        return cartRepository.findCartIdByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart not found"));
    }

    public List<CartItem> getCartItemsByUserId(long userId) {
        long cartId = getCartIdByUserId(userId);

        return cartItemService.getCartItems(cartId);
    }

    private void createCart(long userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        cartRepository.save(cart);
    }


}
