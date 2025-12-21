package com.cartservice.service;

import com.cartservice.dto.CartDto;
import com.cartservice.dto.CartItemDto;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.dto.request.ChangeCartItemQuantityRequest;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.cartservice.exception.ApiException;
import com.cartservice.exception.NotFoundException;
import com.cartservice.mapper.CartItemMapper;
import com.cartservice.mapper.CartMapper;
import com.cartservice.repository.CartRepository;
import com.cartservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;
    private final GrpcProductService grpcProductService;
    private final CartMapper cartMapper;

    @Transactional
    public CartItemDto addItemToCart(AddItemToCartRequest dto, UUID userId) {
        Cart cart = cartRepository.findCartWithItemsByUserId(userId)
                .orElseGet(() -> createCart(userId));

        return cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(dto.productId()))
                .findFirst()
                .map(existing -> updateExistingItem(dto, existing))
                .orElseGet(() -> createNewItem(dto, cart));
    }

    @Transactional(readOnly = true)
    public CartDto getCart(UUID userId) {
        return cartRepository.findCartWithItemsByUserId(userId)
                .map(cartMapper::toDto)
                .orElseGet(() -> new CartDto(
                        Collections.emptyList(),
                        BigDecimal.ZERO
                ));
    }

    @Transactional(readOnly = true)
    public List<CartItemDto> getCartItemsForOrder(UUID userId) {
        Cart cart = getCartWithItemsByUserId(userId);

        return cartItemMapper.toDtoList(cart.getCartItems());
    }

    @Transactional
    public void deleteItemFromCart(UUID itemId, UUID userId) {
        Cart cart = getCartWithItemsByUserId(userId);

        CartItem item = cart.getCartItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ApiException("Cart item not found", HttpStatus.NOT_FOUND));

        cart.getCartItems().remove(item);
        deleteCartIfEmpty(cart);
    }

    @Transactional
    public void updateCartItem(ChangeCartItemQuantityRequest dto, UUID itemId, UUID userId) {
        Cart cart = getCartWithItemsByUserId(userId);
        List<CartItem> cartItems = cart.getCartItems();
        CartItem selectedItem = cartItems.stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ApiException("Cart item not found", HttpStatus.NOT_FOUND));


        if (dto.amount() <= 0) {
            cartItems.remove(selectedItem);
            deleteCartIfEmpty(cart);
        } else {
            selectedItem.setQuantity(dto.amount());
        }
    }

    public void deleteCartByUserId(UUID userId) {
        cartRepository.deleteCartByUserId(userId);
    }

    private Cart getCartWithItemsByUserId(UUID userId) {
        return cartRepository.findCartWithItemsByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Cart Not Found"));
    }

    private void deleteCartIfEmpty(Cart cart) {
        if (cart.getCartItems().isEmpty()) {
            cartRepository.delete(cart);
        }
    }

    private Cart createCart(UUID userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        return cartRepository.save(cart);
    }

    private CartItemDto createNewItem(AddItemToCartRequest dto, Cart cart) {
        Product product = getProductByIdAndCheckQuantity(dto.productId(), dto.quantity());
        CartItem newCartItem = CartItem.builder()
                .priceAtAdd(new BigDecimal(product.getPrice()))
                .productName(product.getProductName())
                .quantity(dto.quantity())
                .productId(dto.productId())
                .cart(cart)
                .productImageUrl(product.getProductImageUrl())
                .build();

        return cartItemMapper.toDto(newCartItem);
    }

    private CartItemDto updateExistingItem(AddItemToCartRequest dto, CartItem cartItem) {
        getProductByIdAndCheckQuantity(dto.productId(), dto.quantity() + cartItem.getQuantity());
        cartItem.setQuantity(cartItem.getQuantity() + dto.quantity());
        return cartItemMapper.toDto(cartItem);
    }

    private Product getProductByIdAndCheckQuantity(UUID productId, int quantity) {
        Product product = grpcProductService.getProductById(productId);

        if (product.getAvailableQuantity() < quantity) {
            throw new ApiException(
                    "Insufficient stock for product. Requested: %d, Available: %d"
                            .formatted(quantity, product.getAvailableQuantity()),
                    HttpStatus.BAD_REQUEST
            );
        }

        return product;
    }
}
