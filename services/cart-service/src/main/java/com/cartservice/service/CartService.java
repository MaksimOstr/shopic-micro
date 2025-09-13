package com.cartservice.service;

import com.cartservice.dto.CartDto;
import com.cartservice.dto.CartItemDto;
import com.cartservice.dto.CartItemDtoForOrder;
import com.cartservice.dto.request.AddItemToCartRequest;
import com.cartservice.dto.request.ChangeCartItemQuantity;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.cartservice.exception.NotFoundException;
import com.cartservice.mapper.CartItemMapper;
import com.cartservice.repository.CartRepository;
import com.cartservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.ProductDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemService cartItemService;
    private final CartItemMapper cartItemMapper;
    private final GrpcProductService grpcProductService;

    private static final String CART_NOT_FOUND = "Cart Not Found";

    @Transactional
    public void addItemToCart(AddItemToCartRequest dto, long userId) {
        Cart cart = cartRepository.findCartByUserId(userId)
                .orElseGet(() -> createCart(userId));

        Optional<CartItem> cartItemOptional = cartItemService.getByCartIdAndProductId(cart.getId(), dto.productId());

        if(cartItemOptional.isPresent()) {
            CartItem item = cartItemOptional.get();
            item.setQuantity(item.getQuantity() + dto.quantity());
        } else {
            CartItem item = createCartItem(dto, cart);
            cart.getCartItems().add(item);
        }
    }

    @Transactional(readOnly = true)
    public CartDto getCart(long userId) {
        return cartRepository.findCartWithItemsByUserId(userId)
                .map(cart -> {
                    List<CartItemDto> cartItemList = cartItemMapper.toCartItemDtoList(cart.getCartItems());
                    return new  CartDto(
                            cartItemList,
                            cart.calculateTotal()
                    );
                })
                .orElseGet(this::createEmptyCartDto);
    }

    @Transactional(readOnly = true)
    public List<CartItemDtoForOrder> getCartItemsForOrder(long userId) {
        Cart cart = cartRepository.findCartWithItemsByUserId(userId)
                .orElseThrow(() -> new NotFoundException(CART_NOT_FOUND));

        return cartItemMapper.toCartItemDtoListForOrder(cart.getCartItems());
    }

    public void removeItemFromCart(long cartItemId) {
        long cartId = cartItemService.getCartIdFromCartItem(cartItemId);

        cartItemService.deleteCartItemById(cartItemId);
        deleteCartIfEmpty(cartId);
    }

    @Transactional
    public void changeCartItemQuantity(ChangeCartItemQuantity dto) {
        CartItem cartItem = cartItemService.getCartItemById(dto.cartItemId());

        if (dto.amount() <= 0) {
            cartItemService.deleteCartItemById(cartItem.getId());
            deleteCartIfEmpty(cartItem.getCart().getId());
        } else {
            cartItem.setQuantity(dto.amount());
        }
    }

    public void deleteCartByUserId(long userId) {
        int deleted = cartRepository.deleteCartByUserId(userId);

        if (deleted == 0) {
            throw new NotFoundException(CART_NOT_FOUND);
        }
    }

    private void deleteCartIfEmpty(long cartId) {
        int cartItemsCount = cartItemService.countCartItems(cartId);
        if (cartItemsCount == 0) {
            cartRepository.deleteById(cartId);
        }
    }

    private Cart createCart(long userId) {
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

    private CartItem createCartItem(AddItemToCartRequest dto, Cart cart) {
        ProductDetailsResponse response = grpcProductService.getProductInfoForCart(dto.productId(), dto.quantity());

        return CartItem.builder()
                .productId(dto.productId())
                .productName(response.getProductName())
                .productImageUrl(response.getProductImageUrl())
                .cart(cart)
                .quantity(dto.quantity())
                .priceAtAdd(new BigDecimal(response.getProductPrice()))
                .build();
    }
}
