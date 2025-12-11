package com.cartservice.controller;

import com.cartservice.dto.CartDto;
import com.cartservice.dto.CartItemDto;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.dto.request.ChangeCartItemQuantityRequest;
import com.cartservice.dto.response.ErrorResponseDto;
import com.cartservice.security.CustomPrincipal;
import com.cartservice.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Cart", description = "Cart management endpoints")
public class CartController {
    private final CartService cartService;

    @Operation(
            summary = "Get current cart",
            description = "Returns cart items and total price for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart returned.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<CartDto> getCart(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        CartDto cart = cartService.getCart(principal.getId());

        return ResponseEntity.ok(cart);
    }

    @Operation(
            summary = "Add item to cart",
            description = "Adds a new item or increments quantity if the product already exists in cart."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item added or updated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CartItemDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid input data",
                                            value = """
                                                    {
                                                        "quantity": "must be greater than or equal to 0"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "If product is out of stock",
                                            value = """
                                                    {
                                                        "code": "Bad request",
                                                        "status": 400,
                                                        "message": "Insufficient stock"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/items")
    public ResponseEntity<CartItemDto> addCartItem(
            @AuthenticationPrincipal CustomPrincipal principal,
            @RequestBody @Valid AddItemToCartRequest body
    ) {
        CartItemDto cartItem = cartService.addItemToCart(body, principal.getId());

        return ResponseEntity.ok(cartItem);
    }

    @Operation(
            summary = "Delete cart item",
            description = "Deletes a specific item from cart by id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Item deleted."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart or item not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        cartService.deleteItemFromCart(id, principal.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Delete cart",
            description = "Removes the entire cart for the authenticated user."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Cart deleted."
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @DeleteMapping()
    public ResponseEntity<Void> deleteCart(
            @AuthenticationPrincipal CustomPrincipal principal
    ) {
        cartService.deleteCartByUserId(principal.getId());

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Update cart item",
            description = "Sets a new quantity for a cart item or removes it when amount is 0."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Quantity updated or item removed."
            ),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(
                            examples = @ExampleObject(
                                    value = """
                                                {
                                                 "amount": "must be greater than or equal to 0"
                                                }
                                            """)
                    )),
            @ApiResponse(
                    responseCode = "404",
                    description = "Cart item not found.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "User not authenticated.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
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
