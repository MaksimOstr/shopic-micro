package com.cartservice.unit.service;

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
import com.cartservice.service.CartService;
import com.cartservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartRepository cartRepository;
    @Mock CartItemMapper cartItemMapper;
    @Mock CartMapper cartMapper;
    @Mock GrpcProductService grpcProductService;

    @InjectMocks CartService cartService;

    Cart cart;
    CartItem cartItem;
    Product product;
    CartItemDto cartItemDto;

    @BeforeEach
    void setup() {
        cart = Cart.builder()
                .userId(UUID.randomUUID())
                .cartItems(new ArrayList<>())
                .build();

        cartItem = CartItem.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .quantity(2)
                .priceAtAdd(BigDecimal.TEN)
                .productName("p")
                .productImageUrl("img")
                .cart(cart)
                .build();

        product = Product.newBuilder()
                .setAvailableQuantity(10)
                .setPrice("100")
                .setName("p")
                .setImageUrl("img")
                .build();

        cartItemDto = new CartItemDto(
                cartItem.getId(),
                cartItem.getProductId(),
                cartItem.getQuantity(),
                cartItem.getPriceAtAdd(),
                cartItem.getProductName(),
                cartItem.getProductImageUrl()
        );
    }

    @Test
    void addItemToCart_createsCartAndItem_whenCartDoesNotExist() {
        UUID userId = UUID.randomUUID();

        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.empty());
        when(grpcProductService.getProductById(cartItem.getProductId())).thenReturn(product);

        when(cartMapper.toDto(argThat(c -> c.getUserId().equals(userId))))
                .thenReturn(new CartDto(List.of(cartItemDto), BigDecimal.valueOf(200)));

        cartService.addItemToCart(new AddItemToCartRequest(cartItem.getProductId(), 2), userId);

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());

        Cart saved = captor.getValue();

        assertEquals(userId, saved.getUserId());
        assertEquals(1, saved.getCartItems().size());

        CartItem savedItem = saved.getCartItems().get(0);
        assertEquals(cartItem.getProductId(), savedItem.getProductId());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(new BigDecimal("100"), savedItem.getPriceAtAdd());
        assertEquals("p", savedItem.getProductName());
        assertEquals("img", savedItem.getProductImageUrl());

        verify(grpcProductService).getProductById(cartItem.getProductId());
    }

    @Test
    void addItemToCart_incrementsQuantity_whenItemAlreadyExists() {
        UUID userId = UUID.randomUUID();
        cart.getCartItems().add(cartItem);
        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(grpcProductService.getProductById(cartItem.getProductId())).thenReturn(product);

        cartService.addItemToCart(new AddItemToCartRequest(cartItem.getProductId(), 3), userId);

        assertEquals(5, cartItem.getQuantity());
        assertEquals(cart, cartItem.getCart());
        verify(cartRepository).save(cart);
    }

    @Test
    void addItemToCart_addsNewItem_whenCartExistsAndItemDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(grpcProductService.getProductById(cartItem.getProductId())).thenReturn(product);

        cartService.addItemToCart(new AddItemToCartRequest(cartItem.getProductId(), 1), userId);

        assertEquals(1, cart.getCartItems().size());
        CartItem newItem = cart.getCartItems().get(0);

        assertEquals(cartItem.getProductId(), newItem.getProductId());
        assertEquals(1, newItem.getQuantity());
        assertEquals(new BigDecimal("100"), newItem.getPriceAtAdd());
        assertEquals("p", newItem.getProductName());
        assertEquals("img", newItem.getProductImageUrl());
    }

    @Test
    void addItemToCart_throwsException_whenInsufficientStock() {
        UUID userId = UUID.randomUUID();
        product = product.toBuilder().setAvailableQuantity(1).build();
        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.empty());
        when(grpcProductService.getProductById(cartItem.getProductId())).thenReturn(product);

        ApiException ex = assertThrows(ApiException.class,
                () -> cartService.addItemToCart(new AddItemToCartRequest(cartItem.getProductId(), 5), userId));

        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(cartRepository, never()).save(cart);
    }

    @Test
    void getCart_returnsCart_whenCartExists() {
        UUID userId = UUID.randomUUID();
        cart.getCartItems().add(cartItem);
        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(new CartDto(List.of(cartItemDto), BigDecimal.TEN));

        CartDto dto = cartService.getCart(userId);

        assertEquals(1, dto.cartItemList().size());
        assertEquals(cartItem.getId(), dto.cartItemList().get(0).id());
        assertEquals(BigDecimal.TEN, dto.totalPrice());
    }

    @Test
    void getCart_returnsEmptyCart_whenCartDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.empty());

        CartDto dto = cartService.getCart(userId);

        assertTrue(dto.cartItemList().isEmpty());
        assertEquals(BigDecimal.ZERO, dto.totalPrice());
    }

    @Test
    void getCartItemsForOrder_returnsItems_whenCartExists() {
        UUID userId = UUID.randomUUID();
        cart.getCartItems().add(cartItem);
        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.of(cart));
        when(cartItemMapper.toDtoList(cart.getCartItems())).thenReturn(List.of(cartItemDto));

        List<CartItemDto> items = cartService.getCartItemsForOrder(userId);

        assertEquals(1, items.size());
        assertEquals(cartItem.getId(), items.get(0).id());
        assertEquals(cartItem.getProductId(), items.get(0).productId());
    }

    @Test
    void deleteItemFromCart_removesItemAndDeletesCart_whenLastItem() {
        UUID userId = UUID.randomUUID();
        cart.getCartItems().add(cartItem);
        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.deleteItemFromCart(cartItem.getId(), userId);

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).delete(cart);
    }

    @Test
    void updateCartItem_removesItemAndDeletesCartWhenAmountIsZero() {
        UUID userId = UUID.randomUUID();
        cart.getCartItems().add(cartItem);
        when(cartRepository.findCartWithItemsByUserId(userId)).thenReturn(Optional.of(cart));

        cartService.updateCartItem(new ChangeCartItemQuantityRequest(0), cartItem.getId(), userId);

        assertTrue(cart.getCartItems().isEmpty());
        verify(cartRepository).delete(cart);
    }
}
