package com.cartservice.controller;

import com.cartservice.config.security.model.CustomPrincipal;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.dto.request.ChangeCartItemQuantity;
import com.cartservice.entity.CartItem;
import com.cartservice.projection.CartItemProjection;
import com.cartservice.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartService cartService;

    @GetMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<CartItemProjection>> getCartItems(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        List<CartItemProjection> cartItems = cartService.getCartItemsByUserId(principal.getId());

        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CartItem> addCartItem(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid AddItemToCartRequest body
    ) {
        cartService.addItemToCart(body, principal.getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteCartItem(
            @PathVariable long id

    ) {
        cartService.removeItemFromCart(id);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> deleteCart(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        cartService.deleteCartByUserId(principal.getId());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/items/quantity")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> changeCartItemQuantity(
            @RequestBody @Valid ChangeCartItemQuantity body
    ) {
        cartService.changeCartItemQuantity(body);

        return ResponseEntity.ok().build();
    }
}
