package com.cartservice.controller;

import com.cartservice.config.security.model.CustomPrincipal;
import com.cartservice.entity.CartItem;
import com.cartservice.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItem>> getCartItems(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<CartItem> cartItems = cartService.getCartItemsByUserId(principal.getId());

        return ResponseEntity.ok(cartItems);
    }
}
