package com.cartservice.unit.service;

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
import com.cartservice.repository.CartRepository;
import com.cartservice.service.CartItemService;
import com.cartservice.service.CartService;
import com.cartservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.ProductInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemService cartItemService;

    @Spy
    private CartItemMapper cartItemMapper = Mappers.getMapper(CartItemMapper.class);

    @Mock
    private GrpcProductService grpcProductService;

    @InjectMocks
    private CartService cartService;


    private static final long PRODUCT_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long CART_ITEM_ID = 3L;
    private static final String PRODUCT_NAME = "test_product_name";
    private static final String PRODUCT_IMAGE_URL = "test_product_image_url";
    private static final long CART_ID = 4L;
    private static final String PRICE_AT_ADD = "10.0";
    private static final int REQUESTED_QUANTITY = 9;
    private static final AddItemToCartRequest ADD_ITEM_TO_CART_REQUEST = new AddItemToCartRequest(
            PRODUCT_ID,
            REQUESTED_QUANTITY
    );


    private Cart cart;
    private CartItem cartItem;

    @BeforeEach
    public void setUp() {
        cart = Cart.builder()
                .id(CART_ID)
                .userId(USER_ID)
                .build();

        cartItem = CartItem.builder()
                .id(CART_ITEM_ID)
                .productName(PRODUCT_NAME)
                .priceAtAdd(new BigDecimal(PRICE_AT_ADD))
                .productImageUrl(PRODUCT_IMAGE_URL)
                .productId(PRODUCT_ID)
                .cart(cart)
                .build();
    }

    @Test
    public void testAddItemToCart_whenCalledWithExistingCartAndEnoughProductStockAndNonExistingCartItem_thenAddNewItemToCart() {
        ArgumentCaptor<CartItem> cartItemArgumentCaptor = ArgumentCaptor.forClass(CartItem.class);
        int availableQuantity = 10;
        ProductInfo productInfo = ProductInfo.newBuilder()
                .setProductId(PRODUCT_ID)
                .setAvailableQuantity(availableQuantity)
                .setProductName(PRODUCT_NAME)
                .setProductImageUrl(PRODUCT_IMAGE_URL)
                .setPrice(PRICE_AT_ADD)
                .build();

        cartItem.setQuantity(REQUESTED_QUANTITY);

        when(cartRepository.findCartByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(cartItemService.getOptionalByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(grpcProductService.getProductInfo(anyLong())).thenReturn(productInfo);
        when(cartItemService.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemDto result = cartService.addItemToCart(ADD_ITEM_TO_CART_REQUEST, USER_ID);

        verify(cartRepository).findCartByUserId(USER_ID);
        verify(cartItemService).getOptionalByCartIdAndProductId(CART_ID, PRODUCT_ID);
        verify(grpcProductService).getProductInfo(PRODUCT_ID);
        verify(cartItemMapper).toEntity(ADD_ITEM_TO_CART_REQUEST, productInfo, cart);
        verify(cartItemService).save(cartItemArgumentCaptor.capture());
        verify(cartItemMapper).toCartItemDto(cartItem);

        CartItem savedCartItem = cartItemArgumentCaptor.getValue();

        assertEquals(cart, savedCartItem.getCart());
        assertEquals(PRODUCT_ID, savedCartItem.getProductId());
        assertEquals(PRODUCT_NAME, savedCartItem.getProductName());
        assertEquals(PRODUCT_IMAGE_URL, savedCartItem.getProductImageUrl());
        assertEquals(REQUESTED_QUANTITY, savedCartItem.getQuantity());
        assertEquals(new BigDecimal(PRICE_AT_ADD), savedCartItem.getPriceAtAdd());
        assertEquals(PRODUCT_ID, result.productId());
        assertEquals(PRODUCT_NAME, result.productName());
        assertEquals(PRODUCT_IMAGE_URL, result.productImageUrl());
        assertEquals(REQUESTED_QUANTITY, result.quantity());
        assertEquals(new BigDecimal(PRICE_AT_ADD), result.priceAtAdd());
    }


    @Test
    public void testAddItemToCart_whenCalledWithExistingCartAndEnoughProductStockAndExistingCartItem_thenUpdateExistingCartItem() {
        int startItemQuantity = 7;
        int availableQuantity = startItemQuantity + REQUESTED_QUANTITY;
        ProductInfo productInfo = ProductInfo.newBuilder()
                .setProductId(PRODUCT_ID)
                .setAvailableQuantity(availableQuantity)
                .setProductName(PRODUCT_NAME)
                .setProductImageUrl(PRODUCT_IMAGE_URL)
                .setPrice(PRICE_AT_ADD)
                .build();
        cartItem.setQuantity(startItemQuantity);

        when(cartRepository.findCartByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(cartItemService.getOptionalByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(cartItem));
        when(grpcProductService.getProductInfo(anyLong())).thenReturn(productInfo);

        CartItemDto result = cartService.addItemToCart(ADD_ITEM_TO_CART_REQUEST, USER_ID);

        verify(cartRepository).findCartByUserId(USER_ID);
        verify(cartItemService).getOptionalByCartIdAndProductId(CART_ID, PRODUCT_ID);
        verify(grpcProductService).getProductInfo(PRODUCT_ID);
        verify(cartItemMapper).toCartItemDto(cartItem);

        assertEquals(CART_ITEM_ID, result.id());
        assertEquals(PRODUCT_ID, result.productId());
        assertEquals(PRODUCT_NAME, result.productName());
        assertEquals(PRODUCT_IMAGE_URL, result.productImageUrl());
        assertEquals(new BigDecimal(PRICE_AT_ADD), result.priceAtAdd());
        assertEquals(startItemQuantity + REQUESTED_QUANTITY, result.quantity());
    }

    @Test
    public void testAddItemToCart_whenCalledWithExistingCartAndNotEnoughProductStockAndExistingCartItem_thenUpdateExistingCartItem() {
        int startItemQuantity = 7;
        int availableQuantity = startItemQuantity + REQUESTED_QUANTITY - 2;

        ProductInfo productInfo = ProductInfo.newBuilder()
                .setProductId(PRODUCT_ID)
                .setAvailableQuantity(availableQuantity)
                .setProductName(PRODUCT_NAME)
                .setProductImageUrl(PRODUCT_IMAGE_URL)
                .setPrice(PRICE_AT_ADD)
                .build();

        cartItem.setQuantity(startItemQuantity);

        when(cartRepository.findCartByUserId(anyLong())).thenReturn(Optional.of(cart));
        when(cartItemService.getOptionalByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(cartItem));
        when(grpcProductService.getProductInfo(anyLong())).thenReturn(productInfo);

        assertThrows(InsufficientProductStockException.class, () -> {
            cartService.addItemToCart(ADD_ITEM_TO_CART_REQUEST, USER_ID);
        });

        verify(cartRepository).findCartByUserId(USER_ID);
        verify(cartItemService).getOptionalByCartIdAndProductId(CART_ID, PRODUCT_ID);
        verify(grpcProductService).getProductInfo(PRODUCT_ID);
        verifyNoInteractions(cartItemMapper);
    }

    @Test
    public void testAddItemToCart_whenCalledWithNonExistingCartAndEnoughProductStockAndExistingCartItem_thenCreateCartAndUpdateExistingCartItem() {
        int startItemQuantity = 7;
        int availableQuantity = startItemQuantity + REQUESTED_QUANTITY;
        ArgumentCaptor<Cart> cartCaptor = ArgumentCaptor.forClass(Cart.class);
        ProductInfo productInfo = ProductInfo.newBuilder()
                .setProductId(PRODUCT_ID)
                .setAvailableQuantity(availableQuantity)
                .setProductName(PRODUCT_NAME)
                .setProductImageUrl(PRODUCT_IMAGE_URL)
                .setPrice(PRICE_AT_ADD)
                .build();
        cartItem.setQuantity(startItemQuantity);

        when(cartRepository.findCartByUserId(anyLong())).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartItemService.getOptionalByCartIdAndProductId(anyLong(), anyLong())).thenReturn(Optional.of(cartItem));
        when(grpcProductService.getProductInfo(anyLong())).thenReturn(productInfo);

        CartItemDto result = cartService.addItemToCart(ADD_ITEM_TO_CART_REQUEST, USER_ID);

        verify(cartRepository).findCartByUserId(USER_ID);
        verify(cartRepository).save(cartCaptor.capture());
        verify(cartItemService).getOptionalByCartIdAndProductId(CART_ID, PRODUCT_ID);
        verify(grpcProductService).getProductInfo(PRODUCT_ID);
        verify(cartItemMapper).toCartItemDto(cartItem);

        Cart savedCart = cartCaptor.getValue();

        assertEquals(CART_ITEM_ID, result.id());
        assertEquals(PRODUCT_ID, result.productId());
        assertEquals(PRODUCT_NAME, result.productName());
        assertEquals(PRODUCT_IMAGE_URL, result.productImageUrl());
        assertEquals(new BigDecimal(PRICE_AT_ADD), result.priceAtAdd());
        assertEquals(startItemQuantity + REQUESTED_QUANTITY, result.quantity());
        assertEquals(USER_ID, savedCart.getUserId());
    }

    @Test
    public void testGetCart_whenCalledWithNonExistingCart_thenReturnEmptyCartDto() {
        when(cartRepository.findCartWithItemsByUserId(anyLong())).thenReturn(Optional.empty());

        CartDto result = cartService.getCart(USER_ID);

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verifyNoInteractions(cartItemMapper);

        assertEquals(Collections.emptyList(), result.cartItemList());
        assertEquals(BigDecimal.ZERO, result.totalPrice());
    }

    @Test
    public void testGetCart_whenCalledWithExistingCart_thenReturnCartDtoWithItems() {
        List<CartItem> cartItemList = List.of(cartItem);
        int itemQuantity = REQUESTED_QUANTITY;

        cart.setCartItems(cartItemList);
        cartItem.setQuantity(itemQuantity);

        when(cartRepository.findCartWithItemsByUserId(anyLong())).thenReturn(Optional.of(cart));

        CartDto result = cartService.getCart(USER_ID);
        CartItemDto cartItemDto = result.cartItemList().get(0);

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verify(cartItemMapper).toCartItemDtoList(cartItemList);

        assertEquals(new BigDecimal(PRICE_AT_ADD).multiply(BigDecimal.valueOf(itemQuantity)), result.totalPrice());
        assertEquals(cartItem.getQuantity(), cartItemDto.quantity());
        assertEquals(cartItem.getProductId(), cartItemDto.productId());
        assertEquals(cartItem.getProductName(), cartItemDto.productName());
        assertEquals(cartItem.getProductImageUrl(), cartItemDto.productImageUrl());
        assertEquals(cartItem.getPriceAtAdd(), cartItemDto.priceAtAdd());
        assertEquals(cartItem.getId(), cartItemDto.id());
    }

    @Test
    public void testGetCartItemsForOrder_whenCalledWithExistingCart_thenReturnCartItems() {
        List<CartItem> cartItemList = List.of(cartItem);
        cart.setCartItems(cartItemList);

        when(cartRepository.findCartWithItemsByUserId(anyLong())).thenReturn(Optional.of(cart));

        List<CartItemDtoForOrder> result = cartService.getCartItemsForOrder(USER_ID);
        CartItemDtoForOrder cartItemDtoForOrder = result.get(0);

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verify(cartItemMapper).toCartItemDtoListForOrder(cartItemList);

        assertEquals(cartItemList.size(), result.size());
        assertEquals(cartItem.getProductId(), cartItemDtoForOrder.productId());
        assertEquals(cartItem.getQuantity(), cartItemDtoForOrder.quantity());
    }

    @Test
    public void testGetCartItemsForOrder_whenCalledWithNonExistingCart_thenThrowException() {
        when(cartRepository.findCartWithItemsByUserId(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            cartService.getCartItemsForOrder(USER_ID);
        });

        verify(cartRepository).findCartWithItemsByUserId(USER_ID);
        verifyNoInteractions(cartItemMapper);
    }

    @Test
    public void testDeleteItemFromCart_whenCalledWithExistingCartItemAndEmptyCartAfterItemRemoving_thenDeleteItemAndCart() {
        when(cartItemService.getCartIdFromCartItem(anyLong())).thenReturn(CART_ID);
        when(cartItemService.countCartItems(anyLong())).thenReturn(0);

        cartService.deleteItemFromCart(CART_ITEM_ID);

        verify(cartItemService).getCartIdFromCartItem(CART_ITEM_ID);
        verify(cartItemService).deleteCartItemById(CART_ITEM_ID);
        verify(cartItemService).countCartItems(CART_ID);
        verify(cartRepository).deleteById(CART_ID);
    }

    @Test
    public void testDeleteItemFromCart_whenCalledWithExistingCartItemAndNotEmptyCartAfterItemRemoving_thenDeleteItem() {
        when(cartItemService.getCartIdFromCartItem(anyLong())).thenReturn(CART_ID);
        when(cartItemService.countCartItems(anyLong())).thenReturn(1);

        cartService.deleteItemFromCart(CART_ITEM_ID);

        verify(cartItemService).getCartIdFromCartItem(CART_ITEM_ID);
        verify(cartItemService).deleteCartItemById(CART_ITEM_ID);
        verify(cartItemService).countCartItems(CART_ID);
        verifyNoInteractions(cartRepository);
    }

    @Test
    public void testChangeCartItemQuantity_whenRequestedAboveZeroRequestedQuantity_thenSetQuantity() {
        int startItemQuantity = 10;
        ChangeCartItemQuantityRequest changeCartItemQuantity = new ChangeCartItemQuantityRequest(REQUESTED_QUANTITY, CART_ITEM_ID);

        cartItem.setQuantity(startItemQuantity);

        when(cartItemService.getCartItemById(anyLong())).thenReturn(cartItem);

        cartService.changeCartItemQuantity(changeCartItemQuantity);

        verify(cartItemService).getCartItemById(CART_ITEM_ID);
        verifyNoMoreInteractions(cartItemService);
        verifyNoInteractions(cartRepository);

        assertNotEquals(startItemQuantity, cartItem.getQuantity());
        assertEquals(REQUESTED_QUANTITY, cartItem.getQuantity());
    }

    @Test
    public void testChangeCartItemQuantity_whenRequestedBelowZeroRequestedQuantity_thenDeleteItem() {
        int startItemQuantity = 10;
        ChangeCartItemQuantityRequest changeCartItemQuantity = new ChangeCartItemQuantityRequest(0, CART_ITEM_ID);

        cartItem.setQuantity(startItemQuantity);

        when(cartItemService.getCartItemById(anyLong())).thenReturn(cartItem);
        when(cartItemService.countCartItems(anyLong())).thenReturn(1);

        cartService.changeCartItemQuantity(changeCartItemQuantity);

        verify(cartItemService).getCartItemById(CART_ITEM_ID);
        verify(cartItemService).deleteCartItemById(CART_ITEM_ID);
        verify(cartItemService).countCartItems(CART_ID);
    }

    @Test
    public void testDeleteCartByUserId_whenCalledWithArguments_thenDeleteCart() {
        cartService.deleteCartByUserId(USER_ID);

        verify(cartRepository).deleteCartByUserId(USER_ID);
    }
}
