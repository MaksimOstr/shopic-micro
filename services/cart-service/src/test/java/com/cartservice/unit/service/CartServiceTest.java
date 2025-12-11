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
import com.shopic.grpc.productservice.ProductInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID CART_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID CART_ITEM_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID PRODUCT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID OTHER_PRODUCT_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final BigDecimal PRICE_AT_ADD = new BigDecimal("10.50");
    private static final String PRODUCT_NAME = "Test product name";
    private static final String PRODUCT_IMAGE_URL = "http://image.png";

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private GrpcProductService grpcProductService;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        cart = Cart.builder()
                .id(CART_ID)
                .userId(USER_ID)
                .cartItems(new ArrayList<>())
                .build();

        cartItem = CartItem.builder()
                .id(CART_ITEM_ID)
                .cart(cart)
                .productId(PRODUCT_ID)
                .priceAtAdd(PRICE_AT_ADD)
                .quantity(1)
                .productName(PRODUCT_NAME)
                .productImageUrl(PRODUCT_IMAGE_URL)
                .build();
    }

    @Test
    void addItemToCart_updatesExistingItemQuantity() {
        AddItemToCartRequest request = new AddItemToCartRequest(PRODUCT_ID, 2);
        cart.getCartItems().add(cartItem);
        CartItemDto expectedDto = new CartItemDto(CART_ITEM_ID, PRODUCT_ID, 3, PRICE_AT_ADD, PRODUCT_NAME, PRODUCT_IMAGE_URL);

        ProductInfo productInfo = ProductInfo.newBuilder()
                .setProductId(PRODUCT_ID.toString())
                .setAvailableQuantity(10)
                .setPrice(PRICE_AT_ADD.toString())
                .setProductName(PRODUCT_NAME)
                .setProductImageUrl(PRODUCT_IMAGE_URL)
                .build();

        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(grpcProductService.getProductInfo(PRODUCT_ID)).thenReturn(productInfo);
        when(cartItemMapper.toDto(cartItem)).thenReturn(expectedDto);

        CartItemDto result = cartService.addItemToCart(request, USER_ID);

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verify(grpcProductService).getProductInfo(PRODUCT_ID);
        verify(cartItemMapper).toDto(cartItem);

        assertEquals(3, cartItem.getQuantity());
        assertSame(expectedDto, result);
    }

    @Test
    void addItemToCart_createsCartWhenMissing() {
        AddItemToCartRequest request = new AddItemToCartRequest(OTHER_PRODUCT_ID, 1);
        Cart savedCart = Cart.builder()
                .id(CART_ID)
                .userId(USER_ID)
                .cartItems(new ArrayList<>())
                .build();

        ProductInfo productInfo = ProductInfo.newBuilder()
                .setProductId(OTHER_PRODUCT_ID.toString())
                .setAvailableQuantity(5)
                .setPrice(PRICE_AT_ADD.toString())
                .setProductName(PRODUCT_NAME)
                .setProductImageUrl(PRODUCT_IMAGE_URL)
                .build();

        CartItemDto mappedDto = new CartItemDto(null, OTHER_PRODUCT_ID, 1, PRICE_AT_ADD, PRODUCT_NAME, PRODUCT_IMAGE_URL);

        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);
        when(grpcProductService.getProductInfo(OTHER_PRODUCT_ID)).thenReturn(productInfo);
        when(cartItemMapper.toDto(any(CartItem.class))).thenReturn(mappedDto);

        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        ArgumentCaptor<CartItem> cartItemCaptor = ArgumentCaptor.forClass(CartItem.class);

        CartItemDto result = cartService.addItemToCart(request, USER_ID);

        verify(cartRepository).save(cartCaptor.capture());
        verify(grpcProductService).getProductInfo(OTHER_PRODUCT_ID);
        verify(cartItemMapper).toDto(cartItemCaptor.capture());

        Cart saved = cartCaptor.getValue();
        CartItem newItem = cartItemCaptor.getValue();

        assertEquals(USER_ID, saved.getUserId());
        assertEquals(OTHER_PRODUCT_ID, newItem.getProductId());
        assertEquals(1, newItem.getQuantity());
        assertEquals(PRICE_AT_ADD, newItem.getPriceAtAdd());
        assertEquals(PRODUCT_NAME, newItem.getProductName());
        assertEquals(PRODUCT_IMAGE_URL, newItem.getProductImageUrl());
        assertSame(mappedDto, result);
    }

    @Test
    void addItemToCart_throwsApiExceptionWhenStockIsInsufficient() {
        AddItemToCartRequest request = new AddItemToCartRequest(PRODUCT_ID, 3);
        ProductInfo productInfo = ProductInfo.newBuilder()
                .setProductId(PRODUCT_ID.toString())
                .setAvailableQuantity(2)
                .setPrice(PRICE_AT_ADD.toString())
                .setProductName(PRODUCT_NAME)
                .setProductImageUrl(PRODUCT_IMAGE_URL)
                .build();

        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(grpcProductService.getProductInfo(PRODUCT_ID)).thenReturn(productInfo);

        ApiException exception = assertThrows(ApiException.class, () -> cartService.addItemToCart(request, USER_ID));

        verify(grpcProductService).getProductInfo(PRODUCT_ID);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void getCart_returnsEmptyDtoWhenCartMissing() {
        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.empty());

        CartDto result = cartService.getCart(USER_ID);

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        assertEquals(new CartDto(Collections.emptyList(), BigDecimal.ZERO), result);
        verifyNoInteractions(cartMapper);
    }

    @Test
    void getCart_returnsMappedDtoWhenCartExists() {
        CartDto mappedDto = new CartDto(Collections.emptyList(), BigDecimal.TEN);
        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(cartMapper.toDto(cart)).thenReturn(mappedDto);

        CartDto result = cartService.getCart(USER_ID);

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verify(cartMapper).toDto(cart);
        assertSame(mappedDto, result);
    }

    @Test
    void getCartItemsForOrder_returnsMappedItems() {
        cart.getCartItems().add(cartItem);
        List<CartItemDto> expected = List.of(new CartItemDto(CART_ITEM_ID, PRODUCT_ID, 1, PRICE_AT_ADD, PRODUCT_NAME, PRODUCT_IMAGE_URL));

        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(cartItemMapper.toDtoList(cart.getCartItems())).thenReturn(expected);

        List<CartItemDto> result = cartService.getCartItemsForOrder(USER_ID);

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verify(cartItemMapper).toDtoList(cart.getCartItems());
        assertEquals(expected, result);
    }

    @Test
    void getCartItemsForOrder_throwsWhenCartMissing() {
        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> cartService.getCartItemsForOrder(USER_ID));
        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
    }

    @Test
    void deleteItemFromCart_removesItemAndDeletesCartWhenEmpty() {
        cart.getCartItems().add(cartItem);
        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.of(cart));

        cartService.deleteItemFromCart(CART_ITEM_ID, USER_ID);

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verify(cartRepository).delete(cart);
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void deleteItemFromCart_throwsWhenItemNotFound() {
        cart.getCartItems().add(cartItem);
        UUID missingItemId = UUID.randomUUID();
        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.of(cart));

        ApiException exception = assertThrows(ApiException.class, () -> cartService.deleteItemFromCart(missingItemId, USER_ID));

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verify(cartRepository, never()).delete(any(Cart.class));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals(1, cart.getCartItems().size());
    }

    @Test
    void updateCartItem_setsQuantityWhenPositive() {
        cart.getCartItems().add(cartItem);
        ChangeCartItemQuantityRequest request = new ChangeCartItemQuantityRequest(5);
        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.of(cart));

        cartService.updateCartItem(request, CART_ITEM_ID, USER_ID);

        assertEquals(5, cartItem.getQuantity());
        verify(cartRepository, never()).delete(any(Cart.class));
    }

    @Test
    void updateCartItem_removesItemAndDeletesCartWhenAmountIsZero() {
        cart.getCartItems().add(cartItem);
        ChangeCartItemQuantityRequest request = new ChangeCartItemQuantityRequest(0);
        when(cartRepository.findCartWithItemsByUserId(USER_ID)).thenReturn(Optional.of(cart));

        cartService.updateCartItem(request, CART_ITEM_ID, USER_ID);

        verify(cartRepository).delete(cart);
        assertTrue(cart.getCartItems().isEmpty());
    }

    @Test
    void deleteCartByUserId_delegatesToRepository() {
        cartService.deleteCartByUserId(USER_ID);

        verify(cartRepository).deleteCartByUserId(USER_ID);
    }
}
