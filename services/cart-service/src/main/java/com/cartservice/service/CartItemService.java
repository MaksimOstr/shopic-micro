package com.cartservice.service;

import com.cartservice.entity.CartItem;
import com.cartservice.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;

    public List<CartItem> getCartItems(long cartId) {
        return cartItemRepository.findByCart_Id(cartId);
    }
}
