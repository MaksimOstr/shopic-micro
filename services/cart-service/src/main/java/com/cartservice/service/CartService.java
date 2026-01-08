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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemMapper cartItemMapper;
    private final GrpcProductService grpcProductService;
    private final CartMapper cartMapper;

    @Transactional
    public CartDto addItemToCart(AddItemToCartRequest dto, UUID userId) {
        Cart cart = cartRepository.findCartWithItemsByUserId(userId)
                .orElseGet(() -> createCart(userId));

        Optional<CartItem> existingItemOpt = cart.getCartItems().stream()
                .filter(item -> item.getProductId().equals(dto.productId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            updateExistingItem(dto, existingItemOpt.get());
        } else {
            createNewItem(dto, cart);
        }

        cartRepository.save(cart);

        return cartMapper.toDto(cart);
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
        int deleted = cartRepository.deleteCartByUserId(userId);

        if(deleted == 0) {
            throw new ApiException("Cart not found", HttpStatus.NOT_FOUND);
        }
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
        return Cart.builder()
                .userId(userId)
                .build();
    }

    private void createNewItem(AddItemToCartRequest dto, Cart cart) {
        Product product = getProductByIdAndCheckQuantity(dto.productId(), dto.quantity());
        CartItem newCartItem = CartItem.builder()
                .priceAtAdd(new BigDecimal(product.getPrice()))
                .productName(product.getName())
                .quantity(dto.quantity())
                .productId(dto.productId())
                .cart(cart)
                .productImageUrl(product.getImageUrl())
                .build();

        cart.getCartItems().add(newCartItem);
    }

    private void updateExistingItem(AddItemToCartRequest dto, CartItem cartItem) {
        getProductByIdAndCheckQuantity(dto.productId(), dto.quantity() + cartItem.getQuantity());
        cartItem.setQuantity(cartItem.getQuantity() + dto.quantity());
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
