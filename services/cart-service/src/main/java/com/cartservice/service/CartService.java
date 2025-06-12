package com.cartservice.service;

import com.cartservice.dto.CreateCartItem;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.cartservice.exception.NotFoundException;
import com.cartservice.projection.CartItemProjection;
import com.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    public void addItemToCart(AddItemToCartRequest dto, long userId) {
        Long cartId = cartRepository.findCartIdByUserId(userId)
                .orElseGet(() -> createCart(userId).getId());


        CreateCartItem createCartItem = new CreateCartItem(
                dto.productId(),
                cartId,
                dto.quantity()
        );

        cartItemService.createCartItem(createCartItem);
    }

    public Optional<Long> getCartIdByUserId(long userId) {
        return cartRepository.findCartIdByUserId(userId);
    }

    public List<CartItemProjection> getCartItemsByUserId(long userId) {
        return getCartIdByUserId(userId)
                .map(cartItemService::getCartItems)
                .orElseGet(ArrayList::new);
    }

    private Cart createCart(long userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        return cartRepository.save(cart);
    }


}
