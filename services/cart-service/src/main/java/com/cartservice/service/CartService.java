package com.cartservice.service;

import com.cartservice.dto.CreateCartItemDto;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.dto.request.DecreaseCartItemQuantity;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.cartservice.exception.NotFoundException;
import com.cartservice.projection.CartItemProjection;
import com.cartservice.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;

    public void addItemToCart(AddItemToCartRequest dto, long userId) {
        Long cartId = cartRepository.findCartIdByUserId(userId)
                .orElseGet(() -> createCart(userId).getId());


        CreateCartItemDto createCartItem = new CreateCartItemDto(
                dto.productId(),
                cartId,
                dto.quantity()
        );

        cartItemService.createCartItem(createCartItem);
    }

    public List<CartItemProjection> getCartItemsByUserId(long userId) {
        return cartRepository.findCartIdByUserId(userId)
                .map(cartItemService::getCartItems)
                .orElseGet(ArrayList::new);
    }

    public void removeItemFromCart(long userId, long productId) {
        long cartId = getCartIdByUserId(userId);

        cartItemService.deleteCartItem(cartId, productId);
        deleteCartIfEmpty(cartId);
    }

    @Transactional
    public void decreaseCartItemQuantity(DecreaseCartItemQuantity dto, long userId) {
        long cartId = getCartIdByUserId(userId);
        CartItem cartItem = cartItemService.getCartItem(cartId, dto.productId());
        int currentQuantity = cartItem.getQuantity();

        if(cartItem.getQuantity() <= dto.amount()) {
            cartItemService.deleteCartItem(cartId, dto.productId());
            deleteCartIfEmpty(cartId);
        } else {
            cartItem.setQuantity(currentQuantity - dto.amount());
        }
    }


    private long getCartIdByUserId(long userId) {
        return cartRepository.findCartIdByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart was not found"));
    }

    private void deleteCartIfEmpty(long cartId) {
        int cartItemsCount = cartItemService.countCartItems(cartId);
        if(cartItemsCount == 0) {
            cartRepository.deleteById(cartId);
        }
    }

    private Cart createCart(long userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        return cartRepository.save(cart);
    }
}
