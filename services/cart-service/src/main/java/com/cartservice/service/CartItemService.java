package com.cartservice.service;

import com.cartservice.dto.CreateCartItemDto;
import com.cartservice.entity.Cart;
import com.cartservice.entity.CartItem;
import com.cartservice.exception.NotFoundException;
import com.cartservice.projection.CartItemForOrderProjection;
import com.cartservice.projection.CartItemProjection;
import com.cartservice.repository.CartItemRepository;
import com.cartservice.service.grpc.GrpcProductService;
import com.shopic.grpc.productservice.ProductDetailsResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;


@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final EntityManager entityManager;
    private final GrpcProductService grpcProductService;


    public List<CartItemProjection> getCartItems(long cartId) {
        return cartItemRepository.findByCart_Id(cartId);
    }

    public CartItem getCartItem(long cartId, long productId) {
        return cartItemRepository.findByCart_IdAndProductId(cartId, productId)
                .orElseThrow(() -> new NotFoundException("Cart item not found"));
    }

    @Transactional
    public void createCartItem(CreateCartItemDto dto) {
        ProductDetailsResponse response = grpcProductService.getProductInfoForCart(dto.productId(), dto.quantity());

        cartItemRepository.findByCart_IdAndProductId(dto.cartId(), dto.productId())
                .ifPresentOrElse(
                        value -> {
                            value.setQuantity(dto.quantity() + value.getQuantity());
                        },
                        () -> {
                            createAndSaveCartItem(dto, response);
                        }
                );
    }

    public int countCartItems(long cartId) {
        return cartItemRepository.countByCart_Id(cartId);
    }

    public void deleteCartItem(long cartId, long productId) {
        int deleted = cartItemRepository.deleteCartItem(cartId, productId);

        if(deleted == 0) {
            throw new NotFoundException("Cart item not found");
        }
    }

    public List<CartItemForOrderProjection> getCartItemsForOrder(long cartId) {
        return cartItemRepository.findCartItemForOrderByCartId(cartId);
    }

    private void createAndSaveCartItem(CreateCartItemDto dto, ProductDetailsResponse response) {
        System.out.println(response.getProductImageUrl());
        CartItem cartItem = CartItem.builder()
                .productId(dto.productId())
                .productName(response.getProductName())
                .productImageUrl(response.getProductImageUrl())
                .cart(entityManager.getReference(Cart.class, dto.cartId()))
                .quantity(dto.quantity())
                .priceAtAdd(new BigDecimal(response.getProductPrice()))
                .build();

        cartItemRepository.save(cartItem);
    }
}
