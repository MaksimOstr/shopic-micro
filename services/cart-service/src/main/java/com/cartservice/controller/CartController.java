package com.cartservice.controller;

import com.cartservice.config.security.model.CustomPrincipal;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/carts")
@PreAuthorize("hasRole('USER')")
public class CartController {
    private final CartService cartService;

    @GetMapping("/me")
    public ResponseEntity<CartDto> getCart(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        CartDto cart = cartService.getCart(principal.getId());

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/me/items")
    public ResponseEntity<CartItemDto> addCartItem(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid AddItemToCartRequest body
    ) {
        CartItemDto cartItem = cartService.addItemToCart(body, principal.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItem);
    }

    @DeleteMapping("/me/items/{id}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable long id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        cartService.deleteItemFromCart(id, principal.getId());

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCart(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        cartService.deleteCartByUserId(principal.getId());

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/items/{id}/quantity")
    public ResponseEntity<Void> changeCartItemQuantity(
            @RequestBody @Valid ChangeCartItemQuantityRequest body,
            @PathVariable long id
    ) {
        cartService.changeCartItemQuantity(body, id);

        return ResponseEntity.ok().build();
    }
}
