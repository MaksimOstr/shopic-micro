package com.cartservice.service;

import com.cartservice.dto.CartDto;
import com.cartservice.dto.CartItemDto;
import com.cartservice.dto.CartItemDtoForOrder;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.dto.request.ChangeCartItemQuantityRequest;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.cartservice.exception.InsufficientProductStockException;
import com.cartservice.exception.NotFoundException;
import com.cartservice.mapper.CartItemMapper;
import com.cartservice.service.CartItemService;
import com.cartservice.repository.CartRepository;
import com.cartservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.ProductInfo;
import lombok.RequiredArgsConstructor;
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
    private final CartItemService cartItemService;
    private final CartItemMapper cartItemMapper;
    private final GrpcProductService grpcProductService;

    private static final String CART_NOT_FOUND = "Cart Not Found";

    @Transactional
    public CartItemDto addItemToCart(AddItemToCartRequest dto, UUID userId) {
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseGet(() -> createCart(userId));

        Optional<CartItem> cartItemOptional = cartItemService.getOptionalByCartIdAndProductId(cart.getId(), dto.productId());

        if (cartItemOptional.isPresent()) {
            return updateExistingItem(dto, cartItemOptional.get());
        } else {
            return createNewItem(dto, cart);
        }
    }

    @Transactional(readOnly = true)
    public CartDto getCart(UUID userId) {
        return cartRepository.findCartWithItemsByUserId(userId)
                .map(cart -> {
                    List<CartItemDto> cartItemList = cartItemMapper.toCartItemDtoList(cart.getCartItems());
                    return new CartDto(
                            cartItemList,
                            cart.calculateTotal()
                    );
                })
                .orElseGet(this::createEmptyCartDto);
    }

    @Transactional(readOnly = true)
    public List<CartItemDtoForOrder> getCartItemsForOrder(UUID userId) {
        Cart cart = cartRepository.findCartWithItemsByUserId(userId)
                .orElseThrow(() -> new NotFoundException(CART_NOT_FOUND));

        return cartItemMapper.toCartItemDtoListForOrder(cart.getCartItems());
    }

    public void deleteItemFromCart(UUID itemId, UUID userId) {
        UUID cartId = cartRepository.findCartIdByUserId(userId)
                .orElseThrow(() -> new NotFoundException(CART_NOT_FOUND));

        cartItemService.deleteCartItem(itemId, cartId);
        deleteCartIfEmpty(cartId);
    }

    @Transactional
    public void changeCartItemQuantity(ChangeCartItemQuantityRequest dto, UUID itemId) {
        CartItem cartItem = cartItemService.getCartItemById(itemId);

        if (dto.amount() <= 0) {
            cartItemService.deleteCartItem(cartItem.getId());
            deleteCartIfEmpty(cartItem.getCart().getId());
        } else {
            cartItem.setQuantity(dto.amount());
        }
    }

    public void deleteCartByUserId(UUID userId) {
        cartRepository.deleteCartByUserId(userId);
    }

    private void deleteCartIfEmpty(UUID cartId) {
        int cartItemsCount = cartItemService.countCartItems(cartId);
        if (cartItemsCount == 0) {
            cartRepository.deleteById(cartId);
        }
    }

    private Cart createCart(UUID userId) {
        Cart cart = Cart.builder()
                .userId(userId)
                .build();

        return cartRepository.save(cart);
    }

    private CartDto createEmptyCartDto() {
        return new CartDto(
                Collections.emptyList(),
                BigDecimal.ZERO
        );
    }

    private CartItemDto createNewItem(AddItemToCartRequest dto, Cart cart) {
        ProductInfo response = getProductInfoAndCheckQuantity(dto.productId(), dto.quantity());
        CartItem cartItem = cartItemMapper.toEntity(dto, response, cart);
        CartItem savedCartItem = cartItemService.save(cartItem);
        return cartItemMapper.toCartItemDto(savedCartItem);
    }

    private CartItemDto updateExistingItem(AddItemToCartRequest dto, CartItem cartItem) {
        getProductInfoAndCheckQuantity(dto.productId(), dto.quantity() + cartItem.getQuantity());
        cartItem.setQuantity(cartItem.getQuantity() + dto.quantity());
        return cartItemMapper.toCartItemDto(cartItem);
    }

    private ProductInfo getProductInfoAndCheckQuantity(long productId, int quantity) {
        ProductInfo productInfo = grpcProductService.getProductInfo(productId);

        if (productInfo.getAvailableQuantity() < quantity) {
            throw new InsufficientProductStockException(
                    "Insufficient stock for product. Requested: %d, Available: %d"
                            .formatted(quantity, productInfo.getAvailableQuantity())
            );
        }

        return productInfo;
    }
}
