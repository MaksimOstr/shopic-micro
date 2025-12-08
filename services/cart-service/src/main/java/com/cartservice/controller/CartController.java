package com.cartservice.controller;

import com.cartservice.security.CustomPrincipal;
import com.cartservice.dto.CartDto;
import com.cartservice.dto.CartItemDto;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.dto.request.ChangeCartItemQuantityRequest;
import com.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
@PreAuthorize("hasRole('USER')")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartDto> getCart(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        CartDto cart = cartService.getCart(principal.getId());

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemDto> addCartItem(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid AddItemToCartRequest body
    ) {
        CartItemDto cartItem = cartService.addItemToCart(body, principal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        cartService.deleteItemFromCart(id, principal.getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> deleteCart(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        cartService.deleteCartByUserId(principal.getId());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<Void> updateCartItem(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid ChangeCartItemQuantityRequest body,
            @PathVariable UUID id
    ) {
        cartService.updateCartItem(body, id, principal.getId());

        return ResponseEntity.ok().build();
    }
}
